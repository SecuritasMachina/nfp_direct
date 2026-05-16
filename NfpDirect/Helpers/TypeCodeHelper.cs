using System.Globalization;

namespace NfpDirect.Helpers;

public static class TypeCodeHelper
{
    private static readonly Dictionary<string, string> Labels = new(StringComparer.OrdinalIgnoreCase)
    {
        ["3"] = "Public Charity", ["4"] = "Social Welfare", ["5"] = "Labor Union",
        ["6"] = "Agricultural", ["7"] = "Social Club", ["8"] = "Fraternal Beneficiary",
        ["9"] = "Voluntary Employees' Beneficiary", ["10"] = "Domestic Fraternal",
        ["11"] = "Teachers' Retirement", ["12"] = "Benevolent Life Insurance",
        ["13"] = "Cemetery", ["14"] = "Credit Union", ["15"] = "Mutual Insurance",
        ["16"] = "Corp. Financing Crop Ops", ["17"] = "Supplemental Unemployment",
        ["18"] = "Employee-Funded Pension", ["19"] = "War Veterans",
        ["21"] = "Black Lung Trust", ["22"] = "Withdrawal Liability Payment",
        ["23"] = "Veterans Assn. Founded Pre-1880", ["25"] = "Title Holding (Multiple Parents)",
        ["26"] = "State-Chartered Credit Union (Low Income)", ["27"] = "State-Sponsored Workers' Comp",
        ["28"] = "ERISA Sec. 4049 Trust", ["29"] = "Religious/Apostolic",
        ["40"] = "Apostolic/Religious Org.", ["50"] = "Cooperative Hospital",
        ["60"] = "Charitable Risk Pool", ["70"] = "Child Care",
        ["71"] = "Qualified State-Sponsored Tuition", ["81"] = "501(c)(1) Corp",
        ["82"] = "Religious Corp.", ["90"] = "4947(a)(1) Private Foundation",
        ["91"] = "4947(a)(2) Charitable Trust", ["92"] = "527 Political Org",
        ["93"] = "4947(a)(1) Public Charity", ["A"] = "Instrumentality of US",
    };

    public static string Label(string? code) =>
        code != null && Labels.TryGetValue(code, out var label) ? label : code ?? "";

    public static string TitleCase(string? s) =>
        string.IsNullOrEmpty(s) ? "" :
        CultureInfo.CurrentCulture.TextInfo.ToTitleCase(s.ToLower());

    public static string FormatEin(string? ein) =>
        ein is { Length: 9 } && ein.All(char.IsDigit)
            ? $"{ein[..2]}-{ein[2..]}"
            : ein ?? "";

    public static string FormatTaxPeriod(string? tp) =>
        tp is { Length: 6 } &&
        int.TryParse(tp[..4], out var yr) &&
        int.TryParse(tp[4..], out var mo) && mo is >= 1 and <= 12
            ? new DateTime(yr, mo, 1).ToString("MMM yyyy")
            : tp ?? "";

    public static string YesNo(string? v) => v?.Trim().ToUpperInvariant() switch
    {
        "Y" or "1" or "TRUE" => "Yes",
        "N" or "0" or "FALSE" => "No",
        _ => v ?? "—"
    };

    public static string Fmt(string? v) => string.IsNullOrWhiteSpace(v) ? "—" : v.Trim();

    public static readonly IReadOnlyDictionary<string, string> UsStates =
        new Dictionary<string, string>(StringComparer.OrdinalIgnoreCase)
        {
            ["AL"]="Alabama",["AK"]="Alaska",["AZ"]="Arizona",["AR"]="Arkansas",
            ["CA"]="California",["CO"]="Colorado",["CT"]="Connecticut",["DE"]="Delaware",
            ["FL"]="Florida",["GA"]="Georgia",["HI"]="Hawaii",["ID"]="Idaho",
            ["IL"]="Illinois",["IN"]="Indiana",["IA"]="Iowa",["KS"]="Kansas",
            ["KY"]="Kentucky",["LA"]="Louisiana",["ME"]="Maine",["MD"]="Maryland",
            ["MA"]="Massachusetts",["MI"]="Michigan",["MN"]="Minnesota",["MS"]="Mississippi",
            ["MO"]="Missouri",["MT"]="Montana",["NE"]="Nebraska",["NV"]="Nevada",
            ["NH"]="New Hampshire",["NJ"]="New Jersey",["NM"]="New Mexico",["NY"]="New York",
            ["NC"]="North Carolina",["ND"]="North Dakota",["OH"]="Ohio",["OK"]="Oklahoma",
            ["OR"]="Oregon",["PA"]="Pennsylvania",["RI"]="Rhode Island",["SC"]="South Carolina",
            ["SD"]="South Dakota",["TN"]="Tennessee",["TX"]="Texas",["UT"]="Utah",
            ["VT"]="Vermont",["VA"]="Virginia",["WA"]="Washington",["WV"]="West Virginia",
            ["WI"]="Wisconsin",["WY"]="Wyoming",
            ["DC"]="District of Columbia",["PR"]="Puerto Rico",["GU"]="Guam",
            ["VI"]="U.S. Virgin Islands",["AS"]="American Samoa",["MP"]="Northern Mariana Islands",
        };

    public static string FormatMoney(string? v)
    {
        if (string.IsNullOrWhiteSpace(v)) return "—";
        var trimmed = v.Trim();
        return decimal.TryParse(trimmed, NumberStyles.Any, CultureInfo.InvariantCulture, out var amount)
            ? amount.ToString("N0", CultureInfo.InvariantCulture)
            : trimmed;
    }
}
