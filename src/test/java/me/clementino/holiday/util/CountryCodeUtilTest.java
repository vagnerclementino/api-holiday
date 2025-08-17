package me.clementino.holiday.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("CountryCodeUtil Tests")
class CountryCodeUtilTest {

  @Test
  @DisplayName("Should validate valid ISO country codes")
  void shouldValidateValidISOCountryCodes() {
    // Valid codes
    assertThat(CountryCodeUtil.isValidCountryCode("BR")).isTrue();
    assertThat(CountryCodeUtil.isValidCountryCode("US")).isTrue();
    assertThat(CountryCodeUtil.isValidCountryCode("GB")).isTrue();
    assertThat(CountryCodeUtil.isValidCountryCode("DE")).isTrue();
    assertThat(CountryCodeUtil.isValidCountryCode("JP")).isTrue();

    // Case insensitive
    assertThat(CountryCodeUtil.isValidCountryCode("br")).isTrue();
    assertThat(CountryCodeUtil.isValidCountryCode("us")).isTrue();
  }

  @Test
  @DisplayName("Should reject invalid country codes")
  void shouldRejectInvalidCountryCodes() {
    // Invalid codes
    assertThat(CountryCodeUtil.isValidCountryCode("XX")).isFalse();
    assertThat(CountryCodeUtil.isValidCountryCode("ZZ")).isFalse();
    assertThat(CountryCodeUtil.isValidCountryCode("123")).isFalse();
    assertThat(CountryCodeUtil.isValidCountryCode("ABC")).isFalse();

    // Null and empty
    assertThat(CountryCodeUtil.isValidCountryCode(null)).isFalse();
    assertThat(CountryCodeUtil.isValidCountryCode("")).isFalse();
    assertThat(CountryCodeUtil.isValidCountryCode("  ")).isFalse();
  }

  @Test
  @DisplayName("Should get country names from codes")
  void shouldGetCountryNamesFromCodes() {
    assertThat(CountryCodeUtil.getCountryName("BR")).isEqualTo(Optional.of("Brazil"));
    assertThat(CountryCodeUtil.getCountryName("US")).isEqualTo(Optional.of("United States"));
    assertThat(CountryCodeUtil.getCountryName("GB")).isEqualTo(Optional.of("United Kingdom"));
    assertThat(CountryCodeUtil.getCountryName("DE")).isEqualTo(Optional.of("Germany"));

    // Case insensitive
    assertThat(CountryCodeUtil.getCountryName("br")).isEqualTo(Optional.of("Brazil"));

    // Invalid codes
    assertThat(CountryCodeUtil.getCountryName("XX")).isEqualTo(Optional.empty());
    assertThat(CountryCodeUtil.getCountryName(null)).isEqualTo(Optional.empty());
  }

  @Test
  @DisplayName("Should get country codes from names")
  void shouldGetCountryCodesFromNames() {
    assertThat(CountryCodeUtil.getCountryCode("Brazil")).isEqualTo(Optional.of("BR"));
    assertThat(CountryCodeUtil.getCountryCode("United States")).isEqualTo(Optional.of("US"));
    assertThat(CountryCodeUtil.getCountryCode("Germany")).isEqualTo(Optional.of("DE"));

    // Case insensitive
    assertThat(CountryCodeUtil.getCountryCode("brazil")).isEqualTo(Optional.of("BR"));
    assertThat(CountryCodeUtil.getCountryCode("BRAZIL")).isEqualTo(Optional.of("BR"));

    // With whitespace
    assertThat(CountryCodeUtil.getCountryCode("  Brazil  ")).isEqualTo(Optional.of("BR"));

    // Invalid names
    assertThat(CountryCodeUtil.getCountryCode("Invalid Country")).isEqualTo(Optional.empty());
    assertThat(CountryCodeUtil.getCountryCode(null)).isEqualTo(Optional.empty());
    assertThat(CountryCodeUtil.getCountryCode("")).isEqualTo(Optional.empty());
  }

  @Test
  @DisplayName("Should normalize country input")
  void shouldNormalizeCountryInput() {
    // Already valid codes
    assertThat(CountryCodeUtil.normalizeCountry("BR")).isEqualTo(Optional.of("BR"));
    assertThat(CountryCodeUtil.normalizeCountry("br")).isEqualTo(Optional.of("BR"));
    assertThat(CountryCodeUtil.normalizeCountry("US")).isEqualTo(Optional.of("US"));

    // Country names
    assertThat(CountryCodeUtil.normalizeCountry("Brazil")).isEqualTo(Optional.of("BR"));
    assertThat(CountryCodeUtil.normalizeCountry("United States")).isEqualTo(Optional.of("US"));
    assertThat(CountryCodeUtil.normalizeCountry("Germany")).isEqualTo(Optional.of("DE"));

    // With whitespace
    assertThat(CountryCodeUtil.normalizeCountry("  BR  ")).isEqualTo(Optional.of("BR"));
    assertThat(CountryCodeUtil.normalizeCountry("  Brazil  ")).isEqualTo(Optional.of("BR"));

    // Invalid input
    assertThat(CountryCodeUtil.normalizeCountry("Invalid")).isEqualTo(Optional.empty());
    assertThat(CountryCodeUtil.normalizeCountry(null)).isEqualTo(Optional.empty());
    assertThat(CountryCodeUtil.normalizeCountry("")).isEqualTo(Optional.empty());
  }

  @Test
  @DisplayName("Should get pretty names")
  void shouldGetPrettyNames() {
    assertThat(CountryCodeUtil.getPrettyName("BR")).isEqualTo("Brazil");
    assertThat(CountryCodeUtil.getPrettyName("US")).isEqualTo("United States");
    assertThat(CountryCodeUtil.getPrettyName("DE")).isEqualTo("Germany");

    // Invalid codes should return the original input
    assertThat(CountryCodeUtil.getPrettyName("XX")).isEqualTo("XX");
    assertThat(CountryCodeUtil.getPrettyName("Invalid")).isEqualTo("Invalid");
  }

  @Test
  @DisplayName("Should check if countries are the same")
  void shouldCheckIfCountriesAreTheSame() {
    // Same codes
    assertThat(CountryCodeUtil.isSameCountry("BR", "BR")).isTrue();
    assertThat(CountryCodeUtil.isSameCountry("br", "BR")).isTrue();
    assertThat(CountryCodeUtil.isSameCountry("BR", "br")).isTrue();

    // Code vs name
    assertThat(CountryCodeUtil.isSameCountry("BR", "Brazil")).isTrue();
    assertThat(CountryCodeUtil.isSameCountry("Brazil", "BR")).isTrue();

    // Different countries
    assertThat(CountryCodeUtil.isSameCountry("BR", "US")).isFalse();
    assertThat(CountryCodeUtil.isSameCountry("Brazil", "United States")).isFalse();

    // Null values
    assertThat(CountryCodeUtil.isSameCountry(null, "BR")).isFalse();
    assertThat(CountryCodeUtil.isSameCountry("BR", null)).isFalse();
    assertThat(CountryCodeUtil.isSameCountry(null, null)).isFalse();
  }
}
