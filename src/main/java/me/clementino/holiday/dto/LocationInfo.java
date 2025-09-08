package me.clementino.holiday.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import me.clementino.holiday.domain.Location;
import me.clementino.holiday.util.CountryCodeUtil;

/**
 * Information about where a holiday is observed.
 *
 * <p>This record represents a geographical location where a holiday applies, following DOP
 * principles with immutable, transparent data structure.
 *
 * <p>Uses ISO 3166-1 alpha-2 country codes for standardization.
 */
@Schema(description = "Geographical location where a holiday is observed")
public record LocationInfo(
    @Schema(description = "ISO 3166-1 alpha-2 country code", example = "BR", required = true)
        String country,
    @Schema(description = "State, province or subdivision name", example = "São Paulo")
        String subdivision,
    @Schema(description = "City name", example = "São Paulo") String city,
    @Schema(
            description = "Pretty formatted location string",
            example = "São Paulo, São Paulo, Brazil")
        String pretty) {

  /**
   * Create LocationInfo from a Location domain object.
   *
   * @param location the domain location
   * @return LocationInfo DTO
   */
  public static LocationInfo from(Location location) {
    String countryCode =
        CountryCodeUtil.normalizeCountry(location.country()).orElse(location.country());

    String prettyName =
        generatePrettyName(
            countryCode, location.state().orElse(null), location.city().orElse(null));

    return new LocationInfo(
        countryCode, location.state().orElse(null), location.city().orElse(null), prettyName);
  }

  /**
   * Generate a pretty formatted name for the location.
   *
   * @param countryCode the ISO country code
   * @param subdivision the subdivision name (optional)
   * @param city the city name (optional)
   * @return formatted location string with city, subdivision, country names
   */
  private static String generatePrettyName(String countryCode, String subdivision, String city) {
    StringBuilder sb = new StringBuilder();

    if (city != null && !city.isBlank()) {
      sb.append(city).append(", ");
    }

    if (subdivision != null && !subdivision.isBlank()) {
      sb.append(subdivision).append(", ");
    }

    String countryName = CountryCodeUtil.getPrettyName(countryCode);
    sb.append(countryName);

    return sb.toString();
  }
}
