package me.clementino.holiday.util;

import module java.base;
import com.neovisionaries.i18n.CountryCode;

/**
 * Utility class for working with ISO 3166-1 alpha-2 country codes.
 *
 * <p>
 * This utility follows DOP principles by providing pure functions for country
 * code validation
 * and conversion operations.
 */
public final class CountryCodeUtil {

  private CountryCodeUtil() {
  }

  /**
   * Validate if a string is a valid ISO 3166-1 alpha-2 country code.
   *
   * @param countryCode the country code to validate
   * @return true if valid, false otherwise
   */
  public static boolean isValidCountryCode(String countryCode) {
    if (countryCode == null || countryCode.isBlank()) {
      return false;
    }

    try {
      CountryCode code = CountryCode.getByCode(countryCode.toUpperCase());
      return code != null && code != CountryCode.UNDEFINED;
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Get the country name from an ISO 3166-1 alpha-2 country code.
   *
   * @param countryCode the ISO country code
   * @return the country name, or empty if invalid
   */
  public static Optional<String> getCountryName(String countryCode) {
    if (!isValidCountryCode(countryCode)) {
      return Optional.empty();
    }

    try {
      CountryCode code = CountryCode.getByCode(countryCode.toUpperCase());
      return Optional.ofNullable(code.getName());
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Convert a country name to ISO 3166-1 alpha-2 country code.
   *
   * @param countryName the country name
   * @return the ISO country code, or empty if not found
   */
  public static Optional<String> getCountryCode(String countryName) {
    if (countryName == null || countryName.isBlank()) {
      return Optional.empty();
    }

    try {
      for (CountryCode code : CountryCode.values()) {
        if (code != CountryCode.UNDEFINED
            && code.getName() != null
            && code.getName().equalsIgnoreCase(countryName.trim())) {
          return Optional.of(code.getAlpha2());
        }
      }
      return Optional.empty();
    } catch (Exception e) {
      return Optional.empty();
    }
  }

  /**
   * Normalize a country input to ISO 3166-1 alpha-2 format.
   *
   * <p>
   * If the input is already a valid country code, returns it normalized. If the
   * input is a
   * country name, tries to convert it to a country code.
   *
   * @param countryInput the country code or name
   * @return normalized ISO country code, or empty if invalid
   */
  public static Optional<String> normalizeCountry(String countryInput) {
    if (countryInput == null || countryInput.isBlank()) {
      return Optional.empty();
    }

    String trimmed = countryInput.trim();

    if (isValidCountryCode(trimmed)) {
      return Optional.of(trimmed.toUpperCase());
    }

    return getCountryCode(trimmed);
  }

  /**
   * Get a pretty display name for a country code.
   *
   * <p>
   * Returns the country name if the code is valid, otherwise returns the original
   * input.
   *
   * @param countryCode the ISO country code
   * @return pretty display name
   */
  public static String getPrettyName(String countryCode) {
    return getCountryName(countryCode).orElse(countryCode);
  }

  /**
   * Check if a country code represents a specific country.
   *
   * @param countryCode  the country code to check
   * @param expectedCode the expected country code
   * @return true if they represent the same country
   */
  public static boolean isSameCountry(String countryCode, String expectedCode) {
    if (countryCode == null || expectedCode == null) {
      return false;
    }

    Optional<String> normalized1 = normalizeCountry(countryCode);
    Optional<String> normalized2 = normalizeCountry(expectedCode);

    if (normalized1.isEmpty() || normalized2.isEmpty()) {
      return false;
    }

    return normalized1.get().equalsIgnoreCase(normalized2.get());
  }
}
