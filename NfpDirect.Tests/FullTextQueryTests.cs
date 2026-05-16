using NfpDirect.Pages;

namespace NfpDirect.Tests;

public class FullTextQueryTests
{
    [Fact]
    public void BuildFullTextQuery_SingleWord_ReturnsPrefixWildcard() =>
        Assert.Equal("+habitat*", SearchModel.BuildFullTextQuery("habitat"));

    [Fact]
    public void BuildFullTextQuery_MultiWord_ReturnsAllWordsPrefixed() =>
        Assert.Equal("+habitat* +humanity*", SearchModel.BuildFullTextQuery("habitat humanity"));

    [Fact]
    public void BuildFullTextQuery_SpecialCharsStripped_NoSpaceInserted() =>
        Assert.Equal("+abcdef*", SearchModel.BuildFullTextQuery("abc!!!def"));

    [Fact]
    public void BuildFullTextQuery_FulltextOperatorStripped_PlusSignRemoved() =>
        Assert.Equal("+search*", SearchModel.BuildFullTextQuery("+search"));

    [Fact]
    public void BuildFullTextQuery_FulltextNegateStripped_MinusSignRemoved() =>
        Assert.Equal("+word*", SearchModel.BuildFullTextQuery("-word"));

    [Fact]
    public void BuildFullTextQuery_WildcardStripped_AsteriskRemoved() =>
        Assert.Equal("+wild*", SearchModel.BuildFullTextQuery("wild*"));

    [Fact]
    public void BuildFullTextQuery_QuotesStripped_QuotesRemoved() =>
        Assert.Equal("+exact* +phrase*", SearchModel.BuildFullTextQuery("\"exact phrase\""));

    [Fact]
    public void BuildFullTextQuery_AllSpecialChars_ReturnsEmpty() =>
        Assert.Equal("", SearchModel.BuildFullTextQuery("!@#$%^&*()"));

    [Fact]
    public void BuildFullTextQuery_ExtraWhitespace_NormalizedToSingleTerms() =>
        Assert.Equal("+abc* +def*", SearchModel.BuildFullTextQuery("  abc   def  "));

    [Fact]
    public void BuildFullTextQuery_Numeric_PreservesDigits() =>
        Assert.Equal("+12345*", SearchModel.BuildFullTextQuery("12345"));

    [Fact]
    public void BuildFullTextQuery_MixedCasePreserved() =>
        Assert.Equal("+Habitat* +Humanity*", SearchModel.BuildFullTextQuery("Habitat Humanity"));
}
