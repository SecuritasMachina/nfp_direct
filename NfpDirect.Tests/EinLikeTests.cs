using NfpDirect.Pages;

namespace NfpDirect.Tests;

public class EinLikeTests
{
    [Fact]
    public void IsEinLike_NineDigits_ReturnsTrue() =>
        Assert.True(SearchModel.IsEinLike("123456789"));

    [Fact]
    public void IsEinLike_HyphenatedEin_ReturnsTrue() =>
        Assert.True(SearchModel.IsEinLike("12-3456789"));

    [Fact]
    public void IsEinLike_EightDigits_ReturnsFalse() =>
        Assert.False(SearchModel.IsEinLike("12345678"));

    [Fact]
    public void IsEinLike_TenDigits_ReturnsFalse() =>
        Assert.False(SearchModel.IsEinLike("1234567890"));

    [Fact]
    public void IsEinLike_ContainsLetters_ReturnsFalse() =>
        Assert.False(SearchModel.IsEinLike("12345678A"));

    [Fact]
    public void IsEinLike_Empty_ReturnsFalse() =>
        Assert.False(SearchModel.IsEinLike(""));

    [Fact]
    public void IsEinLike_PlainText_ReturnsFalse() =>
        Assert.False(SearchModel.IsEinLike("habitat humanity"));

    [Fact]
    public void IsEinLike_MultipleHyphens_NineDigitsTotal_ReturnsTrue() =>
        Assert.True(SearchModel.IsEinLike("1-2-3456789"));

    [Fact]
    public void IsEinLike_AllHyphens_ReturnsFalse() =>
        Assert.False(SearchModel.IsEinLike("---------"));

    [Fact]
    public void IsEinLike_EightDigitsWithHyphen_ReturnsFalse() =>
        Assert.False(SearchModel.IsEinLike("12-345678"));
}
