package me.clementino.holiday.domain.dop;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Sealed interface representing different types of holidays using Data-Oriented Programming
 * principles.
 *
 * <p>This implementation uses <strong>Solution 1: Interface Methods</strong> to eliminate
 * repetition while maintaining all DOP principles:
 *
 * <ul>
 *   <li><strong>Zero Repetition</strong>: Common attributes defined once as interface methods
 *   <li><strong>Type Safety</strong>: Compiler ensures all methods are implemented
 *   <li><strong>Performance</strong>: Direct field access in records
 *   <li><strong>Common Functionality</strong>: Default methods for shared behavior
 *   <li><strong>Pattern Matching</strong>: Works perfectly with sealed interfaces
 * </ul>
 *
 * <p><strong>DOP Principles Applied:</strong>
 *
 * <ol>
 *   <li><strong>Model Data Immutably and Transparently</strong> - All variants are immutable
 *       records
 *   <li><strong>Model the Data, the Whole Data, and Nothing but the Data</strong> - Each variant
 *       contains exactly what it needs
 *   <li><strong>Make Illegal States Unrepresentable</strong> - Sealed interface prevents invalid
 *       holiday types
 *   <li><strong>Separate Operations from Data</strong> - No behavior methods, only data and derived
 *       calculations
 * </ol>
 *
 * <p><strong>Jackson Configuration:</strong> - Uses @JsonTypeInfo to determine which concrete type
 * to deserialize - Property "holidayVariant" in JSON determines the implementation
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "holidayVariant")
@JsonSubTypes({
  @JsonSubTypes.Type(value = FixedHoliday.class, name = "FixedHoliday"),
  @JsonSubTypes.Type(value = ObservedHoliday.class, name = "ObservedHoliday"),
  @JsonSubTypes.Type(value = MoveableHoliday.class, name = "MoveableHoliday"),
  @JsonSubTypes.Type(value = MoveableFromBaseHoliday.class, name = "MoveableFromBaseHoliday")
})

/**
 * Sealed interface representing different types of holidays using Data-Oriented Programming
 * principles.
 *
 * <p>This implementation uses <strong>Solution 1: Interface Methods</strong> to eliminate
 * repetition while maintaining all DOP principles:
 *
 * <ul>
 *   <li><strong>Zero Repetition</strong>: Common attributes defined once as interface methods
 *   <li><strong>Type Safety</strong>: Compiler ensures all methods are implemented
 *   <li><strong>Performance</strong>: Direct field access in records
 *   <li><strong>Common Functionality</strong>: Default methods for shared behavior
 *   <li><strong>Pattern Matching</strong>: Works perfectly with sealed interfaces
 * </ul>
 *
 * <p><strong>DOP Principles Applied:</strong>
 *
 * <ol>
 *   <li><strong>Model Data Immutably and Transparently</strong> - All variants are immutable
 *       records
 *   <li><strong>Model the Data, the Whole Data, and Nothing but the Data</strong> - Each variant
 *       contains exactly what it needs
 *   <li><strong>Make Illegal States Unrepresentable</strong> - Sealed interface prevents invalid
 *       holiday types
 *   <li><strong>Separate Operations from Data</strong> - Operations in HolidayOperations, data in
 *       records
 * </ol>
 *
 * @see HolidayOperations for operations on holidays
 */
