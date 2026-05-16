using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.EntityFrameworkCore;
using NfpDirect.Data;
using NfpDirect.Models;

namespace NfpDirect.Pages;

public class IndexEntry
{
    public required Pub78 Pub78 { get; init; }
    public Postcard? Postcard { get; init; }
}

public class IndexModel : PageModel
{
    private readonly NfpDbContext _db;
    private const int PageSize = 50;

    public IndexModel(NfpDbContext db) => _db = db;

    public int PageNumber { get; private set; }
    public bool HasNextPage { get; private set; }
    public string StateFilter { get; private set; } = "";
    public List<IndexEntry> Entries { get; private set; } = [];

    public async Task<IActionResult> OnGetAsync([FromQuery] string? page, [FromQuery] string? state)
    {
        if (!int.TryParse(page, out var pageNum) || pageNum < 0)
            pageNum = 0;
        PageNumber = pageNum;
        StateFilter = (state ?? "").Trim().ToUpper();

        var queryable = _db.Pub78.AsNoTracking();
        if (StateFilter.Length > 0)
            queryable = queryable.Where(p => p.State == StateFilter);

        var pub78Page = await queryable
            .OrderBy(p => p.CompanyName)
            .ThenBy(p => p.EIN)
            .Skip(PageNumber * PageSize)
            .Take(PageSize)
            .ToListAsync();

        var eins = pub78Page.Select(p => p.EIN).ToHashSet();
        var postcards = await _db.Postcards
            .Where(pc => eins.Contains(pc.Id))
            .AsNoTracking()
            .ToDictionaryAsync(pc => pc.Id);

        Entries = pub78Page.Select(p => new IndexEntry
        {
            Pub78 = p,
            Postcard = postcards.GetValueOrDefault(p.EIN)
        }).ToList();

        HasNextPage = pub78Page.Count == PageSize;

        return Page();
    }
}
