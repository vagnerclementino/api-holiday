package me.clementino.holiday.dto;

import java.util.Optional;

/**
 * Response DTO for geographical locality data using Java 24 record.
 *
 * <p>This record demonstrates DOP principles:
 *
 * <ul>
 *   <li>Model Data Immutably and Transparently - immutable record structure
 *   <li>Model the Data, the Whole Data, and Nothing but the Data - contains exactly what's needed
 *       for locality responses
 * </ul>
 */
public record LocalityResponse(
    String countryCode,
    String countryName,
    Optional<String> subdivisionCode,
    Optional<String> subdivisionName,
    Optional<String> cityName,
    LocalityType type) {

  /** Enum representing the type of locality. */
  public enum LocalityType {
    COUNTRY,
    SUBDIVISION,
    CITY
  }

  /** Creates a country-level locality response. */
  public static LocalityResponse country(String countryCode, String countryName) {
    return new LocalityResponse(
        countryCode,
        countryName,
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        LocalityType.COUNTRY);
  }

  /** Creates a subdivision-level locality response. */
  public static LocalityResponse subdivision(
      String countryCode, String countryName, String subdivisionCode, String subdivisionName) {
    return new LocalityResponse(
        countryCode,
        countryName,
        Optional.of(subdivisionCode),
        Optional.of(subdivisionName),
        Optional.empty(),
        LocalityType.SUBDIVISION);
  }

  /** Creates a city-level locality response. */
  public static LocalityResponse city(
      String countryCode,
      String countryName,
      String subdivisionCode,
      String subdivisionName,
      String cityName) {
    return new LocalityResponse(
        countryCode,
        countryName,
        Optional.of(subdivisionCode),
        Optional.of(subdivisionName),
        Optional.of(cityName),
        LocalityType.CITY);
  }

  /** Gets a formatted display name for this locality. */
  public String displayName() {
    return switch (type) {
      case COUNTRY -> countryName;
      case SUBDIVISION -> subdivisionName.orElse("Unknown") + ", " + countryName;
      case CITY ->
          cityName.orElse("Unknown")
              + ", "
              + subdivisionName.orElse("Unknown")
              + ", "
              + countryName;
    };
  }

  /** Gets a short display name for this locality. */
  public String shortDisplayName() {
    return switch (type) {
      case COUNTRY -> countryCode;
      case SUBDIVISION -> subdivisionCode.orElse("??") + ", " + countryCode;
      case CITY -> cityName.orElse("Unknown") + " (" + countryCode + ")";
    };
  }

  /** Checks if this locality is at the country level. */
  public boolean isCountryLevel() {
    return type == LocalityType.COUNTRY;
  }

  /** Checks if this locality is at the subdivision level. */
  public boolean isSubdivisionLevel() {
    return type == LocalityType.SUBDIVISION;
  }

  /** Checks if this locality is at the city level. */
  public boolean isCityLevel() {
    return type == LocalityType.CITY;
  }

  /** Gets the hierarchical level (1=country, 2=subdivision, 3=city). */
  public int getHierarchicalLevel() {
    return switch (type) {
      case COUNTRY -> 1;
      case SUBDIVISION -> 2;
      case CITY -> 3;
    };
  }
}
