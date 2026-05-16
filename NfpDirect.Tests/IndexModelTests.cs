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

public class IndexModelTests
{
    private static NfpDbContext CreateDb(string name)
    {
        var opts = new DbContextOptionsBuilder<NfpDbContext>()
            .UseInMemoryDatabase(name)
            .Options;
        return new NfpDbContext(opts);
    }

    private static IndexModel CreateModel(NfpDbContext db)
    {
        var httpContext = new DefaultHttpContext();
        var modelState = new ModelStateDictionary();
        var viewData = new ViewDataDictionary(new EmptyModelMetadataProvider(), modelState);
        var tempData = new TempDataDictionary(httpContext, new NullIndexTempDataProvider());

        var pageContext = new PageContext(new ActionContext(
            httpContext,
            new Microsoft.AspNetCore.Routing.RouteData(),
            new PageActionDescriptor(),
            modelState))
        {
            ViewData = viewData
        };

        return new IndexModel(db) { PageContext = pageContext, TempData = tempData };
    }

    [Fact]
    public async Task EmptyDatabase_ReturnsNoEntries()
    {
        using var db = CreateDb(nameof(EmptyDatabase_ReturnsNoEntries));
        var model = CreateModel(db);

        await model.OnGetAsync(page: null, state: null);

        Assert.Empty(model.Entries);
        Assert.Equal(0, model.PageNumber);
        Assert.False(model.HasNextPage);
    }

    [Fact]
    public async Task NonNumericPage_ClampsToZero()
    {
        using var db = CreateDb(nameof(NonNumericPage_ClampsToZero));
        var model = CreateModel(db);

        await model.OnGetAsync(page: "foo", state: null);

        Assert.Equal(0, model.PageNumber);
    }

    [Fact]
    public async Task NegativePage_ClampsToZero()
    {
        using var db = CreateDb(nameof(NegativePage_ClampsToZero));
        var model = CreateModel(db);

        await model.OnGetAsync(page: "-3", state: null);

        Assert.Equal(0, model.PageNumber);
    }

    [Fact]
    public async Task HasNextPage_TrueWhenExactlyPageSize()
    {
        using var db = CreateDb(nameof(HasNextPage_TrueWhenExactlyPageSize));
        for (int i = 0; i < 50; i++)
            db.Pub78.Add(new Pub78 { EIN = i.ToString("D9"), CompanyName = $"ORG {i:D3}", Country = "US" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(page: null, state: null);

        Assert.Equal(50, model.Entries.Count);
        Assert.True(model.HasNextPage);
    }

    [Fact]
    public async Task HasNextPage_FalseWhenFewerThanPageSize()
    {
        using var db = CreateDb(nameof(HasNextPage_FalseWhenFewerThanPageSize));
        for (int i = 0; i < 10; i++)
            db.Pub78.Add(new Pub78 { EIN = i.ToString("D9"), CompanyName = $"ORG {i:D3}", Country = "US" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(page: null, state: null);

        Assert.Equal(10, model.Entries.Count);
        Assert.False(model.HasNextPage);
    }

    [Fact]
    public async Task PostcardJoined_WhenEinMatches()
    {
        using var db = CreateDb(nameof(PostcardJoined_WhenEinMatches));
        db.Pub78.Add(new Pub78 { EIN = "123456789", CompanyName = "ACME ORG", Country = "US" });
        db.Postcards.Add(new Postcard { Id = "123456789", CompanyName = "ACME ORG", WebsiteURL = "acme.org" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(page: null, state: null);

        Assert.Single(model.Entries);
        Assert.NotNull(model.Entries[0].Postcard);
        Assert.Equal("acme.org", model.Entries[0].Postcard!.WebsiteURL);
    }

    [Fact]
    public async Task SecondPage_ReturnsNextBatch()
    {
        using var db = CreateDb(nameof(SecondPage_ReturnsNextBatch));
        for (int i = 0; i < 55; i++)
            db.Pub78.Add(new Pub78 { EIN = i.ToString("D9"), CompanyName = $"ORG {i:D3}", Country = "US" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(page: "1", state: null);

        Assert.Equal(1, model.PageNumber);
        Assert.Equal(5, model.Entries.Count);
        Assert.False(model.HasNextPage);
    }

    [Fact]
    public async Task StateFilter_FiltersByState()
    {
        using var db = CreateDb(nameof(StateFilter_FiltersByState));
        db.Pub78.AddRange(
            new Pub78 { EIN = "111111111", CompanyName = "ILLINOIS ORG", State = "IL", Country = "US" },
            new Pub78 { EIN = "222222222", CompanyName = "TEXAS ORG", State = "TX", Country = "US" }
        );
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(page: null, state: "IL");

        Assert.Equal("IL", model.StateFilter);
        Assert.Single(model.Entries);
        Assert.Equal("111111111", model.Entries[0].Pub78.EIN);
    }
}

file sealed class NullIndexTempDataProvider : ITempDataProvider
{
    public IDictionary<string, object?> LoadTempData(HttpContext context) => new Dictionary<string, object?>();
    public void SaveTempData(HttpContext context, IDictionary<string, object?> values) { }
}
