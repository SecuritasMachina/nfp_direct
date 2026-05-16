using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.EntityFrameworkCore;
using NfpDirect.Data;
using NfpDirect.Models;

namespace NfpDirect.Pages;

public class DetailsModel : PageModel
{
    private readonly NfpDbContext _db;

    public DetailsModel(NfpDbContext db) => _db = db;

    public string Id { get; private set; } = "";
    public string BackUrl { get; private set; } = "/";
    public Pub78? Pub78 { get; private set; }
    public Postcard? Postcard { get; private set; }
    public Filing? Filing { get; private set; }

    public async Task OnGetAsync([FromQuery] string? id, [FromQuery] string? back)
    {
        Id = id ?? "";
        // Whitelist the back URL to prevent open redirect — only allow relative paths starting with /
        if (!string.IsNullOrWhiteSpace(back) && back.StartsWith('/') && !back.StartsWith("//"))
            BackUrl = back;
        if (string.IsNullOrWhiteSpace(Id))
            return;

        Pub78 = await _db.Pub78.AsNoTracking().FirstOrDefaultAsync(p => p.EIN == Id);
        if (Pub78 == null)
            return;

        Postcard = await _db.Postcards.AsNoTracking().FirstOrDefaultAsync(pc => pc.Id == Id);
        Filing   = await _db.Filings.AsNoTracking().FirstOrDefaultAsync(f => f.EIN == Id);
    }
}
