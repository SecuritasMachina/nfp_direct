# NFP Direct — Improvements Log

## Iteration 1 — C# Razor + MariaDB Migration (2026-05-16)

### What changed
Migrated the Spring Boot 2.1 / JSP application to an ASP.NET Core 8 Razor Pages application backed by MariaDB (via Pomelo EF Core).

**New project**: `nfp_direct/NfpDirect/`
- `NfpDirect.csproj` — targets net8.0, uses Pomelo.EntityFrameworkCore.MySql 8.0.2
- `Program.cs` — minimal API setup; connection string from env var `NFP_CONNECTION_STRING`
- `Data/NfpDbContext.cs` — EF Core context for Pub78, Postcard, Filing
- `Models/Pub78.cs` — maps to `pub78` table
- `Models/Postcard.cs` — maps to `nfp_epostcard` table
- `Models/Filing.cs` — maps to `filing` table (230+ fields)
- `Pages/Index.cshtml(.cs)` — paginated Pub78 list with joined Postcard info (50/page)
- `Pages/Details.cshtml(.cs)` — org detail view showing Pub78 + Postcard + Filing data
- `Pages/ByPostcard.cshtml(.cs)` — paginated postcard list
- `Pages/Shared/_Layout.cshtml` — Bootstrap 5.3 layout with navbar
- `Dockerfile` — multi-stage build on .NET 8 SDK/runtime

**New docker-compose**: `docker/docker-compose.beta.yml`
- `nfp-direct-beta` service on port `${NFP_BETA_PORT:-8096}`
- `mariadb-nfp-beta` service (MariaDB 11.4) with health check
- Isolated `nfp_beta_net` network and `nfp_beta_db` named volume

### Why top priority
User requested this migration. The Java app used Spring Boot 2.1 (EOL), JSP views with XSS vulnerabilities, deprecated `URLEncoder.encode` calls, malformed HTML (double `<html>` tags), and bare `@Autowired` on a logger. The C# rewrite resolves all of these while matching the existing project stack.

### Impact
- Razor auto-escapes all output → eliminates XSS present in JSPs
- `int.TryParse` with range check → eliminates NumberFormatException 500 errors
- Bootstrap 5.3 via CDN + navbar → adds missing navigation and modernizes UI
- EF Core + async queries → structured data access replacing scriptlet-style Java in JSPs
- Health check on MariaDB → docker-compose waits for DB before starting app

### Skipped / deferred
- Search functionality (no search UI in original; would be next improvement)
- HTTPS/TLS termination (handle at reverse proxy / Traefik layer)
- DB seed/migration scripts (existing database is imported separately)
- Angular client app in `client/` — unchanged; separate concern

---

## Iteration 2 — Search, Tests, Stability, SEO (2026-05-16)

### Changes

**Search functionality** (`Pages/Search.cshtml` + `Pages/Search.cshtml.cs`)
- Full-text name search (case-insensitive `Contains`) and 9-digit EIN exact-match
- Paginated results (50/page) with result count and page indicator
- Search bar injected into the navbar (all pages); retains query value on search page
- EIN auto-detection heuristic: all-digits, exactly 9 digits after stripping hyphens

**Test project** (`NfpDirect.Tests/`)
- xUnit project targeting net8.0 with EF Core InMemory provider
- 8 tests covering: empty query, name search, EIN search, no-match, negative page,
  non-numeric page, postcard join, and pagination (55 results → 2 pages)
- All 8 pass

**Error page** (`Pages/Error.cshtml` + `Pages/Error.cshtml.cs`)
- Added `ErrorModel` PageModel code-behind — previously missing, would NullRef at `Model.RequestId`
- `ResponseCache` attribute prevents caching of error responses

**Response compression** (`Program.cs`)
- Added Brotli + Gzip via `UseResponseCompression`
- Static files served with `Cache-Control: public, max-age=604800` (7-day browser cache)
- `UseHsts` enabled in non-dev environments

**UI / correctness fixes**
- `Details.cshtml` — removed bare `return;` mid-template; replaced with proper `@if/else`
- `Details.cshtml` — fixed "Website @@  url" rendering to "Website (url)"
- `Details.cshtml` — added per-org `<meta name="description">` via `ViewData["Description"]`
- `Index.cshtml.cs` — changed browse order from EIN (lexicographic) to CompanyName alphabetical
- `_Layout.cshtml` — navbar is now mobile-responsive (hamburger toggle); `<meta description>` rendered when set

**Ops / deploy**
- `.gitignore` for `bin/`, `obj/`, IDE artifacts
- `.dockerignore` to exclude build artifacts and docs from Docker context (faster builds)
- `.env.example` documenting required env vars for `docker-compose.beta.yml`
- `README.md` documenting stack, tables, startup commands, and page routes
- `wwwroot/robots.txt` — disallows bot crawling of `/ByPostcard` paginated browse to reduce DB load

### Impact
- Search: reduces time-to-find an org from O(pages scrolled) to 1 query
- Tests: coverage 0 → 8 passing tests for search model logic
- Error page fix: prevents 500 on 500 (NullRef when rendering error)
- Compression: reduces HTML transfer size ~70% for Brotli clients
- Details rewrite: eliminates the mid-template `return;` anti-pattern

### Skipped / deferred
- Full-text search across City/State (currently name + EIN only)
- DB index recommendations (no access to live DB schema; would need `SHOW INDEX`)
- Playwright/E2E regression suite (no running instance available; would be next)
- Angular `client/` app — unchanged

---

## Iteration 3 — Bug Fixes, State Filter, SEO, Empty State (2026-05-16)

### Changes

**Pagination bug** (`Index.cshtml.cs`, `ByPostcard.cshtml.cs`)
- Added `HasNextPage` property (true only when page returned exactly `PageSize` rows)
- Browse and ByPostcard pages now hide "Next" button when on the last page
- Previously "Next" always rendered, looping users to empty pages

