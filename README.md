# NFP Direct

NFP Direct is a fast, public web application for **browsing and searching U.S. IRS non-profit
organization data** — tax-exempt charities, e-Postcard (Form 990-N) filers, and full Form 990
filings — sourced from IRS Publication 78 and related public datasets.

The current, actively-maintained application is the **ASP.NET Core 8 Razor Pages** edition
(`NfpDirect/`). It replaces an earlier Spring Boot / Angular implementation, which is retained in
this repository under `nfp_direct/` for historical reference only.

---

## Overview

Non-profit and charitable-organization data published by the IRS is large, flat, and awkward to
browse. NFP Direct loads that data into a relational database and exposes a clean, fast,
crawler-friendly web UI to:

- Browse tax-exempt organizations (IRS Publication 78), paginated and filterable by state.
- Browse Form 990-N e-Postcard filings, paginated and filterable by state.
- Search organizations by name or EIN, using a database FULLTEXT index for relevance.
- View a consolidated detail page for any organization (EIN) that merges its Publication 78
  record, its e-Postcard filing, and its full Form 990 financial filing into one view.

The app is read-only and unauthenticated — it surfaces public IRS data, so there is no login,
no user accounts, and no write paths.

---

## Key features

- **State-filtered browse** of Publication 78 organizations and 990-N postcard filings, paginated.
- **Name / EIN search** backed by a MySQL/MariaDB FULLTEXT index (used for queries of 4+ characters);
  a query that resolves to a single EIN auto-redirects to that organization's detail page.
- **Consolidated organization detail view** joining Pub78 + e-Postcard + Form 990 (200+ financial
  and compliance fields), with monetary figures formatted for display.
- **Human-readable exempt-type labels** — IRS numeric/letter TypeCodes are mapped to friendly
  descriptions (e.g. "Public Charity", "Credit Union", "527 Political Org") via `TypeCodeHelper`.
- **SEO / crawler support** — generated `/sitemap.xml` (home, search, and per-state browse URLs),
  `robots.txt`, OpenSearch description, custom 404 page, and a favicon.
- **Performance hardening** — EF Core `DbContextPool`, output caching per page type, Brotli + Gzip
  response compression, long-lived static-asset cache headers, and DB performance/FULLTEXT indexes.
- **Abuse protection** — per-IP sliding-window rate limiting on search (30 requests/minute),
  returning HTTP 429 with a `Retry-After` header.
- **Security headers** — strict Content-Security-Policy with per-request nonce, HSTS (with preload),
  `X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy`, `Permissions-Policy`, and
  Cross-Origin isolation headers.
- **Operational endpoints** — `/health` JSON check that verifies database connectivity, plus a
  container `HEALTHCHECK`.

---

## Tech stack

| Layer | Technology |
|-------|-----------|
| Web framework | ASP.NET Core 8 Razor Pages (.NET 8) |
| ORM | Entity Framework Core 8 + Pomelo MySQL provider |
| Database | MariaDB / MySQL (tables: `pub78`, `nfp_epostcard`, `filing`) |
| Caching | ASP.NET Core Output Cache + response compression (Brotli/Gzip) |
| Rate limiting | `System.Threading.RateLimiting` sliding-window limiter |
| Container | Docker (multi-stage SDK build → ASP.NET runtime image, port 8080) |
| Tests | xUnit (`NfpDirect.Tests`) |

> Legacy edition (`nfp_direct/`, reference only): Java / Gradle backend with an Angular 7 client.

---

## Repository layout

```
.
├── NfpDirect/              # Active ASP.NET Core 8 Razor Pages application
│   ├── Pages/             # Razor Pages: Index, Search, Details, ByPostcard, Error, NotFound, ...
│   ├── Models/            # EF Core entities: Pub78, Postcard, Filing
│   ├── Data/              # NfpDbContext
│   ├── Helpers/           # TypeCodeHelper (exempt-type & US-state lookups)
│   ├── wwwroot/           # Static assets (css, favicon, robots.txt, opensearch.xml)
│   ├── Program.cs         # App startup: DI, middleware, caching, rate limiting, security, routes
│   ├── schema.sql         # Table schema + performance / FULLTEXT indexes
│   ├── Dockerfile         # Multi-stage container build
│   └── README.md          # App-specific docs
├── NfpDirect.Tests/        # xUnit test project
├── nfp_direct/             # Legacy Java + Angular implementation (historical reference)
└── IMPROVEMENTS.md         # Working notes / improvement log
```

