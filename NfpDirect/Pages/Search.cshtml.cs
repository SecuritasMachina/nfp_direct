using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.EntityFrameworkCore;
using NfpDirect.Data;
using NfpDirect.Models;

namespace NfpDirect.Pages;

public class SearchModel : PageModel
{
    private readonly NfpDbContext _db;
    private const int PageSize = 50;

    public SearchModel(NfpDbContext db) => _db = db;

    public string Query { get; private set; } = "";
    public string StateFilter { get; private set; } = "";
    public int PageNumber { get; private set; }
    public int TotalResults { get; private set; }
    public int TotalPages { get; private set; }
    public List<SearchResult> Results { get; private set; } = [];

    public async Task<IActionResult> OnGetAsync(
        [FromQuery] string? q,
        [FromQuery] string? state,
        [FromQuery] string? page)
    {
        Query = (q ?? "").Trim();
        StateFilter = (state ?? "").Trim().ToUpper();
        ViewData["SearchQuery"] = Query;

        if (Query.Length == 0 && StateFilter.Length == 0)
            return Page();

        if (!int.TryParse(page, out var pageNum) || pageNum < 0)
            pageNum = 0;
        PageNumber = pageNum;

        var queryable = _db.Pub78.AsNoTracking();
        var normalizedEin = Query.Replace("-", "");

        if (IsEinLike(Query))
        {
            queryable = queryable.Where(p => p.EIN == normalizedEin);
        }
        else if (Query.Length > 0)
        {
            var useFullText = Query.Length >= 4 &&
                _db.Database.ProviderName?.Contains("InMemory") != true;

            if (useFullText)
            {
                var sanitized = new string(Query
                    .Where(c => char.IsLetterOrDigit(c) || c == ' ').ToArray()).Trim();
                var ftQuery = string.Join(" ", sanitized
                    .Split(' ', StringSplitOptions.RemoveEmptyEntries)
                    .Select(w => $"+{w}*"));

                if (ftQuery.Length > 0)
                    queryable = _db.Pub78
                        .FromSqlRaw(
                            "SELECT * FROM pub78 WHERE MATCH(CompanyName) AGAINST({0} IN BOOLEAN MODE)",
                            ftQuery)
                        .AsNoTracking();
                else
                    queryable = queryable.Where(p => p.CompanyName != null && p.CompanyName.Contains(Query));
            }
            else
            {
                queryable = queryable.Where(p =>
                    p.CompanyName != null && p.CompanyName.Contains(Query));
            }
        }

        if (StateFilter.Length > 0)
            queryable = queryable.Where(p => p.State == StateFilter);

        queryable = queryable.OrderBy(p => p.CompanyName).ThenBy(p => p.EIN);

        TotalResults = await queryable.CountAsync();
        TotalPages = (TotalResults + PageSize - 1) / PageSize;

        // EIN exact-match with one result: skip the list and jump directly to detail
        if (IsEinLike(Query) && TotalResults == 1)
        {
            var match = await queryable.FirstAsync();
            var backUrl = "/Search?q=" + Uri.EscapeDataString(Query);
            return RedirectToPage("/Details", new { id = match.EIN, back = backUrl });
        }

        var pub78Page = await queryable
            .Skip(pageNum * PageSize)
            .Take(PageSize)
            .ToListAsync();

        var eins = pub78Page.Select(p => p.EIN).ToHashSet();
        var postcards = await _db.Postcards
            .AsNoTracking()
            .Where(pc => eins.Contains(pc.Id))
            .ToDictionaryAsync(pc => pc.Id);

        Results = pub78Page.Select(p => new SearchResult
        {
            Pub78 = p,
            Postcard = postcards.GetValueOrDefault(p.EIN)
        }).ToList();

        return Page();
    }

    // Heuristic: treat all-digit strings (with optional hyphens) as EIN lookups
    private static bool IsEinLike(string q) =>
        q.Replace("-", "").All(char.IsDigit) && q.Replace("-", "").Length == 9;
}

public record SearchResult
{
    public required Pub78 Pub78 { get; init; }
    public Postcard? Postcard { get; init; }
}
