# NFP Direct — C# Razor Edition

ASP.NET Core 8 Razor Pages application for browsing IRS non-profit organization data.  
Replaces the legacy Spring Boot 2.1 / JSP application.

## Stack

| Component | Technology |
|-----------|-----------|
| Web framework | ASP.NET Core 8 Razor Pages |
| ORM | Entity Framework Core 8 + Pomelo (MariaDB) |
| Database | MariaDB 11.4 (shared `mariadb` container on `ai_internal` network) |
| Container | Docker (multi-stage build) |

## Database tables

| Table | Model | Description |
|-------|-------|-------------|
| `pub78` | `Pub78` | IRS Publication 78 — deductibility codes |
| `nfp_epostcard` | `Postcard` | IRS e-Postcard (Form 990-N) filings |
| `filing` | `Filing` | IRS Form 990 filings (230+ fields) |

## Running locally (Docker Compose)

```bash
cd /path/to/src/docker

# Build and start (uses shared mariadb container on ai_internal network)
docker compose -f docker-compose.beta.yml up --build -d

# Check logs
docker logs -f nfp-direct-beta
```

The app will be available at `http://localhost:${NFP_BETA_PORT:-8096}`.

## Populating the database

The `nfp` database is served by the shared `mariadb` container. Import the IRS data:

```bash
docker exec -i mariadb mariadb -u nfp -p<password> nfp < nfp_dump.sql
```

To create the schema from scratch (empty DB):

```bash
docker exec -i mariadb mariadb -u nfp -p<password> nfp < NfpDirect/schema.sql
```

## Running tests

```bash
cd NfpDirect.Tests
dotnet test
```

53 tests covering `SearchModel`, `IndexModel`, `ByPostcardModel`, and `TypeCodeHelper`.

## Environment variables

| Variable | Required | Default | Description |
|----------|----------|---------|-------------|
| `NFP_CONNECTION_STRING` | Yes | — | Full MariaDB connection string |
| `NFP_BETA_PORT` | No | `8096` | Host port for the beta service |

## Pages & endpoints

| URL | Description |
|-----|-------------|
| `/` | Paginated browse of Pub78 orgs; optional `?state=XX` filter |
| `/ByPostcard` | Paginated browse of 990-N postcard filings; optional `?state=XX` filter |
| `/Details?id=<EIN>` | Detail view: Pub78 + Postcard + Filing (financial figures formatted) |
| `/Search?q=<term>` | Name/EIN search with optional `?state=XX` filter; FULLTEXT index used for queries ≥ 4 chars; single EIN match auto-redirects to Details |
| `/health` | JSON health check (`{"status":"ok"}`); verifies DB connectivity via `SELECT 1` |
| `/sitemap.xml` | Sitemap for crawlers (home + search pages only) |
| `/404` | Custom 404 page |

## Performance indexes (applied in schema.sql)

```sql
-- Browse/filter indexes
idx_pub78_company       (CompanyName(191))
idx_pub78_state         (State)
idx_pub78_state_company (State, CompanyName(191))
idx_epostcard_company   (company_name(191))

-- Search index
ft_pub78_company        FULLTEXT(CompanyName)
```
