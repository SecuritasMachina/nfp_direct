using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace NfpDirect.Models;

[Table("pub78")]
public class Pub78
{
    [Key]
    [Column("EIN")]
    public string EIN { get; set; } = "";

    [Column("CompanyName")]
    public string? CompanyName { get; set; }

    [Column("City")]
    public string? City { get; set; }

    [Column("State")]
    public string? State { get; set; }

    [Column("Country")]
    public string? Country { get; set; }

    [Column("TypeCode")]
    public string? TypeCode { get; set; }
}
