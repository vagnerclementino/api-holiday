package me.clementino.holiday.domain.dop;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.Objects;

/**
 * Sealed interface representing a geographical locality where a holiday is observed.
 *
 * <p>This sealed interface models the hierarchical nature of geographical locations using
 * Data-Oriented Programming principles. It makes illegal states unrepresentable by ensuring that a
 * locality can only be one of three specific types: Country, Subdivision, or City.
 *
 * <p><strong>DOP Principles Applied:</strong>
 *
 * <ul>
 *   <li><strong>Model Data Immutably and Transparently:</strong> All variants are immutable records
 *   <li><strong>Model the Data, the Whole Data, and Nothing but the Data:</strong> Each variant
 *       contains exactly what it needs
 *   <li><strong>Make Illegal States Unrepresentable:</strong> Sealed interface prevents invalid
 *       locality types
 *   <li><strong>Separate Operations from Data:</strong> No behavior methods, only data
 * </ul>
 *
 * <p><strong>Hierarchy Levels:</strong>
 *
 * <ul>
 *   <li><strong>Country:</strong> National level with code and name
 *   <li><strong>Subdivision:</strong> State/Province level with country, code, and name
 *   <li><strong>City:</strong> Municipal level with name, subdivision, and country
 * </ul>
 *
 * <p><strong>Jackson Configuration:</strong> - Uses @JsonTypeInfo to determine which concrete type
 * to deserialize - Property "localityType" in JSON determines the implementation (Country,
 * Subdivision, City)
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "localityType")
@JsonSubTypes({
  @JsonSubTypes.Type(value = Locality.Country.class, name = "Country"),
  @JsonSubTypes.Type(value = Locality.Subdivision.class, name = "Subdivision"),
  @JsonSubTypes.Type(value = Locality.City.class, name = "City")
})

/**
 * Sealed interface representing a geographical locality where a holiday is observed.
 *
 * <p>This sealed interface models the hierarchical nature of geographical locations using
 * Data-Oriented Programming principles. It makes illegal states unrepresentable by ensuring that a
 * locality can only be one of three specific types: Country, Subdivision, or City.
 *
 * <p><strong>DOP Principles Applied:</strong>
 *
 * <ul>
 *   <li><strong>Model Data Immutably and Transparently:</strong> All variants are immutable records
 *   <li><strong>Model the Data, the Whole Data, and Nothing but the Data:</strong> Each variant
 *       contains exactly what it needs
 *   <li><strong>Make Illegal States Unrepresentable:</strong> Sealed interface prevents invalid
 *       locality types
 *   <li><strong>Separate Operations from Data:</strong> No behavior methods, only data
 * </ul>
 *
 * <p><strong>Hierarchy Levels:</strong>
 *
 * <ul>
 *   <li><strong>Country:</strong> National level with code and name
 *   <li><strong>Subdivision:</strong> State/Province level with country, code, and name
 *   <li><strong>City:</strong> Municipal level with name, subdivision, and country
 * </ul>
 *
 * <p><strong>Usage Examples:</strong>
 *
 * <pre>{@code
 * // National holiday
 * Locality brazil = new Locality.Country("BR", "Brazil");
 *
 * // State-level holiday
 * Locality saoPauloState = new Locality.Subdivision(
 *     new Locality.Country("BR", "Brazil"),
 *     "SP",
 *     "São Paulo"
 * );
 *
 * // City-level holiday
 * Locality saoPauloCity = new Locality.City(
 *     "São Paulo",
 *     new Locality.Subdivision(
 *         new Locality.Country("BR", "Brazil"),
 *         "SP",
 *         "São Paulo"
 *     ),
 *     new Locality.Country("BR", "Brazil")
 * );
 * }</pre>
 */
public sealed interface Locality permits Locality.Country, Locality.Subdivision, Locality.City {

