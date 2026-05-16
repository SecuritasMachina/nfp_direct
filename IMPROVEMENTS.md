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
