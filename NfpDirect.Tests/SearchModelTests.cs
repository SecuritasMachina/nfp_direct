using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.ModelBinding;
using Microsoft.AspNetCore.Mvc.RazorPages;
using Microsoft.AspNetCore.Mvc.ViewFeatures;
using Microsoft.EntityFrameworkCore;
using NfpDirect.Data;
using NfpDirect.Models;
using NfpDirect.Pages;

namespace NfpDirect.Tests;

public class SearchModelTests
{
    private static NfpDbContext CreateDb(string name)
    {
        var opts = new DbContextOptionsBuilder<NfpDbContext>()
            .UseInMemoryDatabase(name)
            .Options;
        return new NfpDbContext(opts);
    }

    private static SearchModel CreateModel(NfpDbContext db)
    {
        var httpContext = new DefaultHttpContext();
        var modelState = new ModelStateDictionary();
        var viewData = new ViewDataDictionary(new EmptyModelMetadataProvider(), modelState);
        var tempData = new TempDataDictionary(httpContext, new NullTempDataProvider());

        var pageContext = new PageContext(new ActionContext(
            httpContext,
            new Microsoft.AspNetCore.Routing.RouteData(),
            new PageActionDescriptor(),
            modelState))
        {
            ViewData = viewData
        };

        return new SearchModel(db)
        {
            PageContext = pageContext,
            TempData = tempData
        };
    }

    [Fact]
    public async Task EmptyQuery_ReturnsPageWithNoResults()
    {
        using var db = CreateDb(nameof(EmptyQuery_ReturnsPageWithNoResults));
        var model = CreateModel(db);

        await model.OnGetAsync(q: null, state: null, page: null);

        Assert.Equal("", model.Query);
        Assert.Empty(model.Results);
        Assert.Equal(0, model.TotalResults);
    }

    [Fact]
    public async Task NameSearch_ReturnsMatchingOrgs()
    {
        using var db = CreateDb(nameof(NameSearch_ReturnsMatchingOrgs));
        db.Pub78.AddRange(
            new Pub78 { EIN = "123456789", CompanyName = "HABITAT FOR HUMANITY", City = "Atlanta", Country = "US" },
            new Pub78 { EIN = "987654321", CompanyName = "RED CROSS SOCIETY", City = "DC", Country = "US" }
        );
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(q: "HABITAT", state: null, page: null);

        Assert.Equal("HABITAT", model.Query);
        Assert.Equal(1, model.TotalResults);
        Assert.Single(model.Results);
        Assert.Equal("123456789", model.Results[0].Pub78.EIN);
    }