---

## Data model

| Table | Entity | Description |
|-------|--------|-------------|
| `pub78` | `Pub78` | IRS Publication 78 — tax-exempt orgs and deductibility / type codes |
| `nfp_epostcard` | `Postcard` | IRS Form 990-N e-Postcard filings |
| `filing` | `Filing` | IRS Form 990 filings (200+ financial & compliance fields) |

Organizations are keyed by **EIN** (Employer Identification Number), which is how the three tables
are joined on the detail page.

---

## Pages & endpoints

| URL | Description |
|-----|-------------|
| `/` | Paginated browse of Pub78 organizations; optional `?state=XX` filter |
| `/ByPostcard` | Paginated browse of 990-N e-Postcard filings; optional `?state=XX` filter |
| `/Details?id=<EIN>` | Consolidated detail view (Pub78 + Postcard + Filing) |
| `/Search?q=<term>` | Name/EIN search with optional `?state=XX`; FULLTEXT for queries ≥ 4 chars; single-EIN match redirects to Details |
| `/health` | JSON health check; verifies DB connectivity (`SELECT 1`) |
| `/sitemap.xml` | Sitemap for crawlers (home, search, per-state browse) |
| `/404` | Custom not-found page |

---

## Build & run

### Prerequisites

- .NET 8 SDK
- A reachable MariaDB / MySQL instance with the `nfp` database loaded (see below)

### Run locally (.NET CLI)

```bash
# Provide the connection string (env var takes precedence over appsettings)
export NFP_CONNECTION_STRING="Server=<host>;Port=3306;Database=nfp;User=<user>;Password=<password>;"

cd NfpDirect
dotnet run
```

The connection string is resolved from the `NFP_CONNECTION_STRING` environment variable first,
then from the `NfpDb` connection string in `appsettings.json`. Startup fails fast if neither is set.

### Run with Docker

The provided `Dockerfile` produces a multi-stage build and serves on port **8080**:

```bash
cd NfpDirect
docker build -t nfp-direct .
docker run --rm -p 8080:8080 \
  -e NFP_CONNECTION_STRING="Server=<host>;Port=3306;Database=nfp;User=<user>;Password=<password>;" \
  nfp-direct
```

A `.env.example` is included for compose-based deployments; copy it to `.env` and supply real
values before use.

### Populate the database

Create the schema from scratch:

```bash
mysql -h <host> -u <user> -p nfp < NfpDirect/schema.sql
```

Then import the IRS dataset dump into the `nfp` database.

### Run tests

```bash
cd NfpDirect.Tests
dotnet test
```

The suite covers the page models (`SearchModel`, `IndexModel`, `ByPostcardModel`, `DetailsModel`),
EIN matching, FULLTEXT query building, and `TypeCodeHelper`.

---

## Configuration

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `NFP_CONNECTION_STRING` | Yes (or `appsettings` value) | — | Full MariaDB/MySQL connection string |
| `ASPNETCORE_ENVIRONMENT` | No | `Production` (in container) | Standard ASP.NET Core environment |
| `ASPNETCORE_HTTP_PORTS` | No | `8080` (in container) | HTTP listen port |

Configuration notes:

- Do **not** commit real credentials. `appsettings.json` ships with a placeholder connection string;
  override it via `NFP_CONNECTION_STRING` in every real environment.
- Output caching, rate limiting, compression, and security headers are configured in `Program.cs`.
- Database performance and FULLTEXT indexes live in `schema.sql` and must be applied for search and
  state-filtered browse to perform well.

---

## License

Internal project. See repository owner for licensing/usage terms.
