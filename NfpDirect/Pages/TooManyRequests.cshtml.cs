using Microsoft.AspNetCore.Mvc.RazorPages;

namespace NfpDirect.Pages;

[Microsoft.AspNetCore.Mvc.ResponseCache(Duration = 0, Location = Microsoft.AspNetCore.Mvc.ResponseCacheLocation.None, NoStore = true)]
public class TooManyRequestsModel : PageModel
{
    public void OnGet() { }
}
