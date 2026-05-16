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

public class DetailsModelTests
{
    private static NfpDbContext CreateDb(string name)
    {
        var opts = new DbContextOptionsBuilder<NfpDbContext>()
            .UseInMemoryDatabase(name)
            .Options;
        return new NfpDbContext(opts);
    }

    private static (DetailsModel model, DefaultHttpContext httpContext) CreateModel(NfpDbContext db)
    {
        var httpContext = new DefaultHttpContext();
        var modelState = new ModelStateDictionary();
        var viewData = new ViewDataDictionary(new EmptyModelMetadataProvider(), modelState);
        var tempData = new TempDataDictionary(httpContext, new NullDetailsTempDataProvider());

        var pageContext = new PageContext(new ActionContext(
            httpContext,
            new Microsoft.AspNetCore.Routing.RouteData(),
            new PageActionDescriptor(),
            modelState))
        {
            ViewData = viewData
        };

        return (new DetailsModel(db) { PageContext = pageContext, TempData = tempData }, httpContext);
    }

    [Fact]
    public async Task NotFoundEin_Returns404StatusCode()
    {
        using var db = CreateDb(nameof(NotFoundEin_Returns404StatusCode));
        var (model, httpContext) = CreateModel(db);

        var result = await model.OnGetAsync(id: "999999999", back: null);

        Assert.IsType<PageResult>(result);
        Assert.Equal(404, httpContext.Response.StatusCode);
        Assert.Null(model.Pub78);
    }

    [Fact]
    public async Task EmptyId_ReturnsPageWithoutQuery()
    {
        using var db = CreateDb(nameof(EmptyId_ReturnsPageWithoutQuery));
        var (model, _) = CreateModel(db);

        var result = await model.OnGetAsync(id: null, back: null);

        Assert.IsType<PageResult>(result);
        Assert.Null(model.Pub78);
    }

    [Fact]
    public async Task FoundOrg_Returns200WithPub78()
    {
        using var db = CreateDb(nameof(FoundOrg_Returns200WithPub78));
        db.Pub78.Add(new Pub78 { EIN = "123456789", CompanyName = "Test Org" });
        await db.SaveChangesAsync();

        var (model, httpContext) = CreateModel(db);

        var result = await model.OnGetAsync(id: "123456789", back: null);

        Assert.IsType<PageResult>(result);
        Assert.Equal(200, httpContext.Response.StatusCode);
        Assert.NotNull(model.Pub78);
        Assert.Equal("123456789", model.Pub78!.EIN);
    }

    [Fact]
    public async Task BackUrl_ValidRelative_IsAccepted()
    {
        using var db = CreateDb(nameof(BackUrl_ValidRelative_IsAccepted));
        var (model, _) = CreateModel(db);

        await model.OnGetAsync(id: null, back: "/Search?q=test");

        Assert.Equal("/Search?q=test", model.BackUrl);
    }

    [Fact]
    public async Task BackUrl_ExternalUrl_IsSanitizedToRoot()
    {
        using var db = CreateDb(nameof(BackUrl_ExternalUrl_IsSanitizedToRoot));
        var (model, _) = CreateModel(db);

        await model.OnGetAsync(id: null, back: "https://evil.com");

        Assert.Equal("/", model.BackUrl);
    }

    [Fact]
    public async Task BackUrl_ProtocolRelative_IsSanitizedToRoot()
    {
        using var db = CreateDb(nameof(BackUrl_ProtocolRelative_IsSanitizedToRoot));
        var (model, _) = CreateModel(db);

        await model.OnGetAsync(id: null, back: "//evil.com");

        Assert.Equal("/", model.BackUrl);
    }
}

file sealed class NullDetailsTempDataProvider : ITempDataProvider
{
    public IDictionary<string, object?> LoadTempData(HttpContext context) => new Dictionary<string, object?>();
    public void SaveTempData(HttpContext context, IDictionary<string, object?> values) { }
}
