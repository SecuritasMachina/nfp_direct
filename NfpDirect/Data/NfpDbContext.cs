using Microsoft.EntityFrameworkCore;
using NfpDirect.Models;

namespace NfpDirect.Data;

public class NfpDbContext : DbContext
{
    public NfpDbContext(DbContextOptions<NfpDbContext> options) : base(options) { }

    public DbSet<Pub78> Pub78 { get; set; }
    public DbSet<Postcard> Postcards { get; set; }
    public DbSet<Filing> Filings { get; set; }
}
