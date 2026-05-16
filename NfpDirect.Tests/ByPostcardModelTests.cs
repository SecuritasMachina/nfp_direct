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

public class ByPostcardModelTests
{
    private static NfpDbContext CreateDb(string name)
    {
        var opts = new DbContextOptionsBuilder<NfpDbContext>()
            .UseInMemoryDatabase(name)
            .Options;
        return new NfpDbContext(opts);
    }

    private static ByPostcardModel CreateModel(NfpDbContext db)
    {
        var httpContext = new DefaultHttpContext();
        var modelState = new ModelStateDictionary();
        var viewData = new ViewDataDictionary(new EmptyModelMetadataProvider(), modelState);
        var tempData = new TempDataDictionary(httpContext, new NullByPostcardTempDataProvider());

        var pageContext = new PageContext(new ActionContext(
            httpContext,
            new Microsoft.AspNetCore.Routing.RouteData(),
            new PageActionDescriptor(),
            modelState))
        {
            ViewData = viewData
        };

        return new ByPostcardModel(db) { PageContext = pageContext, TempData = tempData };
    }

    [Fact]
    public async Task EmptyDatabase_ReturnsNoPostcards()
    {
        using var db = CreateDb(nameof(EmptyDatabase_ReturnsNoPostcards));
        var model = CreateModel(db);

        await model.OnGetAsync(page: null, state: null);

        Assert.Empty(model.Postcards);
        Assert.Equal(0, model.PageNumber);
        Assert.False(model.HasNextPage);
    }

    [Fact]
    public async Task NonNumericPage_ClampsToZero()
    {
        using var db = CreateDb(nameof(NonNumericPage_ClampsToZero));
        var model = CreateModel(db);

        await model.OnGetAsync(page: "xyz", state: null);

        Assert.Equal(0, model.PageNumber);
    }

    [Fact]
    public async Task NegativePage_ClampsToZero()
    {
        using var db = CreateDb(nameof(NegativePage_ClampsToZero));
        var model = CreateModel(db);

        await model.OnGetAsync(page: "-1", state: null);

        Assert.Equal(0, model.PageNumber);
    }

    [Fact]
    public async Task HasNextPage_TrueWhenExactlyPageSize()
    {
        using var db = CreateDb(nameof(HasNextPage_TrueWhenExactlyPageSize));
        for (int i = 0; i < 50; i++)
            db.Postcards.Add(new Postcard { Id = i.ToString("D9"), CompanyName = $"CHARITY {i:D3}" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(page: null, state: null);

        Assert.Equal(50, model.Postcards.Count);
        Assert.True(model.HasNextPage);
    }

    [Fact]
    public async Task HasNextPage_FalseWhenFewerThanPageSize()
    {
        using var db = CreateDb(nameof(HasNextPage_FalseWhenFewerThanPageSize));
        for (int i = 0; i < 3; i++)
            db.Postcards.Add(new Postcard { Id = i.ToString("D9"), CompanyName = $"CHARITY {i}" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(page: null, state: null);

        Assert.Equal(3, model.Postcards.Count);
        Assert.False(model.HasNextPage);
    }

    [Fact]
    public async Task SecondPage_ReturnsNextBatch()
    {
        using var db = CreateDb(nameof(SecondPage_ReturnsNextBatch));
        for (int i = 0; i < 53; i++)
            db.Postcards.Add(new Postcard { Id = i.ToString("D9"), CompanyName = $"CHARITY {i:D3}" });
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(page: "1", state: null);

        Assert.Equal(1, model.PageNumber);
        Assert.Equal(3, model.Postcards.Count);
        Assert.False(model.HasNextPage);
    }

    [Fact]
    public async Task StateFilter_FiltersByOrgState()
    {
        using var db = CreateDb(nameof(StateFilter_FiltersByOrgState));
        db.Postcards.AddRange(
            new Postcard { Id = "111111111", CompanyName = "ILLINOIS ORG", OrgState = "IL" },
            new Postcard { Id = "222222222", CompanyName = "TEXAS ORG", OrgState = "TX" }
        );
        await db.SaveChangesAsync();

        var model = CreateModel(db);
        await model.OnGetAsync(page: null, state: "IL");

        Assert.Equal("IL", model.StateFilter);
        Assert.Single(model.Postcards);
        Assert.Equal("111111111", model.Postcards[0].Id);
    }
}

file sealed class NullByPostcardTempDataProvider : ITempDataProvider
{
    public IDictionary<string, object?> LoadTempData(HttpContext context) => new Dictionary<string, object?>();
    public void SaveTempData(HttpContext context, IDictionary<string, object?> values) { }
}