public sealed interface Holiday
    permits FixedHoliday, ObservedHoliday, MoveableHoliday, MoveableFromBaseHoliday {

  /**
   * The name of the holiday.
   *
   * @return holiday name, never null or blank
   */
  String name();

  /**
   * A description of the holiday.
   *
   * @return holiday description, never null
   */
  String description();

  /**
   * The date when this holiday occurs.
   *
   * @return holiday date, never null
   */
  LocalDate date();

  /**
   * The localities where this holiday is observed.
   *
   * @return list of localities, never null or empty
   */
  List<Locality> localities();

  /**
   * The type/category of this holiday.
   *
   * @return holiday type, never null
   */
  HolidayType type();

  /**
   * Checks if this holiday falls on a weekend for its current date.
   *
   * @return true if the holiday falls on Saturday or Sunday
   */
  default boolean isWeekend() {
    DayOfWeek dayOfWeek = date().getDayOfWeek();
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
  }

  /**
   * Gets a formatted display name for this holiday including its type.
   *
   * @return formatted display name like "Christmas Day (religious)"
   */
  default String getDisplayName() {
    return name() + " (" + type().name().toLowerCase() + ")";
  }

  /**
   * Checks if this holiday applies to a specific locality using hierarchical matching. A national
   * holiday applies to all subdivisions and cities in that country. A state holiday applies to all
   * cities in that state. A city holiday only applies to that specific city.
   *
   * @param targetLocality the locality to check against
   * @return true if this holiday applies to the target locality
   */
  default boolean appliesTo(Locality targetLocality) {
    if (targetLocality == null) {
      return false;
    }

    return localities().stream()
        .anyMatch(holidayLocality -> localityMatches(holidayLocality, targetLocality));
  }

  /**
   * Gets a summary of this holiday including name, type, and locality information.
   *
   * @return formatted summary string
   */
  default String getSummary() {
    String localityInfo =
        localities().size() == 1
            ? formatLocality(localities().getFirst())
            : localities().size() + " localities";

    return String.format(
        "%s - %s holiday in %s", name(), type().name().toLowerCase(), localityInfo);
  }

  /**
   * Checks if this holiday is of a governmental type (national, state, or municipal).
   *
   * @return true if this is a governmental holiday
   */
  default boolean isGovernmental() {
    return type() == HolidayType.NATIONAL
        || type() == HolidayType.STATE
        || type() == HolidayType.MUNICIPAL;
  }

  /**
   * Checks if this holiday is observed in a specific country.
   *
   * @param countryCode the ISO country code to check
   * @return true if this holiday is observed in the specified country
   */
  default boolean isObservedInCountry(String countryCode) {
    if (countryCode == null || countryCode.isBlank()) {
      return false;
    }

    return localities().stream()
        .anyMatch(
            locality ->
                switch (locality) {
                  case Locality.Country country -> country.code().equalsIgnoreCase(countryCode);
                  case Locality.Subdivision subdivision ->
                      subdivision.country().code().equalsIgnoreCase(countryCode);
                  case Locality.City city ->
                      city.subdivision().country().code().equalsIgnoreCase(countryCode);
                });
  }

  /** Checks if a holiday locality matches a target locality using hierarchical matching. */
  private boolean localityMatches(Locality holidayLocality, Locality targetLocality) {
    return switch (holidayLocality) {
      case Locality.Country holidayCountry ->
          switch (targetLocality) {
            case Locality.Country targetCountry -> holidayCountry.equals(targetCountry);
            case Locality.Subdivision targetSubdivision ->
                holidayCountry.equals(targetSubdivision.country());
            case Locality.City targetCity ->
                holidayCountry.equals(targetCity.subdivision().country());
          };
      case Locality.Subdivision holidaySubdivision ->
          switch (targetLocality) {
            case Locality.Country targetCountry -> false;
            case Locality.Subdivision targetSubdivision ->
                holidaySubdivision.equals(targetSubdivision);
            case Locality.City targetCity -> holidaySubdivision.equals(targetCity.subdivision());
          };
      case Locality.City holidayCity ->
          switch (targetLocality) {
            case Locality.Country targetCountry -> false;
            case Locality.Subdivision targetSubdivision -> false;
            case Locality.City targetCity -> holidayCity.equals(targetCity);
          };
    };
  }

  /** Formats a single locality for display. */
  private String formatLocality(Locality locality) {
    return switch (locality) {
      case Locality.Country country -> country.name();
      case Locality.Subdivision subdivision ->
          subdivision.name() + ", " + subdivision.country().name();
      case Locality.City city ->
          city.name()
              + ", "
              + city.subdivision().name()
              + ", "
              + city.subdivision().country().name();
    };
  }
}