  /**
   * Country locality record - represents a national-level location.
   *
   * @param code ISO country code (e.g., "BR", "US", "CA")
   * @param name Full country name (e.g., "Brazil", "United States", "Canada")
   */
  record Country(String code, String name) implements Locality {
    public Country {
      Objects.requireNonNull(code, "Country code cannot be null");
      Objects.requireNonNull(name, "Country name cannot be null");

      if (code.isBlank()) {
        throw new IllegalArgumentException("Country code cannot be blank");
      }
      if (name.isBlank()) {
        throw new IllegalArgumentException("Country name cannot be blank");
      }
      if (code.length() != 2) {
        throw new IllegalArgumentException(
            "Country code must be exactly 2 characters (ISO 3166-1 alpha-2)");
      }
    }

    // Transformation methods
    public Country withCode(String newCode) {
      return new Country(newCode, name);
    }

    public Country withName(String newName) {
      return new Country(code, newName);
    }
  }

  /**
   * Subdivision locality record - represents a state/province-level location.
   *
   * @param country The country this subdivision belongs to
   * @param code Subdivision code (e.g., "SP", "CA", "NY")
   * @param name Full subdivision name (e.g., "São Paulo", "California", "New York")
   */
  record Subdivision(Country country, String code, String name) implements Locality {
    public Subdivision {
      Objects.requireNonNull(country, "Country cannot be null");
      Objects.requireNonNull(code, "Subdivision code cannot be null");
      Objects.requireNonNull(name, "Subdivision name cannot be null");

      if (code.isBlank()) {
        throw new IllegalArgumentException("Subdivision code cannot be blank");
      }
      if (name.isBlank()) {
        throw new IllegalArgumentException("Subdivision name cannot be blank");
      }
    }

    // Transformation methods
    public Subdivision withCountry(Country newCountry) {
      return new Subdivision(newCountry, code, name);
    }

    public Subdivision withCode(String newCode) {
      return new Subdivision(country, newCode, name);
    }

    public Subdivision withName(String newName) {
      return new Subdivision(country, code, newName);
    }
  }

  /**
   * City locality record - represents a municipal-level location.
   *
   * @param name City name (e.g., "São Paulo", "San Francisco", "New York")
   * @param subdivision The subdivision this city belongs to
   * @param country The country this city belongs to
   */
  record City(String name, Subdivision subdivision, Country country) implements Locality {
    public City {
      Objects.requireNonNull(name, "City name cannot be null");
      Objects.requireNonNull(subdivision, "Subdivision cannot be null");
      Objects.requireNonNull(country, "Country cannot be null");

      if (name.isBlank()) {
        throw new IllegalArgumentException("City name cannot be blank");
      }

      // Validate consistency: subdivision's country must match city's country
      if (!subdivision.country().equals(country)) {
        throw new IllegalArgumentException(
            "City's country must match subdivision's country. "
                + "City country: "
                + country
                + ", Subdivision country: "
                + subdivision.country());
      }
    }

    // Transformation methods
    public City withName(String newName) {
      return new City(newName, subdivision, country);
    }

    public City withSubdivision(Subdivision newSubdivision) {
      // Ensure country consistency when changing subdivision
      return new City(name, newSubdivision, newSubdivision.country());
    }

    public City withCountry(Country newCountry) {
      // When changing country, update subdivision to match
      var newSubdivision = subdivision.withCountry(newCountry);
      return new City(name, newSubdivision, newCountry);
    }
  }

  // Factory methods for convenience
  static Country country(String code, String name) {
    return new Country(code, name);
  }

  static Subdivision subdivision(Country country, String code, String name) {
    return new Subdivision(country, code, name);
  }

  static City city(String name, Subdivision subdivision, Country country) {
    return new City(name, subdivision, country);
  }

  // Convenience factory methods for common patterns
  static Country brazil() {
    return new Country("BR", "Brazil");
  }

  static Country unitedStates() {
    return new Country("US", "United States");
  }

  static Country canada() {
    return new Country("CA", "Canada");
  }

  static Subdivision saoPauloState() {
    return new Subdivision(brazil(), "SP", "São Paulo");
  }

  static Subdivision california() {
    return new Subdivision(unitedStates(), "CA", "California");
  }

  static City saoPauloCity() {
    return new City("São Paulo", saoPauloState(), brazil());
  }

  static City sanFrancisco() {
    return new City("San Francisco", california(), unitedStates());
  }
}