    [Fact]
    public async Task EinSearch_ExactMatch_RedirectsToDetails()
    {
        using var db = CreateDb(nameof(EinSearch_ExactMatch_RedirectsToDetails));
        db.Pub78.Add(new Pub78 { EIN = "123456789", CompanyName = "SOME ORG", Country = "US" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        var result = await model.OnGetAsync(q: "123456789", state: null, page: null);

        var redirect = Assert.IsType<RedirectToPageResult>(result);
        Assert.Equal("/Details", redirect.PageName);
        Assert.Equal("123456789", redirect.RouteValues!["id"]);
    }

    [Fact]
    public async Task EinSearch_NoMatch_ReturnsZero()
    {
        using var db = CreateDb(nameof(EinSearch_NoMatch_ReturnsZero));
        db.Pub78.Add(new Pub78 { EIN = "111111111", CompanyName = "OTHER ORG", Country = "US" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(q: "999999999", state: null, page: null);

        Assert.Equal(0, model.TotalResults);
        Assert.Empty(model.Results);
    }

    [Fact]
    public async Task NegativePage_ClampsToZero()
    {
        using var db = CreateDb(nameof(NegativePage_ClampsToZero));
        db.Pub78.Add(new Pub78 { EIN = "123456789", CompanyName = "TEST ORG", Country = "US" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(q: "TEST", state: null, page: "-5");

        Assert.Equal(0, model.PageNumber);
    }

    [Fact]
    public async Task NonNumericPage_ClampsToZero()
    {
        using var db = CreateDb(nameof(NonNumericPage_ClampsToZero));
        db.Pub78.Add(new Pub78 { EIN = "123456789", CompanyName = "TEST ORG", Country = "US" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(q: "TEST", state: null, page: "abc");

        Assert.Equal(0, model.PageNumber);
    }

    [Fact]
    public async Task NameSearch_JoinsPostcard_WhenEinMatches()
    {
        using var db = CreateDb(nameof(NameSearch_JoinsPostcard_WhenEinMatches));
        db.Pub78.Add(new Pub78 { EIN = "123456789", CompanyName = "ACME CHARITY", Country = "US" });
        db.Postcards.Add(new Postcard { Id = "123456789", CompanyName = "ACME CHARITY", WebsiteURL = "acme.org" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(q: "ACME", state: null, page: null);

        Assert.Single(model.Results);
        Assert.NotNull(model.Results[0].Postcard);
        Assert.Equal("acme.org", model.Results[0].Postcard!.WebsiteURL);
    }

    [Fact]
    public async Task TotalPages_CalculatedCorrectly()
    {
        using var db = CreateDb(nameof(TotalPages_CalculatedCorrectly));
        for (int i = 0; i < 55; i++)
        {
            db.Pub78.Add(new Pub78
            {
                EIN = i.ToString("D9"),
                CompanyName = $"CHARITY NUMBER {i}",
                Country = "US"
            });
        }
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(q: "CHARITY", state: null, page: null);

        Assert.Equal(55, model.TotalResults);
        Assert.Equal(2, model.TotalPages);
        Assert.Equal(50, model.Results.Count);
    }

    [Fact]
    public async Task StateFilter_FiltersResultsByState()
    {
        using var db = CreateDb(nameof(StateFilter_FiltersResultsByState));
        db.Pub78.AddRange(
            new Pub78 { EIN = "111111111", CompanyName = "ILLINOIS FOOD BANK", State = "IL", Country = "US" },
            new Pub78 { EIN = "222222222", CompanyName = "TEXAS FOOD BANK", State = "TX", Country = "US" }
        );
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(q: null, state: "IL", page: null);

        Assert.Equal(1, model.TotalResults);
        Assert.Equal("111111111", model.Results[0].Pub78.EIN);
    }

    [Fact]
    public async Task StateFilter_CombinedWithNameSearch()
    {
        using var db = CreateDb(nameof(StateFilter_CombinedWithNameSearch));
        db.Pub78.AddRange(
            new Pub78 { EIN = "111111111", CompanyName = "FOOD BANK ILLINOIS", State = "IL", Country = "US" },
            new Pub78 { EIN = "222222222", CompanyName = "FOOD BANK TEXAS", State = "TX", Country = "US" },
            new Pub78 { EIN = "333333333", CompanyName = "RED CROSS ILLINOIS", State = "IL", Country = "US" }
        );
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(q: "FOOD", state: "IL", page: null);

        Assert.Equal(1, model.TotalResults);
        Assert.Equal("111111111", model.Results[0].Pub78.EIN);
    }

    [Fact]
    public async Task EinSearch_WithStateFilter_BackUrlIncludesState()
    {
        using var db = CreateDb(nameof(EinSearch_WithStateFilter_BackUrlIncludesState));
        db.Pub78.Add(new Pub78 { EIN = "123456789", CompanyName = "SOME ORG", State = "IL", Country = "US" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        var result = await model.OnGetAsync(q: "123456789", state: "IL", page: null);

        var redirect = Assert.IsType<RedirectToPageResult>(result);
        var backUrl = redirect.RouteValues!["back"]?.ToString();
        Assert.NotNull(backUrl);
        Assert.Contains("state=IL", backUrl);
    }
}

// Minimal ITempDataProvider for unit tests — no session/cookie needed
file sealed class NullTempDataProvider : ITempDataProvider
{
    public IDictionary<string, object?> LoadTempData(HttpContext context) => new Dictionary<string, object?>();
    public void SaveTempData(HttpContext context, IDictionary<string, object?> values) { }
}
