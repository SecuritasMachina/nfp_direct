using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace NfpDirect.Models;

[Table("nfp_epostcard")]
public class Postcard
{
    [Key]
    [Column("id")]
    public string Id { get; set; } = "";

    [Column("year")]
    public int? Year { get; set; }

    [Column("company_name")]
    public string? CompanyName { get; set; }

    [Column("GrossReceiptsUnder25k")]
    public string? GrossReceiptsUnder25k { get; set; }

    [Column("Terminated")]
    public string? Terminated { get; set; }

    [Column("TaxPeriodBeginDate")]
    public string? TaxPeriodBeginDate { get; set; }

    [Column("TaxPeriodEndDate")]
    public string? TaxPeriodEndDate { get; set; }

    [Column("WebsiteURL")]
    public string? WebsiteURL { get; set; }

    [Column("OfficerName")]
    public string? OfficerName { get; set; }

    [Column("OfficerAddressLine1")]
    public string? OfficerAddressLine1 { get; set; }

    [Column("OfficerAddressLine2")]
    public string? OfficerAddressLine2 { get; set; }

    [Column("OfficerAddressCity")]
    public string? OfficerAddressCity { get; set; }

    [Column("Province")]
    public string? Province { get; set; }

    [Column("State")]
    public string? State { get; set; }

    [Column("PostalCode")]
    public string? PostalCode { get; set; }

    [Column("Country")]
    public string? Country { get; set; }

    [Column("OrgAddressLine1")]
    public string? OrgAddressLine1 { get; set; }

    [Column("OrgAddressLine2")]
    public string? OrgAddressLine2 { get; set; }

    [Column("OrgCity")]
    public string? OrgCity { get; set; }

    [Column("OrgProvince")]
    public string? OrgProvince { get; set; }

    [Column("OrgState")]
    public string? OrgState { get; set; }

    [Column("OrgPostalCode")]
    public string? OrgPostalCode { get; set; }

    [Column("OrgCountry")]
    public string? OrgCountry { get; set; }

    [Column("DBA1")]
    public string? DBA1 { get; set; }

    [Column("DBA2")]
    public string? DBA2 { get; set; }

    [Column("DBA3")]
    public string? DBA3 { get; set; }
}
