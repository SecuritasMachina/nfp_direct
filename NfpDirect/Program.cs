using System.Threading.RateLimiting;
using Microsoft.AspNetCore.ResponseCompression;
using Microsoft.AspNetCore.StaticFiles;
using Microsoft.EntityFrameworkCore;
using NfpDirect.Data;

var builder = WebApplication.CreateBuilder(args);

builder.Logging.AddSimpleConsole(o =>
{
    o.TimestampFormat = "yyyy-MM-dd HH:mm:ss ";
    o.UseUtcTimestamp = false;
});

var connectionString = Environment.GetEnvironmentVariable("NFP_CONNECTION_STRING")
    ?? builder.Configuration.GetConnectionString("NfpDb")
    ?? throw new InvalidOperationException(
        "NFP_CONNECTION_STRING environment variable or NfpDb connection string is required.");

builder.Services.AddDbContextPool<NfpDbContext>(options =>
    options.UseMySql(connectionString, ServerVersion.AutoDetect(connectionString),
        mySqlOptions => mySqlOptions.EnableRetryOnFailure(
            maxRetryCount: 3,
            maxRetryDelay: TimeSpan.FromSeconds(5),
            errorNumbersToAdd: null)));

builder.Services.AddResponseCompression(opts =>
{
    opts.EnableForHttps = true;
    opts.Providers.Add<BrotliCompressionProvider>();
    opts.Providers.Add<GzipCompressionProvider>();
});

builder.Services.AddRateLimiter(opts =>
{
    opts.RejectionStatusCode = StatusCodes.Status429TooManyRequests;
    // Sliding window: 30 search requests per minute per IP, checked every 15s
    opts.AddPolicy("search", ctx =>
        RateLimitPartition.GetSlidingWindowLimiter(
            partitionKey: ctx.Connection.RemoteIpAddress?.ToString() ?? "unknown",
            factory: _ => new SlidingWindowRateLimiterOptions
            {
                PermitLimit = 30,
                Window = TimeSpan.FromMinutes(1),
                SegmentsPerWindow = 4,
                QueueProcessingOrder = QueueProcessingOrder.OldestFirst,
                QueueLimit = 0
            }));
    opts.OnRejected = (context, _) =>
    {
        context.HttpContext.Response.StatusCode = StatusCodes.Status429TooManyRequests;
        context.HttpContext.Response.Headers.RetryAfter = "60";
        return ValueTask.CompletedTask;
    };
});

builder.Services.AddOutputCache(opts =>
{
    opts.AddBasePolicy(p => p.NoCache());
    opts.AddPolicy("BrowsePage", p => p
        .Expire(TimeSpan.FromSeconds(60))
        .SetVaryByQuery("page", "state")
        .Tag("browse"));
    opts.AddPolicy("DetailsPage", p => p
        .Expire(TimeSpan.FromSeconds(30))
        .SetVaryByQuery("id")
        .Tag("details"));
});

builder.Services.AddRazorPages();

var app = builder.Build();

if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Error");
    app.UseStatusCodePagesWithReExecute("/{0}");
    app.UseHsts();
}

app.UseResponseCompression();

app.Use(async (ctx, next) =>
{
    ctx.Response.Headers["X-Content-Type-Options"] = "nosniff";
    ctx.Response.Headers["X-Frame-Options"] = "SAMEORIGIN";
    ctx.Response.Headers["Referrer-Policy"] = "strict-origin-when-cross-origin";
    ctx.Response.Headers["Permissions-Policy"] = "camera=(), microphone=(), geolocation=()";
    // CSP: no inline scripts; CDN sources for Bootstrap + Font Awesome
    ctx.Response.Headers["Content-Security-Policy"] =
        "default-src 'none'; " +
        "script-src cdn.jsdelivr.net; " +
        "style-src 'self' cdn.jsdelivr.net cdnjs.cloudflare.com; " +
        "font-src cdnjs.cloudflare.com; " +
        "img-src 'self'; " +
        "connect-src 'self'; " +
        "base-uri 'self'; " +
        "form-action 'self'; " +
        "frame-ancestors 'self'";
    await next();
});

app.UseStaticFiles(new StaticFileOptions
{
    OnPrepareResponse = ctx =>
    {
        ctx.Context.Response.Headers.CacheControl = "public, max-age=604800";
    }
});

app.UseRouting();
app.UseRateLimiter();
app.UseOutputCache();
app.MapRazorPages();
app.MapGet("/health", async (NfpDbContext db) =>
{
    try
    {
        await db.Database.ExecuteSqlRawAsync("SELECT 1");
        return Results.Ok(new { status = "ok" });
    }
    catch
    {
        return Results.StatusCode(503);
    }
});

app.Run();
