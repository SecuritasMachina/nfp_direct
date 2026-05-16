using NfpDirect.Helpers;

namespace NfpDirect.Tests;

public class TypeCodeHelperTests
{
    // Label
    [Theory]
    [InlineData("3", "Public Charity")]
    [InlineData("4", "Social Welfare")]
    [InlineData("92", "527 Political Org")]
    [InlineData("A", "Instrumentality of US")]
    public void Label_KnownCode_ReturnsHumanLabel(string code, string expected) =>
        Assert.Equal(expected, TypeCodeHelper.Label(code));

    [Fact]
    public void Label_UnknownCode_ReturnsCodeItself() =>
        Assert.Equal("99", TypeCodeHelper.Label("99"));

    [Fact]
    public void Label_Null_ReturnsEmpty() =>
        Assert.Equal("", TypeCodeHelper.Label(null));

    // FormatEin
    [Fact]
    public void FormatEin_NineDigits_AddsHyphen() =>
        Assert.Equal("12-3456789", TypeCodeHelper.FormatEin("123456789"));

    [Fact]
    public void FormatEin_AlreadyFormatted_ReturnsRaw() =>
        Assert.Equal("12-3456789", TypeCodeHelper.FormatEin("12-3456789"));

    [Fact]
    public void FormatEin_Null_ReturnsEmpty() =>
        Assert.Equal("", TypeCodeHelper.FormatEin(null));

    // FormatTaxPeriod
    [Fact]
    public void FormatTaxPeriod_ValidYYYYMM_ReturnsMonthYear() =>
        Assert.Equal("Dec 2019", TypeCodeHelper.FormatTaxPeriod("201912"));

    [Fact]
    public void FormatTaxPeriod_Invalid_ReturnsRaw() =>
        Assert.Equal("BADVAL", TypeCodeHelper.FormatTaxPeriod("BADVAL"));

    [Fact]
    public void FormatTaxPeriod_Null_ReturnsEmpty() =>
        Assert.Equal("", TypeCodeHelper.FormatTaxPeriod(null));

    // YesNo
    [Theory]
    [InlineData("Y", "Yes")]
    [InlineData("y", "Yes")]
    [InlineData("1", "Yes")]
    [InlineData("TRUE", "Yes")]
    [InlineData("N", "No")]
    [InlineData("0", "No")]
    [InlineData("FALSE", "No")]
    public void YesNo_KnownValues_ReturnsYesOrNo(string input, string expected) =>
        Assert.Equal(expected, TypeCodeHelper.YesNo(input));

    [Fact]
    public void YesNo_Null_ReturnsDash() =>
        Assert.Equal("—", TypeCodeHelper.YesNo(null));

    // FormatMoney
    [Fact]
    public void FormatMoney_Integer_AddsCommas() =>
        Assert.Equal("1,234,567", TypeCodeHelper.FormatMoney("1234567"));

    [Fact]
    public void FormatMoney_NegativeValue_FormatsCorrectly() =>
        Assert.Equal("-5,000", TypeCodeHelper.FormatMoney("-5000"));

    [Fact]
    public void FormatMoney_Zero_ReturnsZero() =>
        Assert.Equal("0", TypeCodeHelper.FormatMoney("0"));

    [Fact]
    public void FormatMoney_Blank_ReturnsDash() =>
        Assert.Equal("—", TypeCodeHelper.FormatMoney(""));

    [Fact]
    public void FormatMoney_Null_ReturnsDash() =>
        Assert.Equal("—", TypeCodeHelper.FormatMoney(null));

    [Fact]
    public void FormatMoney_NonNumeric_ReturnsRaw() =>
        Assert.Equal("n/a", TypeCodeHelper.FormatMoney("n/a"));

    // TitleCase
    [Fact]
    public void TitleCase_AllCaps_ReturnsTitleCase() =>
        Assert.Equal("Habitat For Humanity", TypeCodeHelper.TitleCase("HABITAT FOR HUMANITY"));

    [Fact]
    public void TitleCase_Null_ReturnsEmpty() =>
        Assert.Equal("", TypeCodeHelper.TitleCase(null));

    // UsStates
    [Fact]
    public void UsStates_ContainsAllFiftyStatesAndDC()
    {
        Assert.True(TypeCodeHelper.UsStates.Count >= 51);
        Assert.Equal("Illinois", TypeCodeHelper.UsStates["IL"]);
        Assert.Equal("District of Columbia", TypeCodeHelper.UsStates["DC"]);
        Assert.Equal("Puerto Rico", TypeCodeHelper.UsStates["PR"]);
    }

    [Fact]
    public void UsStates_IsCaseInsensitive() =>
        Assert.True(TypeCodeHelper.UsStates.ContainsKey("il"));
}
