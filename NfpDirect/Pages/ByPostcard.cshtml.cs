using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.EntityFrameworkCore;
using NfpDirect.Data;
using NfpDirect.Models;

namespace NfpDirect.Pages;

public class ByPostcardModel : PageModel
{
    private readonly NfpDbContext _db;
    private const int PageSize = 50;

    public ByPostcardModel(NfpDbContext db) => _db = db;

    public int PageNumber { get; private set; }
    public bool HasNextPage { get; private set; }
    public string StateFilter { get; private set; } = "";
    public List<Postcard> Postcards { get; private set; } = [];

    public async Task<IActionResult> OnGetAsync([FromQuery] string? page, [FromQuery] string? state)
    {
        if (!int.TryParse(page, out var pageNum) || pageNum < 0)
            pageNum = 0;
        PageNumber = pageNum;
        StateFilter = (state ?? "").Trim().ToUpper();

        var queryable = _db.Postcards.AsNoTracking();
        if (StateFilter.Length > 0)
            queryable = queryable.Where(pc => pc.OrgState == StateFilter);

        Postcards = await queryable
            .OrderBy(pc => pc.CompanyName)
            .Skip(PageNumber * PageSize)
            .Take(PageSize)
            .ToListAsync();

        HasNextPage = Postcards.Count == PageSize;

        return Page();
    }
}
