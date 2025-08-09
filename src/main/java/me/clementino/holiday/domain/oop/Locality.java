package me.clementino.holiday.domain.oop;

/**
 * Represents a geographical locality where a holiday is observed.
 *
 * <p>This class models the hierarchical nature of geographical locations, supporting different
 * levels of administrative divisions from country level down to city level. It follows
 * Data-Oriented Programming principles by representing location data as an immutable structure.
 *
 * <p><strong>Hierarchy Levels:</strong>
 *
 * <ul>
 *   <li><strong>Country:</strong> National level (e.g., "Brazil", "United States")
 *   <li><strong>Subdivision:</strong> State/Province level (e.g., "São Paulo", "California")
 *   <li><strong>City:</strong> Municipal level (e.g., "São Paulo", "San Francisco")
 * </ul>
 *
 * <p><strong>Usage Examples:</strong>
 *
 * <pre>{@code
 * // National holiday
 * Locality brazil = new Locality("Brazil", null, null);
 *
 * // State-level holiday
 * Locality saoPauloState = new Locality("Brazil", "São Paulo", null);
 *
 * // City-level holiday
 * Locality saoPauloCity = new Locality("Brazil", "São Paulo", "São Paulo");
 *
 * // Using convenience constructors
 * Locality usa = Locality.country("United States");
 * Locality california = Locality.subdivision("United States", "California");
 * Locality sanFrancisco = Locality.city("United States", "California", "San Francisco");
 * }</pre>
 *
 * <p><strong>Data-Oriented Design:</strong>
 *
 * <ul>
 *   <li>Immutable data structure
 *   <li>Clear hierarchical representation
 *   <li>Null-safe handling of optional levels
 *   <li>Convenient factory methods for common patterns
 * </ul>
 *
 * @author Vagner Clementino
 * @since 1.0
 * @see Holiday
 * @see FixedHoliday
 * @see MoveableHoliday
 */
public class Locality {
  private final String country;
  private final String subdivision;
  private final String city;

  // ================================================================================================
  // PUBLIC CONSTRUCTORS
  // ================================================================================================

  /**
   * Creates a new Locality with the specified geographical components.
   *
   * @param country the country name (must not be null or blank)
   * @param subdivision the state/province name (can be null for national holidays)
   * @param city the city name (can be null for national or state holidays)
   * @throws IllegalArgumentException if country is null or blank
   */
  public Locality(String country, String subdivision, String city) {
    if (country == null || country.trim().isEmpty()) {
      throw new IllegalArgumentException("Country cannot be null or blank");
    }
    this.country = country.trim();
    this.subdivision = subdivision != null ? subdivision.trim() : null;
    this.city = city != null ? city.trim() : null;
  }

  // ================================================================================================
  // PUBLIC FACTORY METHODS
  // ================================================================================================

  /**
   * Creates a country-level locality.
   *
   * @param country the country name (must not be null or blank)
   * @return a new Locality representing the entire country
   * @throws IllegalArgumentException if country is null or blank
   */
  public static Locality country(String country) {
    return new Locality(country, null, null);
  }

  /**
   * Creates a subdivision-level locality (state/province).
   *
   * @param country the country name (must not be null or blank)
   * @param subdivision the subdivision name (must not be null or blank)
   * @return a new Locality representing the subdivision within the country
   * @throws IllegalArgumentException if country or subdivision is null or blank
   */
  public static Locality subdivision(String country, String subdivision) {
    if (subdivision == null || subdivision.trim().isEmpty()) {
      throw new IllegalArgumentException("Subdivision cannot be null or blank");
    }
    return new Locality(country, subdivision, null);
  }

  /**
   * Creates a city-level locality.
   *
   * @param country the country name (must not be null or blank)
   * @param subdivision the subdivision name (must not be null or blank)
   * @param city the city name (must not be null or blank)
   * @return a new Locality representing the city within the subdivision and country
   * @throws IllegalArgumentException if any parameter is null or blank
   */
  public static Locality city(String country, String subdivision, String city) {
    if (subdivision == null || subdivision.trim().isEmpty()) {
      throw new IllegalArgumentException(
          "Subdivision cannot be null or blank for city-level locality");
    }
    if (city == null || city.trim().isEmpty()) {
      throw new IllegalArgumentException("City cannot be null or blank");
    }
    return new Locality(country, subdivision, city);
  }

  // ================================================================================================
  // PUBLIC GETTER METHODS
  // ================================================================================================

  /**
   * Returns the country name.
   *
   * @return the country name (never null)
   */
  public String getCountry() {
    return country;
  }

  /**
   * Returns the subdivision name (state/province).
   *
   * @return the subdivision name (may be null for country-level localities)
   */
  public String getSubdivision() {
    return subdivision;
  }

  /**
   * Returns the city name.
   *
   * @return the city name (may be null for country or subdivision-level localities)
   */
  public String getCity() {
    return city;
  }

  // ================================================================================================
  // PUBLIC UTILITY METHODS
  // ================================================================================================

  /**
   * Checks if this is a country-level locality.
   *
   * @return true if only country is specified, false otherwise
   */
  public boolean isCountryLevel() {
    return subdivision == null && city == null;
  }

  /**
   * Checks if this is a subdivision-level locality.
   *
   * @return true if country and subdivision are specified but city is not, false otherwise
   */
  public boolean isSubdivisionLevel() {
    return subdivision != null && city == null;
  }

  /**
   * Checks if this is a city-level locality.
   *
   * @return true if all levels (country, subdivision, city) are specified, false otherwise
   */
  public boolean isCityLevel() {
    return subdivision != null && city != null;
  }

  /**
   * Returns the administrative level of this locality.
   *
   * @return "COUNTRY", "SUBDIVISION", or "CITY" depending on the specified components
   */
  public String getLevel() {
    if (isCityLevel()) return "CITY";
    if (isSubdivisionLevel()) return "SUBDIVISION";
    return "COUNTRY";
  }

  /**
   * Returns a formatted display name for this locality.
   *
   * @return a human-readable representation of the locality
   */
  public String getDisplayName() {
    if (isCityLevel()) {
      return String.format("%s, %s, %s", city, subdivision, country);
    }
    if (isSubdivisionLevel()) {
      return String.format("%s, %s", subdivision, country);
    }
    return country;
  }

  /**
   * Returns a string representation of this locality.
   *
   * @return a formatted string containing the locality information
   */
  @Override
  public String toString() {
    return String.format(
        "Locality{country='%s', subdivision='%s', city='%s', level='%s'}",
        country, subdivision, city, getLevel());
  }

  /**
   * Computes the hash code for this locality.
   *
   * @return the hash code value based on country, subdivision, and city
   */
  @Override
  public int hashCode() {
    int result = country.hashCode();
    result = 31 * result + (subdivision != null ? subdivision.hashCode() : 0);
    result = 31 * result + (city != null ? city.hashCode() : 0);
    return result;
  }

  /**
   * Compares this locality with another object for equality.
   *
   * <p>Two localities are considered equal if they have the same country, subdivision, and city.
   *
   * @param obj the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    Locality locality = (Locality) obj;

    if (!country.equals(locality.country)) return false;
    if (subdivision != null
        ? !subdivision.equals(locality.subdivision)
        : locality.subdivision != null) return false;
    return city != null ? city.equals(locality.city) : locality.city == null;
  }
}