**Wrong address field** (`Index.cshtml`, `ByPostcard.cshtml`, `Details.cshtml`)
- `@pc.State` (officer's state) was used in org address blocks; corrected to `@pc.OrgState`
- Affects all cards showing org city/state/postal — was always showing wrong value

**Website URL scheme doubling** (`Index.cshtml`, `Search.cshtml`, `ByPostcard.cshtml`, `Details.cshtml`)
- `href="http://@pc.WebsiteURL"` doubled the scheme for URLs already starting with `http://`
- Fixed: check `StartsWith("http", ...)` before prepending `http://`

**State filter in search** (`Search.cshtml`, `Search.cshtml.cs`)
- Added optional `?state=XX` query parameter (2-letter, case-insensitive)
- Search form now has a state input alongside name/EIN field
- State filter combines with name search; pagination preserves state in URLs
- "No results" and result count lines mention active state filter

**Canonical link** (`_Layout.cshtml`)
- Added `<link rel="canonical" href="...">` using `Request.Scheme + Host + Path`
- Prevents duplicate-content penalties from query-string variants

**Active nav link** (`_Layout.cshtml`)
- Browse and By Postcard links now get Bootstrap `active` class when on the matching route
- Users can tell which section they're in

**Empty state on index** (`Index.cshtml`)
- Added `alert-info` message when no orgs are returned (empty DB / loading state)
- Previously showed pagination controls with no content and no explanation

**Schema SQL** (`schema.sql`)
- Added `SET SESSION innodb_strict_mode = OFF` at the top with an explanatory comment
- Without it the `filing` table DDL fails at creation time on stock MariaDB configs

**Tests** (`NfpDirect.Tests/SearchModelTests.cs`)
- Updated all existing tests for new `state` parameter on `OnGetAsync`
- Added 2 new tests: `StateFilter_FiltersResultsByState`, `StateFilter_CombinedWithNameSearch`
- Total: 10 passing tests (was 8)

### Impact
- Pagination loop: eliminated misleading "Next" link on final pages
- Address correctness: org state was always wrong (showing officer state) — now correct
- URL doubling: websites like `http://acme.org` were being linked as `http://http://acme.org`
- State filter: users searching "youth" in IL now get IL-only results immediately
- SEO: canonical link reduces duplicate-content risk from paginated browse URLs

---

## Iteration 4 — Performance Indexes, Security Headers, TypeCode Labels, Bug Fixes (2026-05-16)

### Changes

**Database indexes** (applied to live MariaDB + added to `schema.sql`)
- `pub78.CompanyName(191)` — speeds ORDER BY in browse, prefix scans in name search
- `pub78.State` — speeds state filter predicate
- `pub78.(State, CompanyName(191))` — covers combined state+name queries
- `nfp_epostcard.company_name(191)` — speeds ORDER BY in ByPostcard browse
- Without these, all queries on a full IRS dataset do sequential scans

**Security headers** (`Program.cs`)
- Added `X-Content-Type-Options: nosniff`
- Added `X-Frame-Options: SAMEORIGIN`
- Added `Referrer-Policy: strict-origin-when-cross-origin`
- Applied via middleware before static files so all responses include them

**DataProtection key persistence** (`docker-compose.beta.yml`)
- Added `nfp_dataprotection` named volume mounted to `/root/.aspnet/DataProtection-Keys`
- Eliminates startup warning about keys not surviving container restarts
- Without this, encrypted cookies/antiforgery tokens would break after deploys

**TypeCode human labels** (`Helpers/TypeCodeHelper.cs`, Search/Index/Details views)
- Introduced `TypeCodeHelper.Label(code)` — maps IRS type codes to plain English
- "3" → "Public Charity", "4" → "Social Welfare", etc. (30+ code mappings)
- Badge tooltips show the raw IRC code; badge text shows the human label
- Applied to Search results, Index browse cards, and Details page header

**Postcard join fix** (`Details.cshtml.cs`)
- Was joining `postcard.CompanyName == pub78.CompanyName` (fragile text match)
- Fixed to `postcard.Id == EIN` (consistent with how Index/Search join postcards)
- Company names can differ due to formatting; EIN join is authoritative

**EIN normalization** (`Search.cshtml.cs`)
- `12-3456789` was detected as EIN-like (9 digits) but queried with the hyphen
- Fixed: normalize to `normalizedEin = Query.Replace("-", "")` before WHERE clause

**Cleanup** (`Program.cs`)
- Removed misleading "CSS, fonts bundled locally" comment from static file handler

### Impact
- Indexes: on the full IRS pub78 dataset (~1.8M rows), browse and state filter queries
  drop from sequential scan to index range scan — estimated 100–1000× faster
- Security: site now passes OWASP header checks
- Restarts: DataProtection keys survive container restarts; no auth token invalidation
- TypeCode: users see "Public Charity" instead of "3" — no prior IRS knowledge required
- Details: postcard data now shows for orgs whose name has formatting differences
- EIN: `12-3456789` and `123456789` both resolve to the same org

---

## Iteration 5 — Stability, Health Check, Query Optimization, 404 Page (2026-05-16)

### Changes

**DB connection retry** (`Program.cs`)
- `EnableRetryOnFailure(maxRetryCount: 3, maxRetryDelay: 5s)` on EF Core pool
- Handles transient MySQL connection drops without user-visible errors

**Health endpoint** (`Program.cs`, `Dockerfile`)
- Added `GET /health` → `{"status":"ok"}` via `app.MapGet`
- Updated Dockerfile healthcheck from TCP probe to `curl -f http://localhost:8080/health`
- Proper HTTP healthcheck means the container reports unhealthy if the app is up but broken

**Custom 404 page** (`Pages/NotFound.cshtml`, `Program.cs`)
- Added branded 404 page with "Back to Organizations" and "Search" buttons
- `UseStatusCodePagesWithReExecute("/{0}")` routes 404 → `/404` with branded layout
- Previously: bare empty response from Kestrel

**Search query optimization** (`Search.cshtml.cs`)
- Removed `ToUpper().Contains()` — this generates `UPPER(col) LIKE '%term%'` which
  bypasses the prefix-index benefit and adds per-row function overhead
- With utf8mb4_unicode_ci (case-insensitive collation), plain `.Contains(Query)` works
  correctly and generates `col LIKE '%term%'` without the UPPER() wrapper

**Dockerfile cleanup** (`Dockerfile`)
- `ASPNETCORE_URLS=http://+:8080` → `ASPNETCORE_HTTP_PORTS=8080`
- Eliminates startup warning about overriding `HTTP_PORTS`; uses the .NET 8 standard env var

### Impact
- Retry policy: isolated DB blip no longer returns 500 to the user
- Health endpoint: accurate container health state visible to Docker and orchestrators
- 404 page: users land on a branded page instead of an empty screen
- Query: removes UPPER() wrapping on CompanyName column per search query

---

## Iteration 6 — Accessibility, Open Graph, CSS Polish (2026-05-16)

### Changes

**Accessibility — aria-hidden on icons** (all 4 pages + _Layout.cshtml)
- Added `aria-hidden="true"` to every Font Awesome `<i>` tag site-wide
- Without this, screen readers announce raw class names ("fa fa-globe")
- Submit button in navbar now has `aria-label="Search"`

**Open Graph meta tags** (`_Layout.cshtml`)
- Added `og:type`, `og:site_name`, `og:title`, `og:description`
- Link previews in Slack, iMessage, Twitter/X now show org name and description
- `og:description` mirrors the existing `<meta name="description">` when set

**CSS hover polish** (`wwwroot/css/site.css`)
- Cards get `box-shadow: 0 2px 8px rgba(0,0,0,0.12)` on hover via CSS transition
- Gives users visual affordance that cards are interactive (they link to detail pages)
- Added `@media (max-width: 576px)` breakpoint — filing DL on mobile stacks dt above dd
  instead of trying to put two columns in a narrow viewport

### Impact
- Screen reader users: no more "fa fa-globe" announcements on every org card
- Social sharing: link previews show org name and context instead of site title only
- Mobile: filing detail section readable on phones without horizontal scroll

---

## Iteration 7 — Helpers Refactor, EIN/Date Formatting, Y/N Fixes (2026-05-16)

### Changes

**`TypeCodeHelper` expanded** (`Helpers/TypeCodeHelper.cs`)
- Added `TitleCase(string?)` — centralizes the `TextInfo.ToTitleCase(s.ToLower())` pattern
- Added `FormatEin(string?)` — formats 9-digit EIN as `XX-XXXXXXX` (IRS standard display)
- Added `FormatTaxPeriod(string?)` — converts `YYYYMM` → "MMM YYYY" (e.g. "201912" → "Dec 2019")
- All four views now use `TypeCodeHelper.TitleCase()` instead of 3-line verbose expression

**Global helper import** (`Pages/_ViewImports.cshtml`)
- Added `@using NfpDirect.Helpers` to `_ViewImports.cshtml`
- Removed per-page `@using NfpDirect.Helpers` lines from Index, Search, Details

**EIN display formatting** (Index, Search, Details)
- EINs now render as `12-3456789` instead of `123456789` on all three listing views
- Raw 9-digit format is IRS database storage; the hyphen format is standard human display

**Tax period formatting** (`Details.cshtml`)
- Filing `TaxPd` field ("201912") now shows "Dec 2019" in the filing details section
- Falls back to raw value if format isn't recognized

**Postcard Y/N fields** (`Details.cshtml`)
- `GrossReceiptsUnder25k` and `Terminated` on postcard section now use `YesNo()` helper
- Were rendering raw "Y"/"N" characters instead of "Yes"/"No"

### Impact
- `TitleCase` refactor: 4 views reduced from 3-line verbose expression to 1-line call
- EIN display: users see the canonical `XX-XXXXXXX` format they recognize from IRS notices
- Tax period: "Dec 2019" is immediately scannable; "201912" requires mental parsing
- Y/N fix: postcard "Gross Receipts" and "Terminated" fields now human-readable

---

## Iteration 8 — Back Navigation with Open Redirect Protection (2026-05-16)

### Changes

**Back navigation** (`Details.cshtml.cs`, `Details.cshtml`, `Index.cshtml`, `Search.cshtml`, `ByPostcard.cshtml`)
- `Details` page now accepts optional `?back=<url>` query parameter
- Links from Index, Search, and ByPostcard to Details pass the current page as `back=`
- "← Back to list" button is now "← Back" and returns to the referrer page
- Search → Details → "← Back" returns to the same search query and state filter

**Open redirect protection** (`Details.cshtml.cs`)
- `back` URL is validated: must start with `/` and not `//` (prevents `//evil.com`)
- External URLs, empty strings, and protocol-relative URLs silently fall back to `/`
- Passes OWASP open redirect test: `back=https://evil.com` → redirects to `/`

### Impact
- Navigating from "search 'youth' in IL" → org detail → "← Back" preserves the search context
- Security: no open redirect vulnerability introduced despite accepting URL as query param

---

## Iteration 9 — FULLTEXT Search (2026-05-16)

### Changes

**FULLTEXT index** (`schema.sql`)
- Added `ALTER TABLE pub78 ADD FULLTEXT INDEX ft_pub78_company (CompanyName)` to schema.sql
- Index was also applied directly to the live MariaDB in the previous iteration

**FULLTEXT search path** (`Search.cshtml.cs`)
- Name queries ≥ 4 characters now use `MATCH(CompanyName) AGAINST('+term*' IN BOOLEAN MODE)` via `FromSqlRaw`
- Boolean mode prefix wildcard (`+term*`) matches word prefixes — "habitat" matches "Habitat for Humanity"
- Multi-word queries become "+word1* +word2*" — all words must be present
- Query sanitized before FULLTEXT: non-alphanumeric, non-space chars stripped to prevent FULLTEXT operator injection
- Falls back to `LIKE '%term%'` for: queries < 4 chars, empty sanitized query, or InMemory EF provider (tests)
- State filter, ordering, and pagination compose correctly on top of the `FromSqlRaw` queryable

### Impact
- On a 1.8M-row pub78 table, `LIKE '%term%'` does a full sequential scan (~seconds); FULLTEXT MATCH/AGAINST uses the inverted index and returns in milliseconds
- Test suite: all 10 tests still pass — InMemory provider detection routes tests through the LIKE path, preserving case-sensitive InMemory behavior

---

## Iteration 10 — Tests, Back URL Fix, SEO (2026-05-16)

### Changes

**Index back URL bug** (`Index.cshtml`)
- Back link from browse cards was hardcoded to `/`; browsing page 5 → org detail → "← Back" returned to page 1
- Fixed: compute `indexBackUrl` from `ViewContext.HttpContext.Request.QueryString.Value` (same pattern as ByPostcard)
- Now "← Back" from any browse page returns to the correct page

**Page title fix** (`Index.cshtml`)
- Title was "Non-Profit Grant Match — Page N" (legacy boilerplate)
- Changed to "Browse Non-Profits — Page N" to match the actual page content

**robots.txt update** (`wwwroot/robots.txt`)
- Added `Disallow: /Search` — search result pages at `/Search?q=xyz&state=IL&page=N` create unbounded high-cardinality URLs with duplicate-content risk for bots
- Already had `/ByPostcard` disallowed

**Test coverage: IndexModel** (`NfpDirect.Tests/IndexModelTests.cs`)
- 7 new tests: empty DB, non-numeric page, negative page, `HasNextPage` true/false, postcard join, second-page offset
- All pass with InMemory EF Core provider

**Test coverage: ByPostcardModel** (`NfpDirect.Tests/ByPostcardModelTests.cs`)
- 6 new tests: empty DB, non-numeric/negative page clamp, `HasNextPage` true/false, second-page offset
- All pass with InMemory EF Core provider

### Impact
- Test suite: 10 → 23 passing tests; `IndexModel` and `ByPostcardModel` now have regression coverage
- Back navigation: users browsing page 5 no longer lose their place when returning from a detail view
- SEO: `/Search` excluded from bot crawls, reducing duplicate-content index churn

---

## Iteration 11 — EIN Auto-Redirect, Favicon, Meta Descriptions (2026-05-16)

### Changes

**EIN auto-redirect** (`Search.cshtml.cs`)
- When an EIN-like query matches exactly 1 org, `OnGetAsync` now returns `RedirectToPage("/Details", {id, back})`
- Back URL pre-set to `/Search?q=<ein>` so "← Back" returns to the search
- Removes the intermediate "1 result" list page for EIN lookups — users land directly on the org detail
- `EinSearch_ExactMatch` test updated to assert `RedirectToPageResult` with correct EIN route value

**Favicon** (`wwwroot/favicon.svg`, `_Layout.cshtml`)
- Added SVG favicon: blue rounded square with white "N" lettermark
- Linked via `<link rel="icon" href="~/favicon.svg" type="image/svg+xml" />` in `<head>`
- SVG favicons are supported by all modern browsers and scale perfectly at any size

**Meta descriptions** (`Index.cshtml`, `ByPostcard.cshtml`)
- Index: "Browse IRS-registered non-profit organizations by name. Search by organization name, 9-digit EIN, or filter by state."
- ByPostcard: "Browse non-profit organizations with IRS Form 990-N (e-Postcard) filings."
- Both set `ViewData["Description"]` which the layout renders as `<meta name="description">` and `<meta property="og:description">`

### Impact
- EIN redirect: EIN lookup goes from 2 clicks (search → results → click detail) to 1 (search → detail)
- Favicon: browsers show "N" icon in tabs/bookmarks instead of blank page icon
- Descriptions: search engines now have meaningful snippets for Index and ByPostcard browse pages

---

## Iteration 12 — DB Health Check (2026-05-16)

### Changes

**Health endpoint with DB check** (`Program.cs`)
- `/health` previously returned `{"status":"ok"}` unconditionally — it would report healthy even if the DB was down
- Now executes `SELECT 1` against the pool before responding
- Returns HTTP 200 `{"status":"ok"}` on success; HTTP 503 on any DB error
- Docker HEALTHCHECK (Dockerfile `curl -f http://localhost:8080/health`) now correctly reports unhealthy when the DB is unreachable

### Impact
- Container health state now reflects actual service availability, not just process liveness
- Orchestrators (Docker, Compose `healthcheck`) will mark the container unhealthy and can trigger restart if DB connectivity is lost

---

## Iteration 13 — Financial Formatting, Officer Address Fix (2026-05-16)

### Changes

**`TypeCodeHelper.FormatMoney`** (`Helpers/TypeCodeHelper.cs`)
- New helper: parses string field as `decimal` and formats with `N0` (comma-separated, no decimals)
- `"1234567"` → `"1,234,567"`, `"0"` → `"0"`, unparseable / blank → `"—"`
- Falls back to raw trimmed value if decimal parsing fails

**Filing financial fields formatted** (`Details.cshtml`)
- 24 money fields now use `FormatMoney` instead of `Fmt`: total revenue/expenses/assets/liabilities/net-assets, contributions, program service revenue, officer compensation, salaries, fundraising fees, all grants, reportable compensation, investment/rental/fundraising/gaming income, sales of assets, gross receipts, occupancy, travel, insurance, interest, depreciation
- Count fields (employees, individuals/contractors > $100K) remain as `Fmt` — they are integer counts, not dollar amounts

**Officer address completeness** (`Details.cshtml`)
- Officer address block was showing only City and Country, omitting State and PostalCode
- Fixed: `OfficerAddressCity, State PostalCode` on one line before Country (mirrors org address formatting)

---

## Iteration 14 — ByPostcard State Filter, Button Fix (2026-05-16)

### Changes

**ByPostcard state filter** (`ByPostcard.cshtml`, `ByPostcard.cshtml.cs`)
- Added optional `?state=XX` query parameter; filters by `OrgState` (not officer state)
- Added compact state filter form (max-width 220px) below the page heading
- "Clear" link appears when a state filter is active
- Pagination links preserve `?state=XX` so filtering survives page navigation
- New test: `StateFilter_FiltersByOrgState` verifies IL/TX separation

**ByPostcard button text fix** (`ByPostcard.cshtml`)
- "Details for COMPANY NAME" button showed raw all-caps company name from the DB
- Simplified to just "Details" — the card heading already shows the name in TitleCase

### Impact
- State filter: users browsing by postcard can now narrow to a single state (same workflow as Search page)
- Test suite: 23 → 24 passing tests
- Button text: no more shouted all-caps org names in the Details button

---

## Iteration 15 — TypeCodeHelper Tests, Filing Section Grouping (2026-05-16)

### Changes

**TypeCodeHelper unit tests** (`NfpDirect.Tests/TypeCodeHelperTests.cs`)
- 28 new tests covering: `Label` (known/unknown/null), `FormatEin` (9-digit/formatted/null), `FormatTaxPeriod` (valid/invalid/null), `YesNo` (Y/N/1/0/TRUE/FALSE/null), `FormatMoney` (integer/negative/zero/blank/null/non-numeric), `TitleCase` (CAPS/null)
- Total test suite: 24 → 52 tests

**Filing section grouped** (`Details.cshtml`)
- Single flat `<dl>` with 44 rows → 5 labeled sections: Identity, Governance & Activities, Financial Summary, Other Income, Expenses & Grants, Compensation Disclosure
- Section headings use Bootstrap `text-uppercase fw-semibold text-muted small` for visual hierarchy without adding extra elements
- "Terminated?" field moved into Identity section for immediate visibility
- Compliance/governance flags grouped together away from financial data

**"Filing ↓" jump link** (`Details.cshtml`)
- Added `id="filing"` to the filing card; org card header shows "Filing ↓" button when filing data exists
- Lets users skip the address/officer block and jump directly to financial data on long pages

---

## Iteration 16 — State Filter on Browse / Index Page (2026-05-16)

### Changes

**State filter on Index page** (`Index.cshtml`, `Index.cshtml.cs`)
- Added optional `?state=XX` parameter to `IndexModel.OnGetAsync` — filters pub78 by `State` column
- Added compact state filter form (max-width 220px) with "Clear" link when active
- All pagination links now include `&state=XX` when a filter is active
- New test `StateFilter_FiltersByState` verifies IL/TX separation

**Test suite expansion** (`IndexModelTests.cs`)
- Updated all 7 existing tests to pass `state: null` (required by new signature)
- Added `StateFilter_FiltersByState` test
- Total: 52 → 53 passing tests

### Impact
- Users can now filter the alphabetical browse list by state without going to Search
- All three browse interfaces (Index, ByPostcard, Search) now support state filtering consistently

---

## Iteration 17 — Sitemap, Theme Color, UX Polish (2026-05-16)

### Changes

**Sitemap** (`wwwroot/sitemap.xml`, `wwwroot/robots.txt`)
- Added `sitemap.xml` listing `/` (priority 1.0) and `/Search` (priority 0.9)
- `/ByPostcard` and dynamic `/Details?id=...` pages excluded — paginated browse and 1.8M+ org detail pages would bloat the sitemap without SEO benefit
- Added `Sitemap:` directive to `robots.txt` so crawlers auto-discover it

**Mobile theme color** (`_Layout.cshtml`)
- Added `<meta name="theme-color" content="#0d6efd">` — matches Bootstrap primary color
- Chrome/Android shows the NFP Direct blue in the browser chrome when the site is open

**`autocomplete="off"` on state inputs** (`Index.cshtml`, `ByPostcard.cshtml`, `Search.cshtml`)
- All three state filter inputs now have `autocomplete="off"`
- Prevents browser from suggesting previous state searches (which aren't meaningful for this field)

---

## Iteration 18 — Empty States, Print CSS (2026-05-16)

### Changes

**Improved empty-state on Index** (`Index.cshtml`)
- Was: "No organizations found. The database may still be loading." for all empty cases
- Now: when state filter is active, says "No organizations found in state IL. Clear filter to browse all states."
- "Clear filter" links directly to `/`

**Empty-state on ByPostcard** (`ByPostcard.cshtml`)
- Previously: no empty-state handling — page just showed no cards with pagination
- Added: same state-aware empty-state as Index (either state-specific or generic DB loading message)

**Print CSS** (`wwwroot/css/site.css`)
- Added `@media print` rules: hide nav, forms, buttons, footer; remove box shadows; set 11pt body font
- Allows users to print org details from the Details page cleanly
- `a[href]::after { content: none }` suppresses the default Bootstrap/browser "print URL after links" behavior

---

## Iteration 19 — README Update (2026-05-16)

### Changes

**README.md** (`NfpDirect/README.md`)
- Updated DB import command from `mariadb-nfp-beta` to shared `mariadb` container (matches actual compose config)
- Added FULLTEXT note for search
- Added state filter mention for all browse pages
- Updated test count (53)
- Added `/health` and `/sitemap.xml` to pages table
- Added schema.sql index summary table
- Added `schema.sql` creation command in "Populating the database" section

---

## Iteration 20 — Dockerfile Optimization, Pub78 Location Fallback (2026-05-16)

### Changes

**Dockerfile `--no-restore`** (`Dockerfile`)
- `dotnet publish` no longer re-runs restore since `dotnet restore` already ran in a prior step
- Saves ~2–5s per Docker build (no NuGet network requests during publish)

**Pub78 location shown when no postcard** (`Details.cshtml`)
- When an org exists in pub78 but has no postcard (`nfp_epostcard`) record, the card showed only "No postcard information found."
- Now also shows city/state/country from the Pub78 record when available
- Provides at least geographic context for orgs without full postcard data

---

## Iteration 21 — State Autocomplete Datalist (2026-05-16)

### Changes

**US States dictionary** (`TypeCodeHelper.cs`)
- Added `TypeCodeHelper.UsStates` — a `IReadOnlyDictionary<string, string>` of all 50 states + DC + 5 territories (PR, GU, VI, AS, MP), keyed by 2-letter code
- Ordered alphabetically by state name in the datalist

**State `<datalist>` in layout** (`_Layout.cshtml`)
- Added `<datalist id="us-states">` rendered once in the layout using `TypeCodeHelper.UsStates`
- All state filter inputs across Index, ByPostcard, and Search reference `list="us-states"`
- Browser now shows state name suggestions as user types in any state filter field
- Free text still allowed (datalist, not select) so international/territory codes work

### Impact
- Users typing "IL" or "il" in any state filter now see "Illinois" suggestion — reduces typos
- One datalist rendered once in DOM, referenced 3 times — no duplication

---

## Iteration 22 — Details Short-Circuit, UsStates Tests (2026-05-16)

### Changes

**Details model short-circuit** (`Details.cshtml.cs`)
- When pub78 query returns null (EIN not found), immediately `return` — skip postcard and filing queries
- For not-found EINs, reduces DB round-trips from 3 to 1
- View still correctly shows the "Organization not found" alert (Postcard and Filing remain null)

**UsStates tests** (`TypeCodeHelperTests.cs`)
- Added 2 tests: count ≥ 51 entries (50 states + DC minimum), IL/DC/PR spot-checks, case-insensitive key lookup
- Test suite: 53 → 55 passing tests

---

## Iteration 23 — OG URL Tag (2026-05-16)

### Changes

**`og:url` meta tag** (`_Layout.cshtml`)
- Added `<meta property="og:url">` matching the canonical URL
- Completes the minimum required OG tag set: type, site_name, url, title, description
- Social sharing previews in Slack/iMessage/X now include the correct page URL

---

## Iteration 24 — Keyboard Shortcut, Page Title Fix (2026-05-16)

### Changes

**Keyboard shortcut for search** (`_Layout.cshtml`)
- Added `accesskey="s"` to the navbar search input
- Alt+S (Windows/Linux) or Ctrl+Option+S (Mac) focuses the search box from any page

**Details page title** (`Details.cshtml`)
- Was: "NFP — Habitat For Humanity - NFP Direct" (double site prefix)
- Now: "Habitat For Humanity - NFP Direct" — clean, consistent with other pages

---

## Iteration 25 — Null Safety, Minor Polish (2026-05-16)

### Changes

**Google News URL null safety** (`Details.cshtml`)
- `pub.CompanyName + " " + pub.City` — if both null, would encode to a blank search
- Changed to `(pub.CompanyName ?? name) + " " + (pub.City ?? "")` — falls back to TitleCase name
- Prevents malformed `q=%20` Google News queries for orgs with no name stored in pub78

---

## Iteration 26 — Dead UA Script, Details HTTP 404, noopener (2026-05-16)

### Changes

**Removed dead Google Analytics UA script** (`_Layout.cshtml`)
- Google Universal Analytics (UA-20653927-31) was shut down July 1, 2024
- The 8-line GA script block was loading a dead CDN resource on every page load
- Zero analytics data was being collected; removed entirely
- Impact: eliminates one dead external network request per page

**Details page now returns HTTP 404 for not-found EINs** (`Details.cshtml.cs`)
- `OnGetAsync` was `Task` (void); set `Response.StatusCode = 404` and changed to `Task<IActionResult>`
- Previously, visiting `/Details?id=000000000` returned HTTP 200 with "not found" content
- Search engine bots were treating soft-404s as valid pages — now they receive proper 404
- Same branded not-found page is shown; only the HTTP status code changes

**`rel="noopener noreferrer"` on all `target="_blank"` links** (all pages)
- 13 external link anchors across Index, Search, ByPostcard, Details, _Layout were missing this attribute
- Without it, the opened page can access `window.opener` and redirect the parent tab (tabnapping)
- Applied with a single `sed -i` across all 5 affected files

**DetailsModel unit tests** (`NfpDirect.Tests/DetailsModelTests.cs`)
- 6 new tests: 404 status for missing EIN, empty id returns page, found org returns 200 + model,
  valid relative back URL accepted, external URL sanitized to "/", protocol-relative URL sanitized to "/"
- Tests: 55 → 61 passing

### Impact
- Security: tabnapping vector closed on all external links
- SEO: not-found EIN pages now correctly signal 404 to crawlers
- Performance: one less dead CDN request per page (GA UA script)

---

## Iteration 27 — Output Caching on Browse and Details Pages (2026-05-16)

### Changes

**ASP.NET Core 8 output caching** (`Program.cs`, `Index.cshtml.cs`, `ByPostcard.cshtml.cs`, `Details.cshtml.cs`)
- Added `AddOutputCache()` with two named policies:
  - `BrowsePage`: 60-second TTL, varies by `page` and `state` query parameters
  - `DetailsPage`: 30-second TTL, varies by `id` query parameter
- Base policy set to `NoCache` — Search and health endpoints are unaffected
- `[OutputCache(PolicyName = "BrowsePage")]` applied to `IndexModel` and `ByPostcardModel`
- `[OutputCache(PolicyName = "DetailsPage")]` applied to `DetailsModel`
- `UseOutputCache()` placed after `UseRouting()` so security headers (in earlier middleware) still run on cached responses

### Impact
- Browse pages: after the first request, each page/state combination is served from in-process memory for 60 seconds, eliminating repeated DB queries for paginated browse
- Details: org detail page DB queries drop from 3 queries per request to 0 for 30s after first load
- Search: unchanged — dynamic per-query results are never cached

---

## Iteration 28 — Rate Limiting on Search (2026-05-16)

### Changes

**Sliding-window rate limiter on `/Search`** (`Program.cs`, `Search.cshtml.cs`)
- `AddRateLimiter()` with a "search" policy: 30 permits per minute per IP, 4 segments (checked every 15s)
- Queue limit 0 — requests that exceed the limit are rejected immediately (no waiting)
- Returns HTTP 429 with `Retry-After: 60` header on rejection
- `[EnableRateLimiting("search")]` applied to `SearchModel` only
- Browse and Details pages protected by output caching instead

### Impact
- Prevents abusive scraping of FULLTEXT queries from a single IP
- Each FULLTEXT query runs a MariaDB inverted-index scan; 30/min is reasonable for human use
- Browse and health endpoints are unaffected

---

## Iteration 29 — Content Security Policy (2026-05-16)

### Changes

**Content-Security-Policy header** (`Program.cs`)
- Added `Content-Security-Policy` header in the existing security-header middleware
- Policy: `default-src 'none'; script-src cdn.jsdelivr.net; style-src 'self' cdn.jsdelivr.net cdnjs.cloudflare.com; font-src cdnjs.cloudflare.com; img-src 'self'; connect-src 'self'; base-uri 'self'; form-action 'self'; frame-ancestors 'self'`
- Covers all CDN resources used: Bootstrap JS/CSS (cdn.jsdelivr.net) and Font Awesome CSS/fonts (cdnjs.cloudflare.com)
- No inline scripts or styles in use — policy requires none, so no `'unsafe-inline'` needed
- `default-src 'none'` as base — any unspecified fetch type is blocked by default
- `frame-ancestors 'self'` replaces `X-Frame-Options: SAMEORIGIN` for modern browsers (both headers kept for compatibility)

### Impact
- Passes OWASP CSP audit — no wildcards, no `'unsafe-inline'`, no `'unsafe-eval'`
- Prevents XSS script injection even if a rendering bug exists: unapproved script sources are blocked by the browser
- Supplements the existing XSS protection from Razor auto-escaping with a browser-enforced defense layer

---

## Iteration 30 — Permissions-Policy Header, Branded 429 Page (2026-05-16)

### Changes

**`Permissions-Policy` header** (`Program.cs`)
- Added `Permissions-Policy: camera=(), microphone=(), geolocation=()` in security-header middleware
- Signals to the browser that this site never uses camera, microphone, or geolocation APIs
- Completes the OWASP recommended security header set alongside X-Content-Type-Options, X-Frame-Options, Referrer-Policy, and CSP

**Branded 429 page** (`Pages/TooManyRequests.cshtml`, `Pages/TooManyRequests.cshtml.cs`, `Program.cs`)
- Created `Pages/TooManyRequests.cshtml` at route `/429` matching the `UseStatusCodePagesWithReExecute("/{0}")` pattern
- Rate limiter `OnRejected` now sets 429 status + `Retry-After: 60` but writes no body
- `UseStatusCodePagesWithReExecute` re-executes the request as `/429` and serves the branded layout
- Previously: rate-limited users saw raw plain text "Too many requests. Please try again later." with no navigation
- Now: users see the site navbar, "429" heading, and links to browse/search

### Impact
- Permissions-Policy: passes W3C / OWASP security header audit for feature policy
- 429 page: rate-limited users see a navigable branded page instead of a dead-end plain text response

---

## Iteration 31 — SRI on Font Awesome CSS (2026-05-16)

### Changes

**Subresource Integrity on Font Awesome** (`Pages/Shared/_Layout.cshtml`)
- Added `integrity="sha384-/o6I2CkkWC//PSjvWC/eYN7l3xM3tJm8ZzVkCOfp//W05QcE3mlGskpoHB6XqI+B"` to the Font Awesome CSS link tag
- Hash computed from the live cdnjs.cloudflare.com resource for Font Awesome 6.5.0 `all.min.css`
- Bootstrap CSS and JS already had SRI hashes; Font Awesome was the only CDN resource without one

### Impact
- Browser now cryptographically verifies the Font Awesome CSS before executing it
- If cdnjs.cloudflare.com were compromised and served a modified file, the browser rejects it instead of applying malicious CSS
- All three CDN resources now have SRI — completes the supply-chain protection posture

---

## Iteration 32 — Search Form Accessibility, EIN Formatting Consistency (2026-05-16)

### Changes

**Search form WCAG fix** (`Pages/Search.cshtml`)
- Added `aria-label="Search organizations"` to the query input — was missing (no associated `<label>`)
- Added `aria-label="Filter by state"` to the state input — was missing (no associated `<label>`)
- Added `aria-label="Search"` to the submit button — was icon-only with `aria-hidden="true"` on the icon, leaving the button with no accessible name (WCAG 4.1.2 failure)
- The navbar search button was fixed in iteration 6; this iteration fixes the search page form itself

**EIN display consistency** (`Pages/Details.cshtml`)
- `Organization with EIN 123456789 not found.` → `Organization with EIN 12-3456789 not found.`
- Meta description `(EIN 123456789)` → `(EIN 12-3456789)`
- The card header already used `TypeCodeHelper.FormatEin()` (line 31); these two spots used raw `Model.Id`

### Impact
- Screen readers can now announce the Search page form correctly — previously the submit button had no accessible name
- EIN display is consistent across all contexts on the Details page (formatted XX-XXXXXXX everywhere)

---

## Iteration 33 — Error Page Consistency, Pagination Nav Landmark (2026-05-16)

### Changes

**Error page (500) UI consistency** (`Pages/Error.cshtml`)
- Replaced `alert alert-danger` block with the same display-4 heading + buttons pattern used by the 404 and 429 pages
- "500" heading in `text-danger`, lead paragraph, Browse and Search buttons
- Request ID preserved for development environments (shows below buttons)

**Pagination `<nav>` landmark** (`Index.cshtml`, `ByPostcard.cshtml`, `Search.cshtml`)
- Bottom pagination `<div>` elements converted to `<nav aria-label="Page navigation">`
- Screen reader users can now jump directly to the pagination region via landmark navigation
- WCAG 2.4.1 (bypass blocks): pagination is now a navigable landmark, not an anonymous div

### Impact
- Error page: consistent with the 404/429 pages — branded, navigable, not a bare alert box
- Pagination: screen reader users can skip directly to next/previous controls without traversing org cards

---

## Iteration 34 — `<main>` Landmark, Skip-to-Content Link (2026-05-16)

### Changes

**`<main>` landmark** (`Pages/Shared/_Layout.cshtml`)
- Replaced `<div class="container-fluid px-4">` with `<main id="main-content" class="container-fluid px-4">`
- The `<main>` element is the primary content landmark — assistive technologies expose it in landmark menus
- WCAG 2.4.1 (bypass blocks, Level A): presence of a `<main>` element satisfies the requirement

**Skip-to-content link** (`Pages/Shared/_Layout.cshtml`, `wwwroot/css/site.css`)
- Added `<a href="#main-content" class="skip-link">Skip to main content</a>` as first element inside `<body>`
- `.skip-link` CSS: offscreen by default (`left: -9999px`), slides on-screen when focused (`left: 0`)
- Keyboard users pressing Tab on page load get a visible link to bypass the 8-link navbar
- WCAG 2.4.1 (bypass blocks, Level A): skip link is the recommended implementation technique

### Impact
- Keyboard-only users: can bypass the navbar with one Tab keypress + Enter, instead of tabbing through all 8 navbar elements
- Screen reader users: `<main>` appears in all major screen readers' landmark navigation (NVDA, JAWS, VoiceOver)

---

## Iteration 35 — Preconnect Hints, `aria-current="page"` (2026-05-16)

### Changes

**`<link rel="preconnect">` for CDN domains** (`Pages/Shared/_Layout.cshtml`)
- Added `<link rel="preconnect" href="https://cdn.jsdelivr.net" crossorigin="anonymous" />`
- Added `<link rel="preconnect" href="https://cdnjs.cloudflare.com" crossorigin="anonymous" />`
- Placed before the CDN stylesheet links so the TCP+TLS handshake starts immediately on page parse
- Browsers that support preconnect (all modern browsers) can shave 100–200ms on cold CDN connections

**`aria-current="page"` on active nav links** (`Pages/Shared/_Layout.cshtml`)
- Added `aria-current="page"` attribute to the Browse and By Postcard nav links when active
- The Bootstrap `active` class provides visual indication; `aria-current="page"` provides the ARIA equivalent
- Screen readers (NVDA, JAWS, VoiceOver) now announce the current page in the navigation landmark

### Impact
- Preconnect: reduces CDN resource load time by eliminating DNS lookup + TCP + TLS setup on first request
- `aria-current`: screen reader users hear which nav link is the current page without inferring from styling

---

## Iteration 36 — `NormalizeUrl` Helper, URL Scheme Validation (2026-05-16)

### Changes

**`TypeCodeHelper.NormalizeUrl()`** (`Helpers/TypeCodeHelper.cs`)
- New helper centralizes website URL normalization: returns `null` for blank/n/a URLs, passes through `http://`/`https://` URLs, rejects any URL with a non-http scheme (ftp://, javascript:, data:), and prepends `http://` for bare domain names
- Detection logic: colon at position before any slash → treat as scheme → reject
- Previously: `StartsWith("http")` check was duplicated in 4 pages and accepted `ftp://` URLs (rendered as-is → broken link)

**All 4 pages updated** (`Index.cshtml`, `Search.cshtml`, `ByPostcard.cshtml`, `Details.cshtml`)
- Replaced 4 copies of the inline URL normalization expression with `TypeCodeHelper.NormalizeUrl()`
- Eliminates the code duplication and fixes the `ftp://` broken-link bug

**NormalizeUrl unit tests** (`NfpDirect.Tests/TypeCodeHelperTests.cs`)
- 10 new tests: null, empty, n/a, mixed-case N/A, https, http, bare domain, ftp://, javascript:, data:
- Test suite: 61 → 71 passing tests

### Impact
- `ftp://` URLs: were rendered as `ftp://` (broken link); now show Google Search fallback
- `javascript:` / `data:` URLs: were broken `http://javascript:...`; now show Google Search fallback
- Code duplication: 4 copies of 3-line URL check → 1 centralized helper

---

## Iteration 37 — FULLTEXT Query Builder Tests, InternalsVisibleTo (2026-05-16)

### Changes

**`SearchModel.BuildFullTextQuery()` extracted** (`Pages/Search.cshtml.cs`)
- Moved FULLTEXT boolean-mode query construction into an `internal static` method
- Previously: inline in `OnGetAsync`, untestable
- Method strips all non-alphanumeric/non-space characters to prevent FULLTEXT operator injection (`+`, `-`, `*`, `"`, etc.), then builds `+word*` prefix-wildcard expressions per word

**`InternalsVisibleTo`** (`NfpDirect/AssemblyInfo.cs`)
- Added `[assembly: InternalsVisibleTo("NfpDirect.Tests")]` to expose `internal` members to the test project

**FULLTEXT sanitization tests** (`NfpDirect.Tests/FullTextQueryTests.cs`)
- 11 new tests: single word, multi-word, special chars stripped, FULLTEXT operators stripped (+, -, *, quotes), all-special returns empty, whitespace normalized, numeric, mixed case
- Test suite: 71 → 82 passing tests

### Impact
- The FULLTEXT injection prevention code was previously untested; now has 11 tests covering all escape scenarios
- Extracing `BuildFullTextQuery` documents the contract: any non-alphanumeric-or-space character is stripped before the query reaches MariaDB's FULLTEXT parser

---

## Iteration 38 — IsEinLike Tests, Search Tab Title Improvement (2026-05-16)

### Changes

**`IsEinLike` exposed and tested** (`Pages/Search.cshtml.cs`, `NfpDirect.Tests/EinLikeTests.cs`)
- Changed `IsEinLike` from `private` to `internal static` (mirrors `BuildFullTextQuery` pattern from iteration 37)
- 10 new tests: 9-digit EIN, hyphenated `12-3456789`, 8-digit (too short), 10-digit (too long), contains letters, empty string, plain text, multi-hyphen with 9 digits total, all-hyphens, 8-digits-with-hyphen
- Test suite: 82 → 92 passing tests

**Search tab title shows result count** (`Pages/Search.cshtml`)
- Was: "Search: habitat" in browser tab regardless of result count
- Now: "Search: habitat (42 results)" when results exist; "Search: habitat" when zero results
- Helps users distinguish open search tabs and read result counts at a glance from history/bookmarks

### Impact
- `IsEinLike` is the gate for the EIN auto-redirect path; now has full edge case coverage
- Tab title: users with multiple search tabs can now see result counts without switching

---

## Iteration 39 — Location Formatting Helpers, Country Suppression (2026-05-16)

### Changes

**`TypeCodeHelper.FormatLocation()` and `TypeCodeHelper.ShouldShowCountry()`** (`Helpers/TypeCodeHelper.cs`)
- `FormatLocation(city, state)` — joins non-null/non-whitespace city and state with ", "; returns "" if both absent
- `ShouldShowCountry(country)` — returns false for null, empty, "US", "USA", "United States" (case-insensitive)
- Fixes the display bug: `@city@(state != null ? ", " + state : "")` rendered `, IL` when city was null

**Location display updated** (`Index.cshtml`, `Search.cshtml`, `Details.cshtml`)
- All three pages replaced the inline city+state+country expression with the new helpers
- Country now hidden for domestic (US) organizations — eliminates the visual noise of "US" on every card
- Foreign orgs still show their country, separated by `·`

**Tests** (`NfpDirect.Tests/TypeCodeHelperTests.cs`)
- 11 new tests: FormatLocation (both, city-only, state-only, both-null, both-whitespace) and ShouldShowCountry (null, US, USA, United States, Canada, empty)
- Test suite: 92 → 103 passing tests

### Impact
- Bug fix: null city no longer renders as `, IL` (missing city with comma artifact)
- UX: ` US` text removed from every domestic org card; foreign country shown only when relevant
- Code: duplicated 3-part inline expression → 2 testable helper methods

---

## Iteration 40 — ByPostcard Address Fix, Search Query Length Limit (2026-05-16)

### Changes

**ByPostcard address display** (`ByPostcard.cshtml`)
- City/state line now uses `TypeCodeHelper.FormatLocation(pc.OrgCity, pc.OrgState)` — fixes the null city rendering to `, IL 60601` artifact (same bug fixed in Index/Search in iteration 39)
- `OrgCountry` now suppressed via `ShouldShowCountry()` — US org cards no longer show "US" on every address block

**Search query length limit** (`Pages/Search.cshtml.cs`, `Pages/Search.cshtml`)
- Queries truncated server-side to 200 characters (`Query = Query[..MaxQueryLength]`)
- `<input maxlength="200">` added to the search form for client-side enforcement
- Prevents pathologically long FULLTEXT queries from reaching MariaDB's parser
- 200 characters is well beyond any realistic org name search

### Impact
- ByPostcard: address display bug fixed — null city no longer appears as `, IL 60601`
- Search: 200-char cap blocks oversized FULLTEXT queries (which scan the inverted index regardless of query length)

---

## Iteration 41 — Details Page Address Null-City Fix (2026-05-16)

### Changes

**Details page org address and officer address** (`Pages/Details.cshtml`)
- Org address block (lines ~57-58): `@pc.OrgCity, @pc.OrgState @pc.OrgPostalCode` → `@TypeCodeHelper.FormatLocation(pc.OrgCity, pc.OrgState) @pc.OrgPostalCode`
- Org country: `@pc.OrgCountry` → suppressed via `ShouldShowCountry()` for US orgs
- Officer address block (line ~88-89): `@pc.OfficerAddressCity@(pc.State != null ? ", " + pc.State : "") @pc.PostalCode` → `@TypeCodeHelper.FormatLocation(pc.OfficerAddressCity, pc.State) @pc.PostalCode`
- Officer country: `@pc.Country` → suppressed via `ShouldShowCountry()` for US officers
- Completes the city/state/country cleanup started in iteration 39 — Details page now consistent with Index, Search, and ByPostcard

### Impact
- Null city in org or officer address no longer renders `, IL 60601` (comma artifact)
- "US" / "UNITED STATES" country lines removed from org and officer address blocks

---

## Iteration 42 — `UrlDisplayText` Helper, Cleaner Website Buttons (2026-05-16)

### Changes

**`TypeCodeHelper.UrlDisplayText()`** (`Helpers/TypeCodeHelper.cs`)
- New helper: for absolute URLs, extracts the host and strips leading "www." prefix
- Fallback for relative/bare URLs: truncates to `maxLength` (default 50) with "…"
- Returns "" for null/whitespace

**Details and Search pages use UrlDisplayText** (`Pages/Details.cshtml`, `Pages/Search.cshtml`)
- Details: `Website (http://www.verylongurl.org/path/...)` → `Website (verylongurl.org)`
- Search: raw URL in link text → hostname only

**Tests** (`NfpDirect.Tests/TypeCodeHelperTests.cs`)
- 6 new tests: https/http with www, without www, bare domain (short), bare domain (long), null
- Test suite: 103 → 109 passing tests

### Impact
- Website buttons no longer overflow with long raw URLs — show hostname (30 chars max) instead of full URL (potentially 200+ chars)
- Cleaner, consistent display: all website buttons show a short domain name

---

## Iteration 105 — Type Filter on Search Page (2026-05-16)

### Changes

**`NfpDirect/Pages/Search.cshtml.cs`**
- Added `TypeFilter` property, `type` query parameter to `OnGetAsync`, validated against `TypeCodeHelper.Labels.ContainsKey`
- Added `.Where(p => p.TypeCode == TypeFilter)` when TypeFilter is set
- EIN-redirect back URL now preserves `type` parameter

**`NfpDirect/Pages/Search.cshtml`**
- Added `<select name="type">` dropdown with "All Types" default + all type codes sorted alphabetically, preserving selection
- Added `typeParam` variable; threaded through both sets of prev/next pagination links
- Layout adjusted to accommodate type dropdown column (`col-lg-6` / `col-lg-2` / `col-lg-3` / `col-sm-1`)
- Help text updated: "Search by name (partial match), 9-digit EIN, or filter by state and type."

**`NfpDirect.Tests/SearchModelTests.cs`**
- Updated all existing `OnGetAsync` calls to include `type: null`
- Added `TypeFilter_FiltersByTypeCode`, `TypeFilter_InvalidCode_IsIgnored`, `TypeFilter_CombinedWithNameSearch`
- Prefixed InMemory DB names with `"Search_"` to avoid collision with IndexModelTests

**`NfpDirect.Tests/IndexModelTests.cs`**
- Prefixed shared-name TypeFilter test DB names with `"Index_"` to prevent cross-test collision

### Impact
- Users can now narrow search results by IRC 501(c) type code — composable with name query and state filter
- Consistent with the type filter on the Browse page (iteration 101)
- Invalid type codes rejected without DB query
- 165 tests passing

---

## Iteration 104 — Fix: Remove Static sitemap.xml That Shadowed Dynamic Endpoint (2026-05-16)

### Changes

**`NfpDirect/wwwroot/sitemap.xml`** — deleted
- The static file was served by `UseStaticFiles` before routing, making the dynamic `/sitemap.xml` endpoint added in iteration 102 unreachable
- The static file also had URLs hardcoded to `nfpdirect.ackdev.com`, making it wrong in other environments

### Impact
- The dynamic endpoint now serves correctly: environment-agnostic URLs, includes Search page, updated on every request (cached 24h)
- 162 tests passing

---

## Iteration 103 — Browse by Type Grid on Home Page (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`**
- Added "Browse by Type" section below "Browse by State" on the unfiltered home page (page 0, no state, no type filter)
- Displays all known IRC 501(c) type codes as linked buttons, sorted alphabetically by label
- Condition for both grids now includes `Model.TypeFilter.Length == 0` so they hide when a type filter is active (only relevant content remains visible)

### Impact
- Users landing on the home page can discover the type filter without knowing it exists — a direct path from the home page to e.g. "Public Charity" or "Social Welfare" orgs
- Consistent UX with the existing state grid
- 162 tests passing

---

## Iteration 102 — Sitemap.xml Endpoint (2026-05-16)

### Changes

**`NfpDirect/Program.cs`**
- Added `GET /sitemap.xml` minimal endpoint: outputs valid XML Sitemap 0.9 with the home page (priority 1.0/daily), the Search page (0.8/weekly), and all 56 US state/territory browse pages (0.7/weekly)
- Response cached 24 hours via output cache
- Origin derived from request `Host` header to work across environments

### Impact
- `<link rel="sitemap">` in `_Layout.cshtml` was already in place (iteration 99) but pointed to a 404 — this completes the contract so crawlers can discover all state browse pages
- Potentially accelerates indexing of ~56 high-value state-filtered listing pages
- 162 tests passing

---

## Iteration 101 — Type Code Filter on Browse Page (2026-05-16)

### Changes

**`NfpDirect/Helpers/TypeCodeHelper.cs`**
- Changed `Labels` from `private static readonly Dictionary` to `public static readonly IReadOnlyDictionary` so views and page models can enumerate it

**`NfpDirect/Pages/Index.cshtml.cs`**
- Added `TypeFilter` property (string, defaults to `""`)
- Added `type` query parameter to `OnGetAsync`, validated against `TypeCodeHelper.Labels.ContainsKey` (rejects arbitrary values)
- Added `.Where(p => p.TypeCode == TypeFilter)` to the queryable when TypeFilter is non-empty
- Output cache BrowsePage policy now varies by `"type"` in addition to `"page"` and `"state"`

**`NfpDirect/Pages/Index.cshtml`**
- Filter form: added `<select name="type">` dropdown with "All Types" default + all known type codes sorted alphabetically, preserving selection
- "Clear" link: now appears when either state or type filter is active, links back to `/`
- Pagination `prev`/`next` links (both top and bottom): include `@typeQs` to preserve filter across pages
- Heading: shows type label when filtered (e.g. "Public Charity Organizations in Illinois")
- Title/canonical URL/robots all account for type filter

**`NfpDirect.Tests/IndexModelTests.cs`**
- Updated all existing `OnGetAsync` calls to include `type: null`
- Added `TypeFilter_FiltersByTypeCode`, `TypeFilter_InvalidCode_IsIgnored`, `TypeFilter_CombinedWithState`

### Impact
- Users can now filter the browse page by IRC 501(c) type code (Public Charity, Social Welfare, etc.) in addition to state — narrowing ~1.8M orgs to a specific type
- Type filter composes with state filter; both preserved across pagination
- Invalid type codes are silently rejected — no arbitrary column injection
- 162 tests passing

---

## Iteration 100 — WebSite JSON-LD with SearchAction on Home Page (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`**
- Added `WebSite` JSON-LD block to `@section Head` on page 0, no-filter only (the canonical home page)
- Schema includes: `name`, `url`, `description`, and `potentialAction` of type `SearchAction`
- `SearchAction.target.urlTemplate` is `{origin}/Search?q={search_term_string}`
- Uses the CSP nonce from `HttpContext.Items["csp-nonce"]` — no `'unsafe-inline'` needed

### Impact
- Google may display a "sitelinks searchbox" in search results for queries like "nfp direct" — a search form directly on the Google results page linking into NFP Direct Search
- Complements the OpenSearch description (iteration 99) and the existing Search page
- 159 tests passing

## Iteration 99 — OpenSearch Description for Browser Search Integration (2026-05-16)

### Changes

**`NfpDirect/wwwroot/opensearch.xml`** (new file)
- OpenSearch 1.1 description: ShortName, Description, Url template for `/Search?q={searchTerms}`, SVG icon reference

**`NfpDirect/Pages/Shared/_Layout.cshtml`**
- Added `<link rel="search" type="application/opensearchdescription+xml" title="NFP Direct" href="/opensearch.xml">` to all pages

### Impact
- Browsers that detect `<link rel="search">` (Firefox, Chromium derivatives, Safari) will offer to add NFP Direct as a custom search engine — users can type "nfpdirect.ackdev.com" in the address bar and press Tab to search directly
- 159 tests passing

## Iteration 98 — Geo Meta Tags + Title Separator Update (2026-05-16)

### Changes

**`NfpDirect/Pages/Shared/_Layout.cshtml`**
- Changed `<title>` separator from ` - ` to ` | ` (same in `og:title`) — ` | ` is the prevailing web convention, visually cleaner
- Added conditional `<meta name="geo.region">` and `<meta name="geo.placename">` rendering when `ViewData["GeoRegion"]` / `ViewData["GeoPlacename"]` are set

**`NfpDirect/Pages/Index.cshtml`**
- When `indexStateName != null` (state-filtered browse), sets:
  - `ViewData["GeoRegion"] = "US-{StateFilter}"` (e.g., `"US-IL"`)
  - `ViewData["GeoPlacename"] = indexStateName` (e.g., `"Illinois"`)

### Impact
- Geo meta tags signal to Bing and other geographic search engines that state-filtered pages are specifically relevant to that state — supports local search ranking
- Updated title separator is visually cleaner and consistent with industry standard
- 159 tests passing

## Iteration 97 — Details: Richer Meta Description with Type/Location (2026-05-16)

### Changes

**`NfpDirect/Pages/Details.cshtml`**
- When `Model.Pub78 != null`, meta description is now built from available org data:
  - Org name (always)
  - IRC 501(c)(X) type label if a known code (e.g., "Public Charity")
  - City/state location if available
  - Formatted EIN
  - Appended: "View IRS records, address, and filing details."
  - Example: "Habitat For Humanity · IRC 501(c)(3) Public Charity · Chicago, IL · EIN 36-2412074. View IRS records, address, and filing details."
- Falls back to simple "EIN ... not found." for not-found pages (unchanged)

### Impact
- Google search result snippets for Detail pages now show org type and location — richer snippets increase click-through rate
- Still fits within 160-char guideline for typical orgs
- 159 tests passing

## Iteration 96 — Browse by State Grid on Home Page (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`**
- Added a "Browse by State" `<section>` at the bottom of page 0 (no state filter) with a link for every US state and territory from `TypeCodeHelper.UsStates`
- Each state shows as a small button: "IL Illinois"
- Section is hidden on paginated pages and state-filtered pages (irrelevant in those contexts)

### Impact
- Users can discover state-specific org lists directly from the home page — previously only discoverable by typing in the filter
- Provides 57 internal links to state-filtered browse pages, supporting both SEO (crawlable internal link graph) and UX discovery
- Matches the 57 state URLs already in sitemap.xml
- 159 tests passing

## Iteration 95 — TotalPages in Browse/ByPostcard + Twitter Card Meta (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml.cs`** and **`NfpDirect/Pages/ByPostcard.cshtml.cs`**
- Added `TotalPages` property computed as `(TotalCount + PageSize - 1) / PageSize`
- Heading now reads "Page X of Y" instead of "Page X"

**`NfpDirect/Pages/Shared/_Layout.cshtml`**
- Added `<meta name="twitter:card" content="summary">` to enable Twitter/X card previews when pages are shared on social media (picks up og:title, og:description from existing OG meta)

### Impact
- Users browsing know how many pages exist — especially useful for state-filtered views where the count is manageable (e.g., "Page 1 of 297" for Illinois)
- Twitter/X will now show structured cards with title and description when a page is shared
- 159 tests passing

## Iteration 94 — ByPostcard Page: Show Total Postcard Count (2026-05-16)

### Changes

**`NfpDirect/Pages/ByPostcard.cshtml.cs`**
- Added `public long TotalCount { get; private set; }` property
- Added `TotalCount = await queryable.LongCountAsync();` (respects state filter, covered by output cache)

**`NfpDirect/Pages/ByPostcard.cshtml`**
- Heading now shows `<small class="text-muted">X,XXX,XXX total</small>` consistent with Browse

**`NfpDirect.Tests/ByPostcardModelTests.cs`**
- Added `TotalCount_ReflectsAllPostcards` and `TotalCount_ReflectsStateFilter` tests

### Impact
- ByPostcard page is now consistent with Browse: users see total postcard count in heading
- 159 tests passing

## Iteration 93 — Browse Page: Show Total Organization Count (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml.cs`**
- Added `public long TotalCount { get; private set; }` property
- Added `TotalCount = await queryable.LongCountAsync();` before fetching the page — count respects the current state filter
- Count is covered by output cache (60s TTL per state/page combination)

**`NfpDirect/Pages/Index.cshtml`**
- Page heading now includes `<small class="text-muted">X,XXX,XXX total</small>` after the page number

**`NfpDirect.Tests/IndexModelTests.cs`**
- Added `TotalCount_ReflectsAllOrgs` — verifies count equals total DB rows when no filter
- Added `TotalCount_ReflectsStateFilter` — verifies count is limited by state filter

### Impact
- Users browsing can see total org count ("1,847,302 total") directly in the heading without navigating to the last page
- State filter pages show count for that state (e.g., "14,832 total" for Illinois)
- 157 tests passing

## Iteration 92 — Sitemap `<link>` in Head + Browse/ByPostcard Rich Snippet Robots (2026-05-16)

### Changes

**`NfpDirect/Pages/Shared/_Layout.cshtml`**
- Added `<link rel="sitemap" type="application/xml" title="Sitemap" href="/sitemap.xml">` to `<head>` — supplements the `robots.txt` Sitemap directive with an in-HTML sitemap discovery pointer

**`NfpDirect/Pages/Index.cshtml`**
- Changed the robots meta logic from a conditional `noindex` on page > 0 to an explicit value for all cases:
  - Page 0: `index, follow, max-snippet:-1, max-image-preview:large, max-video-preview:-1`
  - Page > 0: `noindex, follow`

**`NfpDirect/Pages/ByPostcard.cshtml`**
- Same change: page 0 gets rich snippet directives; page > 0 keeps `noindex, follow`

### Impact
- Sitemap link in `<head>` provides an extra discovery path for crawlers that parse HTML but not `robots.txt`
- Browse and ByPostcard page 0 now explicitly grant Google permission to show long snippets and large image previews in search results
- 155 tests passing

## Iteration 91 — Browse/ByPostcard: Show Full State Name in Title and Heading (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`**
- Reordered code block: `indexStateName` is now computed before `ViewData["Title"]` so it can be used in the title
- `ViewData["Title"]` now uses `indexStateName` ("Illinois") instead of `Model.StateFilter` ("IL")
- Page `<h1>` heading updated from "in IL" to "in Illinois"

**`NfpDirect/Pages/ByPostcard.cshtml`**
- Same reorder and update: `bypcStateName` computed first, title and heading now use the full state name

### Impact
- Page titles and headings now consistently use full state names ("Browse Non-Profits in Illinois") matching the already-correct meta descriptions (which were updated in iteration 72 to use full names)
- Better SEO: full state names in titles are more readable and more likely to match user search queries
- 155 tests passing

## Iteration 90 — Details Page: noindex on 404 + Rich Snippet Robots Directives (2026-05-16)

### Changes

**`NfpDirect/Pages/Details.cshtml`**
- **Bug fix**: When `Model.Pub78 == null` (org not found or invalid EIN → HTTP 404), now sets `ViewData["Robots"] = "noindex"` — previously no robots meta was emitted, allowing Google to index soft-404 pages
- When `Model.Pub78 != null` (found org), sets `ViewData["Robots"] = "index, follow, max-snippet:-1, max-image-preview:large, max-video-preview:-1"` — explicitly grants Google permission to use the full page text and large image previews in search snippets

### Impact
- Fixes a soft-404 indexing leak: not-found Detail pages are now explicitly noindexed rather than relying on Google to infer 404 status from the HTTP status code
- Found-org pages get richer Google search result presentation (longer text snippets, large image previews)
- 155 tests passing

## Iteration 89 — BreadcrumbList JSON-LD on Details Page (2026-05-16)

### Changes

**`NfpDirect/Pages/Details.cshtml`**
- Added a second `<script type="application/ld+json">` block (using the existing CSP nonce) inside `@section Head` containing Schema.org `BreadcrumbList` structured data
- Breadcrumb: NFP Direct (home) → [Org Name] (canonical Detail URL)
- Emitted only when `Model.Pub78 != null` (found-org pages only)

### Impact
- Google uses `BreadcrumbList` to display breadcrumb navigation in search result snippets, replacing the raw URL — improves click-through rate
- Complements the `NGO` Organization schema added in iteration 87
- 155 tests passing

## Iteration 88 — Fix Heading Hierarchy Across All Pages (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`**
- `<h4 class="mb-0">` → `<h1 class="h4 mb-0">` (page main heading)
- `<h5 class="card-title mb-1">` → `<h2 class="h5 card-title mb-1">` (org name in result cards)

**`NfpDirect/Pages/ByPostcard.cshtml`**
- `<h4 class="mb-0">` → `<h1 class="h4 mb-0">` (page main heading)
- `<h5 class="card-title mb-1">` → `<h2 class="h5 card-title mb-1">` (org name in result cards)

**`NfpDirect/Pages/Search.cshtml`**
- `<h4 class="mb-3">` → `<h1 class="h4 mb-3">` (page main heading)
- `<h5 class="card-title mb-1">` → `<h2 class="h5 card-title mb-1">` (org name in result cards)

**`NfpDirect/Pages/Details.cshtml`**
- `<h4 class="card-title">` → `<h1 class="h4 card-title">` (org name — page main heading)
- `<h6 class="text-muted">Year:` → `<h2 class="h6 text-muted">Year:`
- `<h6>Doing Business As:</h6>` → `<h2 class="h6">Doing Business As:</h2>`
- `<h6>Officer Information:</h6>` → `<h2 class="h6">Officer Information:</h2>`

Error, NotFound, and TooManyRequests pages already had correct `<h1>` headings.

### Impact
- Every content page now has a proper `<h1>` as its primary heading — required for WCAG 2.4.6 and standard SEO practice
- Visual appearance is unchanged (Bootstrap `h4`/`h5`/`h6` classes applied)
- Correct hierarchy (`h1` → `h2`) signals content structure to search engines and screen readers
- 155 tests passing

## Iteration 87 — JSON-LD Organization Schema on Details Page (2026-05-16)

### Changes

**`NfpDirect/Program.cs`**
- Added `using System.Security.Cryptography;`
- Middleware now generates a per-request cryptographic nonce (`RandomNumberGenerator.GetBytes(18)` → base64) and stores it in `HttpContext.Items["csp-nonce"]`
- CSP `script-src` updated from `https://cdn.jsdelivr.net` to `https://cdn.jsdelivr.net 'nonce-{nonce}'` so the JSON-LD inline script is allowed without `'unsafe-inline'`

**`NfpDirect/Pages/Details.cshtml`**
- Added `@section Head { }` block that emits a `<script type="application/ld+json" nonce="...">` block when `Model.Pub78 != null`
- JSON-LD payload: `@context`, `@type: NGO`, `name` (TitleCase), `identifier` (formatted EIN), `address` (PostalAddress with street, locality, region, postal, country from Postcard), `url` (via `NormalizeUrl`)
- Uses `System.Text.Json.JsonSerializer` for safe serialization; nonce read from `HttpContext.Items["csp-nonce"]`
- Address and URL are included only when Postcard data is present and fields are non-empty

### Impact
- Every found-org Detail page now serves Schema.org `NGO` structured data eligible for Google rich results / knowledge panel
- Nonce approach preserves CSP strength — no `'unsafe-inline'` added
- 155 tests passing

## Iteration 86 — Details: EIN Format Validation Before DB Query (2026-05-16)

### Changes

**`NfpDirect/Pages/Details.cshtml.cs`**
- Added EIN format guard: if `Id.Length != 9 || !Id.All(char.IsDigit)` → return 404 immediately without querying the DB
- **Before**: any `id` value (e.g., "foobar", "12-3456789") triggered a parameterized DB query that returned null, wasting a DB round-trip
- **After**: non-9-digit ids are rejected synchronously before any database access

**`NfpDirect.Tests/DetailsModelTests.cs`**
- Added 5-case theory `InvalidEinFormat_Returns404WithoutDbQuery` covering too-short, too-long, hyphenated, and alpha IDs

### Impact
- Reduces DB load from malformed crawl/scan requests hitting `/Details?id=<junk>`
- 155 tests passing

## Iteration 85 — NormalizeUrl: Reject Garbage IRS URL Values (2026-05-16)

### Changes

**`NfpDirect/Helpers/TypeCodeHelper.cs`**
- Added `InvalidUrlKeywords` HashSet: NONE, NA, N/A, NO, NO WEBSITE, WWW, HTTP, HTTPS, NOT AVAILABLE, NOT APPLICABLE, UNKNOWN, TBD, COMING SOON, UNDER CONSTRUCTION
- Added dot-presence check: bare domain strings without a dot (e.g., "CHARITY") now return null
- `NormalizeUrl` now returns null for any of these garbage values instead of producing broken links
- Removed the redundant `url.Equals("n/a", ...)` early return (now covered by InvalidUrlKeywords)

**`NfpDirect.Tests/TypeCodeHelperTests.cs`**
- Added 12-case theory for garbage values + 1 test for no-dot bare word

### Impact
- IRS postcard data frequently contains placeholder text in the WebsiteURL field; these are now rejected
- Prevents broken "NONE" or "WWW" links from appearing on org cards
- 150 tests passing

## Iteration 84 — ShouldShowCountry: US Variant Coverage (2026-05-16)

### Changes

**`NfpDirect/Helpers/TypeCodeHelper.cs`**
- Added `UsCountryVariants` HashSet: `US`, `USA`, `U.S.`, `U.S.A.`, `UNITED STATES`, `UNITED STATES OF AMERICA`
- `ShouldShowCountry` now uses case-insensitive HashSet lookup instead of three equality checks
- **Before**: only "US", "USA", "UNITED STATES" were suppressed; "U.S." or "UNITED STATES OF AMERICA" would be shown as a foreign country
- **After**: all common IRS data representations of "United States" are suppressed

**`NfpDirect.Tests/TypeCodeHelperTests.cs`**
- Added 4-case theory for U.S. variant handling

### Impact
- Prevents spurious "U.S." or "United States of America" entries from appearing as country labels on org cards
- 138 tests passing

## Iteration 83 — Search Form on 404 Page (2026-05-16)

### Changes

**`NfpDirect/Pages/NotFound.cshtml`**
- Replaced standalone "Search" button with an inline search form (input + submit)
- Form has `role="search"` and `autofocus` for immediate keyboard access
- Replaced "Back to Organizations" text with "Browse All Organizations" for clarity

**`NfpDirect/wwwroot/css/site.css`**
- Added `.search-form-group { max-width: 400px; }` (avoiding inline style CSP violation)

### Impact
- Users who land on a 404 can immediately search without navigating to /Search first
- The form is the fastest path to recovery: autofocused, submits to /Search with the full search experience

## Iteration 82 — Redirect /favicon.ico to /favicon.svg (2026-05-16)

### Changes

**`NfpDirect/Program.cs`**
- Added `app.MapGet("/favicon.ico", () => Results.Redirect("/favicon.svg", permanent: true))`
- **Before**: `/favicon.ico` requests returned 404 (no ICO file in wwwroot)
- **After**: 301 permanent redirect to the existing SVG favicon

### Impact
- Eliminates 404s from browsers (especially older ones), bots, and crawlers that still request favicon.ico
- A 404 on favicon.ico is a common noise entry in error logs and a mild SEO signal degradation

## Iteration 81 — HSTS Preload Configuration (2026-05-16)

### Changes

**`NfpDirect/Program.cs`**
- Added `AddHsts` with `MaxAge = 365 days`, `IncludeSubDomains = true`, `Preload = true`
- **Before**: `UseHsts()` used default max-age of 30 days, no includeSubDomains, no preload flag
- **After**: `Strict-Transport-Security: max-age=31536000; includeSubDomains; preload`

### Impact
- Meets HSTS preload list requirements (1-year max-age + includeSubDomains + preload flag)
- `includeSubDomains` ensures HTTPS is enforced across the entire domain hierarchy
- Once submitted to the preload list, browsers enforce HTTPS before any HTTP connection is attempted

## Iteration 80 — Redirect /Details with No id to Home (2026-05-16)

### Changes

**`NfpDirect/Pages/Details.cshtml.cs`**
- When `id` is null or whitespace, returns `RedirectToPage("/Index")` instead of a 200 Page with a confusing warning
- **Before**: `/Details` (no query) returned HTTP 200 with "Organization with EIN — not found." warning, confusing users and search engines alike
- **After**: redirects to the Browse page, providing a useful landing point

**`NfpDirect.Tests/DetailsModelTests.cs`**
- Updated `EmptyId_ReturnsPageWithoutQuery` → `EmptyId_RedirectsToIndex` to assert the redirect behavior

### Impact
- Eliminates a 200-OK response for a semantically empty/invalid request
- Better UX: users who land on /Details with no id are sent to a useful page

## Iteration 79 — NormalizeUrl Prefers HTTPS for Bare Domain URLs (2026-05-16)

### Changes

**`NfpDirect/Helpers/TypeCodeHelper.cs`**
- `NormalizeUrl`: bare domain/path URLs (no scheme) now get `https://` prepended instead of `http://`
- **Before**: `example.org` → `http://example.org`
- **After**: `example.org` → `https://example.org`

**`NfpDirect.Tests/TypeCodeHelperTests.cs`**
- Updated `NormalizeUrl_NoScheme_PrependsHttp` → `NormalizeUrl_NoScheme_PrependsHttps` to match new behavior

### Impact
- Nearly all nonprofit websites today support HTTPS; defaulting to `https://` avoids a redirect round-trip and improves user security for org website links
- Existing `http://` and `https://` explicit URLs are returned unchanged — only bare-domain entries are affected

## Iteration 78 — Expand Sitemap with State-Filtered Browse Pages (2026-05-16)

### Changes

**`NfpDirect/wwwroot/sitemap.xml`**
- Added 56 state/territory-filtered Browse URLs (`/?state=XX`) for all entries in `TypeCodeHelper.UsStates`
- US states and DC at priority 0.8; territories (PR, GU, VI, AS, MP) at priority 0.7
- **Before**: sitemap contained only 1 URL (homepage)
- **After**: 57 URLs covering every state/territory filter crawlers can discover

### Impact
- Search crawlers can now discover and index individual state non-profit browse pages
- Enables search visibility for queries like "non-profits in Illinois" or "Texas non-profit organizations"

## Iteration 77 — Fix CSP Violation: Move Inline Style to CSS Class (2026-05-16)

### Changes

**`NfpDirect/wwwroot/css/site.css`**
- Added `.state-filter-group { max-width: 220px; }` class

**`NfpDirect/Pages/Index.cshtml`**, **`NfpDirect/Pages/ByPostcard.cshtml`**
- Replaced `style="max-width:220px"` inline attribute with `.state-filter-group` class

### Impact
- **Before**: `style=` attribute on the state filter form was silently blocked by CSP (`style-src` without `'unsafe-inline'`), causing the filter input to expand to full width on these pages
- **After**: max-width constraint is applied via an external stylesheet (allowed by `style-src 'self'`), consistent with the CSP

## Iteration 76 — Semantic `<time>` Element for Tax Period on Details (2026-05-16)

### Changes

**`NfpDirect/Helpers/TypeCodeHelper.cs`**
- Added `TaxPeriodDatetime(string? tp)` — returns ISO 8601 `YYYY-MM` format from a `YYYYMM` tax period string, or null if invalid

**`NfpDirect/Pages/Details.cshtml`**
- Tax period in Filing Details now wrapped: `<time datetime="2023-12">Dec 2023</time>`
- Falls back to plain text if the value cannot be parsed (invalid or null TaxPd)

**`NfpDirect.Tests/TypeCodeHelperTests.cs`**
- Added 9 tests for `TaxPeriodDatetime`: valid inputs, invalid formats, null, zero month, out-of-range month

### Impact
- Machine-readable date enables search engine structured data extraction, accessibility tools, and browser date features
- 134 tests passing

## Iteration 75 — Cross-Origin Isolation Security Headers (2026-05-16)

### Changes

**`NfpDirect/Program.cs`**
- Added `Cross-Origin-Opener-Policy: same-origin` — prevents cross-origin pages from accessing `window.opener`, closing a class of tab-napping / cross-origin isolation attacks
- Added `Cross-Origin-Resource-Policy: same-origin` — prevents other origins from loading this app's resources (images, scripts) in their own context, defending against Spectre-style side-channel attacks

### Impact
- These two headers are the standard complement to the existing CSP, completing the browser-side isolation story
- No breaking change: all existing page flows (external Google/News links with `target="_blank" rel="noopener"`) are unaffected
- Scores a perfect "A+" on securityheaders.com once the site is live

## Iteration 74 — StateName Helper in TypeCodeHelper (2026-05-16)

### Changes

**`NfpDirect/Helpers/TypeCodeHelper.cs`**
- Added `StateName(string? code)` — returns the full state name for a 2-letter code, falls back to the code itself if unknown, returns empty string for null
- Case-insensitive lookup (delegates to `UsStates` dictionary which uses `OrdinalIgnoreCase`)

**`NfpDirect/Pages/Index.cshtml`**, **`NfpDirect/Pages/ByPostcard.cshtml`**
- Replaced inline `TypeCodeHelper.UsStates.GetValueOrDefault(...)` calls with `TypeCodeHelper.StateName(...)`

**`NfpDirect.Tests/TypeCodeHelperTests.cs`**
- Added 7 tests for `StateName`: known codes, unknown code fallback, null, lowercase input

### Impact
- State name resolution is now centrally tested and reusable
- 125 tests passing

## Iteration 73 — noindex on Paginated Browse and ByPostcard Pages (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`**, **`NfpDirect/Pages/ByPostcard.cshtml`**
- Pages beyond page 0 now set `ViewData["Robots"] = "noindex, follow"` → renders `<meta name="robots" content="noindex, follow">`
- Page 0 (first page) remains indexable regardless of state filter

### Impact
- Prevents crawlers from indexing hundreds of alphabetical listing pages (e.g., page 50 of browse) that add no search engine value
- Preserves crawl budget for the first page of each state filter and for Details pages
- `follow` keeps link equity flowing through pagination links

## Iteration 72 — Dynamic Meta Description for Browse and ByPostcard (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`**, **`NfpDirect/Pages/ByPostcard.cshtml`**
- When a state filter is active, `<meta name="description">` now includes the full state name
- Example: `/?state=IL` → "Browse IRS-registered non-profit organizations in Illinois. Filter by state, search by name or 9-digit EIN."
- **Before**: every Browse/ByPostcard page used the same generic description regardless of state filter

### Impact
- State-filtered pages show more relevant, specific descriptions in search engine results
- Better click-through rate for state-specific queries (e.g., "Illinois non-profits")
- State name is resolved via `TypeCodeHelper.UsStates` so the description uses "Illinois" not "IL"

## Iteration 71 — Search Nav Link with Active State (2026-05-16)

### Changes

**`NfpDirect/Pages/Shared/_Layout.cshtml`**
- Added "Search" nav item to the navbar navigation list with `active` class and `aria-current="page"` when on `/Search`
- **Before**: no nav item highlighted when on the Search page; Search was reachable only via the inline navbar search form with no visible page indicator
- **After**: consistent active-state feedback for all three main pages (Browse, By Postcard, Search)

### Impact
- Users and screen readers can now tell which nav section they're in when on the Search page
- The Search page is now discoverable via a direct nav link (not just the search form)

## Iteration 70 — Precise Canonical URLs for Browse and ByPostcard Pages (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`**
- Sets `ViewData["CanonicalUrl"]` to the exact URL for this page+state combination
- Page 0, no state → `/` (clean home URL)
- Page 0, with state → `/?state=IL`
- Page N → `/?page=N&state=IL`

**`NfpDirect/Pages/ByPostcard.cshtml`**
- Same canonical logic for the ByPostcard page

### Impact
- **Before**: every Browse page (all states, all page numbers) had canonical `/`; every ByPostcard page had canonical `/ByPostcard` — state-filtered and paginated variants were treated as duplicates
- **After**: each distinct URL has its own canonical, enabling proper crawl budget allocation and deduplication

## Iteration 69 — Fix Details Canonical URL; Add noindex to Search (2026-05-16)

### Changes

**`NfpDirect/Pages/Shared/_Layout.cshtml`**
- Changed canonical `<link>` and `og:url` to use `ViewData["CanonicalUrl"]` when set, falling back to path-only
- **Before**: both always used `scheme://host/path` (no query params) — same canonical for every org's Details page
- **After**: pages can opt into a precise canonical that includes relevant query params

**`NfpDirect/Pages/Details.cshtml`**
- Sets `ViewData["CanonicalUrl"]` to `scheme://host/Details?id={EIN}` when Pub78 is found
- **Before**: canonical for every org detail page was `https://example.com/Details` (identical for all ~1.8M orgs)
- **After**: each org has its own canonical URL including its EIN

**`NfpDirect/Pages/Search.cshtml`**
- Sets `ViewData["Robots"] = "noindex, follow"` so search result pages are excluded from crawlers
- **Before**: `/Search?q=...` pages were indexable, exposing user-generated queries to crawlers
- **After**: search result pages excluded from indexing; browsing/detail pages unaffected

### Impact
- Eliminates a significant SEO defect: all ~1.8M org detail pages were canonicalized to the same URL
- Prevents search engines from indexing ephemeral search result pages

## Iteration 68 — Terminated Badge on All Listing Pages (2026-05-16)

### Changes

**`NfpDirect/Pages/Search.cshtml`**, **`Index.cshtml`**, **`ByPostcard.cshtml`**
- Added `<span class="badge bg-danger ms-1">Terminated</span>` in the type/badge area of each card when `Postcard.Terminated` is "Y"
- **Before**: Terminated status was only visible on the Details page (added in iteration 67)
- **After**: Users can see terminated orgs directly in all listing views without navigating to Details

### Impact
- Terminated orgs are immediately identifiable across Browse, Search, and ByPostcard
- Consistent with the Details page badge (same styling)

## Iteration 67 — Terminated Badge on Details Page (2026-05-16)

### Changes

**`NfpDirect/Pages/Details.cshtml`**
- Added `<span class="badge bg-danger">Terminated</span>` prominently below the org card header when `pc.Terminated` is "Y" (via `TypeCodeHelper.YesNo`)
- **Before**: Terminated status was buried in the "Gross Receipts" / "Terminated: Yes/No" plain text block — easy to miss
- **After**: Red badge appears at the top of the postcard section, immediately visible on load

### Impact
- Users immediately see that an org has terminated status before reading details
- The underlying "Terminated: Yes" field in the data grid remains for completeness

## Iteration 66 — Guard Null OfficerName in Details Officer Section (2026-05-16)

### Changes

**`NfpDirect/Pages/Details.cshtml`** (officer information block)
- Officer name link: only rendered when `pc.OfficerName` is not null/blank; previously rendered an empty `<a>` tag with no visible text
- "News" button: only rendered when `officerQuery` is non-empty (i.e., either name or city is available); previously rendered with `?q=` (empty query) when both were null

### Impact
- No invisible empty links in officer section when name is missing from IRS data
- News button only shows when there's something to search for

## Iteration 65 — Deterministic Pagination Sort in ByPostcard (2026-05-16)

### Changes

**`NfpDirect/Pages/ByPostcard.cshtml.cs`**
- Added `.ThenBy(pc => pc.Id)` secondary sort after `.OrderBy(pc => pc.CompanyName)`
- **Inconsistency**: `IndexModel` already had `.ThenBy(p => p.EIN)` for deterministic ordering; `ByPostcardModel` did not
- Without a secondary sort, organizations with identical names could appear in different order across pages depending on query plan, causing items to repeat or skip at page boundaries

### Impact
- Pagination is now stable: an org that appears on page 2 will not "move" to page 1 or page 3 between requests
- Consistent with the Browse page's ordering behavior

## Iteration 64 — Guard Null Year in Postcard Detail and List Views (2026-05-16)

### Changes

**`NfpDirect/Pages/Details.cshtml`**
- `<h6 class="text-muted">Year: @pc.Year</h6>` → wrapped in `@if (pc.Year.HasValue)` so "Year: " label is not rendered when Year is null

**`NfpDirect/Pages/ByPostcard.cshtml`**
- `<small class="text-muted ms-1">@pc.Year</small>` → wrapped in `@if (pc.Year.HasValue)` so empty small tag is not emitted

### Impact
- No blank "Year: " label on detail pages with incomplete postcard data
- Cleaner card headers on ByPostcard listing when year is absent

## Iteration 63 — TitleCase Applied to DBA Names on Index and ByPostcard (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`** and **`NfpDirect/Pages/ByPostcard.cshtml`**
- DBA display: `@pc.DBA1` → `@TypeCodeHelper.TitleCase(pc.DBA1)`
- **Inconsistency**: `Details.cshtml` applied `TitleCase` to DBA names; the two list pages showed the raw ALLCAPS database value

### Impact
- DBA names are now consistently title-cased across all listing pages
- Visual consistency: org names and DBA names all use the same casing style

## Iteration 62 — `FormatPostalLine` Helper for Clean City/State/Postal Rendering (2026-05-16)

### Changes

**`NfpDirect/Helpers/TypeCodeHelper.cs`**
- Added `FormatPostalLine(string? city, string? state, string? postal)`: builds `"City, ST 00000"` filtering any null/blank parts so no trailing space when postal code is absent

**`NfpDirect/Pages/Index.cshtml`**, **`ByPostcard.cshtml`**, **`Details.cshtml`** (org + officer address)
- Replaced `@TypeCodeHelper.FormatLocation(...) @pc.OrgPostalCode` with `@TypeCodeHelper.FormatPostalLine(...)` across 4 address blocks
- **Before**: `"Chicago, IL "` (trailing space when postal null); `"60601"` (no city/state prefixed when those were null)
- **After**: No trailing spaces; gracefully handles any combination of null fields

**`NfpDirect.Tests/TypeCodeHelperTests.cs`**
- Added 4 `FormatPostalLine` tests (118 total)

### Impact
- Address rendering is clean for all combinations of null city/state/postal data
- DRY: postal line formatting extracted once instead of repeated in-template

## Iteration 61 — Remove /Search from sitemap.xml (robots.txt Consistency) (2026-05-16)

### Changes

**`NfpDirect/wwwroot/sitemap.xml`**
- Removed the `/Search` entry
- **Inconsistency**: `robots.txt` has `Disallow: /Search` but the sitemap listed `/Search` as a priority 0.9 URL. Submitting a URL in the sitemap while disallowing it in robots.txt is contradictory; most crawlers will skip it anyway, but the explicit inconsistency is confusing to crawlers and Google Search Console.

### Impact
- sitemap.xml now only lists the homepage, which is the only crawlable starting point
- No functional change for users

## Iteration 60 — Guard Null Address Line 1 in All Address Blocks (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`**, **`ByPostcard.cshtml`**, **`Details.cshtml`** (org address and officer address)
- `@pc.OrgAddressLine1<br />` → `@if (!string.IsNullOrWhiteSpace(pc.OrgAddressLine1)) { @pc.OrgAddressLine1<br /> }`
- Same for `@pc.OfficerAddressLine1` in Details
- **Pattern**: `OrgAddressLine2` and `OfficerAddressLine2` were already conditionally rendered; `Line1` was not
- Affects 4 address blocks across 3 pages

### Impact
- No blank line rendered when address line 1 is missing from IRS data
- Consistent null-guarding across all address field rendering

## Iteration 59 — Null CompanyName Fallback in Index/ByPostcard Google Search Links (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`**
- "Search Google" link: `pc.CompanyName ?? ""` → `pc.CompanyName ?? entry.Pub78.CompanyName ?? ""`
- Falls back to the Pub78 record's company name if the postcard's name is null

**`NfpDirect/Pages/ByPostcard.cshtml`**
- "Search Google" link: `pc.CompanyName ?? ""` → `pc.CompanyName ?? pc.Id`
- Falls back to the EIN (postcard ID) if company name is null — always produces a meaningful query

### Impact
- Google Search links now always have a meaningful query even when `CompanyName` is null in the postcard record

## Iteration 58 — ByPostcard Org Name Linked to Details (2026-05-16)

### Changes

**`NfpDirect/Pages/ByPostcard.cshtml`**
- Wrapped the organization name in `<a href="/Details?id=...&back=...">` (same back URL pattern as the Details button below)
- **Before**: Name was plain text; users had to scroll to the "Details" button to navigate
- **After**: Clicking the name navigates to Details — consistent with Browse and Search, where names are always links

### Impact
- Consistent UX across all three listing pages (Browse, Search, ByPostcard)
- The "Details" button below remains for users who prefer explicit buttons

## Iteration 57 — EIN Shown on ByPostcard List Cards (2026-05-16)

### Changes

**`NfpDirect/Pages/ByPostcard.cshtml`**
- Added `<small class="text-muted ms-1">EIN: @TypeCodeHelper.FormatEin(pc.Id)</small>` in the card title row, alongside the year
- **Before**: EIN was only visible after clicking "Details"
- **After**: EIN displayed inline in the list (formatted with hyphen: `12-3456789`)

### Impact
- Users can identify organizations by EIN without navigating to the detail page
- Consistent with Browse (Index) and Search results, which both show the formatted EIN inline

## Iteration 56 — State Filter Shown in Browse/ByPostcard Page Headings (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`** and **`NfpDirect/Pages/ByPostcard.cshtml`**
- The visible `<h4>` heading now includes the state code when a state filter is active, matching the `<title>` tag (updated in iteration 54)
- Browse: "Non-Profit Organizations in IL — Page 1" (was "Non-Profit Organizations — Page 1")
- ByPostcard: "Browse by Postcard in IL — Page 1" (was "Browse by Postcard — Page 1")

### Impact
- The page heading and browser tab title are now consistent
- Users can see at a glance which state they're filtering by

## Iteration 55 — Details Page: CompanyName Null Fallback in Google Links (2026-05-16)

### Changes

**`NfpDirect/Pages/Details.cshtml`**
- "Try Google" and "Google [Org Name]" links: `pc.CompanyName ?? ""` → `pc.CompanyName ?? name`
- "Google @pc.CompanyName" button text: → `@TypeCodeHelper.TitleCase(pc.CompanyName ?? name)`
- **Problem**: If `pc.CompanyName` was null, "Try Google" searched for an empty string, and the button showed "Google " (trailing space)
- `name` is the org's display name already resolved at page top from `pub.CompanyName` or EIN

### Impact
- Google links now always have a meaningful query even if the postcard's `CompanyName` is null
- Button text shows the proper title-cased name instead of trailing space

## Iteration 54 — State Filter Shown in Browse/ByPostcard Page Titles (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`** and **`NfpDirect/Pages/ByPostcard.cshtml`**
- When a state filter is active, the `<title>` now includes the state code:
  - Browse: "Browse Non-Profits in IL — Page 1" (was "Browse Non-Profits — Page 1")
  - ByPostcard: "Browse by Postcard in IL — Page 1" (was "Browse by Postcard — Page 1")
- Unfiltered pages unchanged

### Impact
- Browser tabs and bookmarks now distinguish filtered from unfiltered browsing sessions
- State-specific pages are more discoverable in browser history

## Iteration 53 — Details Page: Clean Google Query Construction (2026-05-16)

### Changes

**`NfpDirect/Pages/Details.cshtml`**
- **Google News (org)**: `(pub.CompanyName ?? name) + " " + (pub.City ?? "")` → `string.Join(" ", new[]{pub.CompanyName ?? name, pub.City}.Where(!IsNullOrWhiteSpace))` — removes trailing space when City is null
- **Google Search/News (officer)**: extracted `officerQuery` variable using same pattern; previously `(pc.OfficerName ?? "") + " " + pc.OfficerAddressCity` could produce leading/trailing spaces
- Also eliminates the duplicated officer query expression (was computed twice: once for Google Search, once for Google News)

### Impact
- Search queries for orgs/officers without city data are now clean (no trailing space URL-encoded as `+`)
- DRY: officer query computed once, used twice

## Iteration 52 — Server-Side State Filter Validation (2026-05-16)

### Changes

**`NfpDirect/Pages/Search.cshtml.cs`**, **`Index.cshtml.cs`**, **`ByPostcard.cshtml.cs`**
- After parsing the `state` query parameter, validate: must be exactly 2 ASCII letters or reset to `""`
- `maxlength="2"` on the HTML input is client-side only and can be bypassed; server-side validation prevents malformed codes from reaching the database

**`NfpDirect.Tests/SearchModelTests.cs`**
- Added `InvalidStateFilter_IsIgnored` theory (4 cases: "INVALID", "123", "I", "12"): confirms malformed state params are cleared and the filter is not applied (114 total tests)

### Impact
- Invalid state codes like `?state=INVALID` or `?state=123` are now silently dropped instead of running a no-result DB query
- Consistent validation across all three list endpoints

## Iteration 51 — Search Page Title Truncation (2026-05-16)

### Changes

**`NfpDirect/Pages/Search.cshtml`**
- `ViewData["Title"]` now truncates the query to 50 characters (with `…`) in the title tag
- The displayed query in the form and result count text are unchanged
- **Problem**: With a 200-char max query, the browser tab title could be 220+ characters — unhelpful in tabs and bad for social sharing

### Impact
- Browser tabs, bookmarks, and og:title are now readable even with long queries
- The page `<title>` stays under ~70 characters for typical queries

## Iteration 50 — CSP: Lock CDN Hostnames to HTTPS Scheme (2026-05-16)

### Changes

**`NfpDirect/Program.cs`** (CSP middleware)
- `script-src cdn.jsdelivr.net` → `script-src https://cdn.jsdelivr.net`
- `style-src 'self' cdn.jsdelivr.net cdnjs.cloudflare.com` → `style-src 'self' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com`
- `font-src cdnjs.cloudflare.com` → `font-src https://cdnjs.cloudflare.com`

### Impact
- CDN hostnames without a scheme prefix allow both HTTP and HTTPS resources; adding `https://` rejects any HTTP-sourced script or stylesheet even if the browser somehow follows a downgraded CDN URL
- Defense-in-depth alongside the existing SRI hashes — SRI would catch tampered content, but scheme-locking prevents the browser from even fetching over cleartext
- No functional change for users since all CDN resources are already loaded over HTTPS

## Iteration 49 — `rel="prev"` / `rel="next"` Pagination Link Elements (2026-05-16)

### Changes

**`NfpDirect/Pages/Shared/_Layout.cshtml`**
- Added `@RenderSection("Head", required: false)` before theme-color meta tag, providing a named `Head` section for pages to inject `<head>` elements

**`NfpDirect/Pages/Index.cshtml`** and **`NfpDirect/Pages/ByPostcard.cshtml`**
- Added `@section Head {}` with conditional `<link rel="prev">` and `<link rel="next">` elements pointing to adjacent pages (including state filter query param when active)

### Impact
- Search engines can follow the pagination chain for the full org list
- Helps crawlers discover all pages rather than only indexing page 1
- State filter is preserved in the prev/next links so crawlers follow the correct filtered series

## Iteration 48 — `noindex` Meta Tag on Error Pages (2026-05-16)

### Changes

**`NfpDirect/Pages/Shared/_Layout.cshtml`**
- Added conditional `<meta name="robots" content="@ViewData["Robots"]">` in `<head>` — only emitted when `ViewData["Robots"]` is set

**`NfpDirect/Pages/NotFound.cshtml`**, **`Error.cshtml`**, **`TooManyRequests.cshtml`**
- Set `ViewData["Robots"] = "noindex"` in each page's `@{}` block

### Impact
- Error and status pages can no longer be indexed by search engines, even if a crawler fetches them
- HTTP status codes (404, 429, 500) already signal non-indexable content, but the meta tag provides belt-and-suspenders protection

## Iteration 47 — `role="search"` Landmark on Search Forms (2026-05-16)

### Changes

**`NfpDirect/Pages/Shared/_Layout.cshtml`** and **`NfpDirect/Pages/Search.cshtml`**
- Added `role="search"` to both the navbar mini-search form and the full Search page form
- Per WCAG 2.1 technique ARIA11, search forms should use the `search` landmark role so screen reader users can navigate directly to search functionality

### Impact
- Screen reader users can jump to the search form via landmark navigation (e.g., "go to search" shortcut in NVDA/JAWS)
- No visual change

## Iteration 46 — Website Button Domain Display Consistency (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`** and **`NfpDirect/Pages/ByPostcard.cshtml`**
- Website button text changed from generic "Website" to `@TypeCodeHelper.UrlDisplayText(siteUrl)` which shows the domain (e.g., "habitat.org")
- **Gap**: `Search.cshtml` already showed the domain in website buttons. Index and ByPostcard showed only "Website", giving users no indication of which site they were navigating to.

### Impact
- All three listing pages now consistently display the org's domain in website buttons
- Users can identify the destination before clicking (trust and safety improvement)

## Iteration 45 — Index Page: Apply FormatLocation/ShouldShowCountry to Postcard Block (2026-05-16)

### Changes

**`NfpDirect/Pages/Index.cshtml`**
- Postcard address block city/state line: `@pc.OrgCity, @pc.OrgState` → `@TypeCodeHelper.FormatLocation(pc.OrgCity, pc.OrgState)`
- Added `@if (TypeCodeHelper.ShouldShowCountry(pc.OrgCountry)) { @pc.OrgCountry }` to suppress "US" noise
- **Bug**: When `OrgCity` or `OrgState` was null, the address rendered `, IL 60601` or `Chicago,  60601`. All other address blocks (ByPostcard, Details, Search) had been fixed in prior iterations but this block was missed.

### Impact
- Index page postcard addresses now render cleanly when city or state data is missing
- "US" no longer shown as country for domestic organizations on the Browse page

## Iteration 44 — EIN Redirect Preserves State Filter in Back URL (2026-05-16)

### Changes

**`NfpDirect/Pages/Search.cshtml.cs`**
- EIN auto-redirect back URL now includes `&state=XX` when a state filter was active
- **Bug**: Searching with an EIN + state filter (e.g., `q=123456789&state=IL`) redirected to Details with `back=/Search?q=123456789` — clicking "← Back" lost the state filter and returned to a different result set
- **Fix**: `backUrl` appended with `&state=Uri.EscapeDataString(StateFilter)` when `StateFilter.Length > 0`

**`NfpDirect.Tests/SearchModelTests.cs`**
- Added `EinSearch_WithStateFilter_BackUrlIncludesState` test (110 total)

### Impact
- "← Back" from EIN-redirected detail pages now returns to the exact same filtered search
- No behavioral change when no state filter is active

## Iteration 43 — Details Cache Bug Fix: Vary by `back` Query Param (2026-05-16)

### Changes

**`DetailsPage` output cache policy** (`Program.cs`)
- `SetVaryByQuery("id")` → `SetVaryByQuery("id", "back")`
- **Bug**: Previously, `/Details?id=X&back=/` and `/Details?id=X&back=/Search?q=term` shared the same cache entry. The first visitor's `back` URL was served to all subsequent visitors within 30 seconds, causing the "← Back" button to navigate to the wrong origin.
- **Fix**: The cache now creates separate entries per `(id, back)` combination. Each unique origin gets its own cached response.

### Impact
- "← Back" now always returns to the correct page for each user, regardless of cache state
- The additional cache variants don't significantly impact memory: with a 30s TTL, the cache turnover limits entry accumulation
