package me.clementino.holiday.domain.dop;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import me.clementino.holiday.domain.HolidayType;

/**
 * Holiday that is calculated relative to another holiday with a day offset.
 *
 * <p>This record implements the Holiday interface, automatically inheriting all common methods
 * without repetition. It adds derived-specific attributes: known holiday type, base holiday, day
 * offset, and mondayisation flag.
 *
 * <p><strong>Examples:</strong>
 *
 * <ul>
 *   <li>Good Friday (2 days before Easter)
 *   <li>Easter Monday (1 day after Easter)
 *   <li>Palm Sunday (7 days before Easter)
 *   <li>Ash Wednesday (46 days before Easter)
 *   <li>Black Friday (1 day after Thanksgiving)
 * </ul>
 *
 * <p><strong>Calculation:</strong> The date is calculated by first determining the base holiday's
 * date for a given year, then adding the day offset. Positive offsets are days after the base
 * holiday, negative offsets are days before.
 *
 * <p><strong>DOP Principles:</strong>
 *
 * <ul>
 *   <li><strong>Immutable</strong>: All fields are final, transformation methods return new
 *       instances
 *   <li><strong>Transparent</strong>: All data is directly accessible
 *   <li><strong>Complete</strong>: Contains exactly the data needed for a derived holiday
 *   <li><strong>Valid</strong>: Constructor validation prevents illegal states
 * </ul>
 */
public record MoveableFromBaseHoliday(
    String name,
    String description,
    LocalDate date,
    List<Locality> localities,
    HolidayType type,
    // Additional attributes specific to MoveableFromBaseHoliday
    KnownHoliday knownHoliday,
    Holiday baseHoliday,
    int dayOffset,
    boolean mondayisation)
    implements Holiday {

  /**
   * Compact constructor with validation to ensure data integrity. This prevents the creation of
   * invalid derived holiday instances.
   */
  public MoveableFromBaseHoliday {
    Objects.requireNonNull(name, "Holiday name cannot be null");
    Objects.requireNonNull(description, "Holiday description cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(localities, "Holiday localities cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");
    Objects.requireNonNull(knownHoliday, "Known holiday cannot be null");
    Objects.requireNonNull(baseHoliday, "Base holiday cannot be null");

    if (name.isBlank()) {
      throw new IllegalArgumentException("Holiday name cannot be blank");
    }
    if (localities.isEmpty()) {
      throw new IllegalArgumentException("Holiday must have at least one locality");
    }

    // Prevent circular dependencies
    if (baseHoliday instanceof MoveableFromBaseHoliday derived && derived.baseHoliday() == this) {
      throw new IllegalArgumentException(
          "Circular dependency detected: base holiday cannot reference this holiday");
    }

    // Ensure localities list is immutable
    localities = List.copyOf(localities);
  }

  // ===== TRANSFORMATION METHODS =====
  // These methods return new instances, maintaining immutability

  /**
   * Returns a new MoveableFromBaseHoliday with the specified name.
   *
   * @param newName the new name for the holiday
   * @return new MoveableFromBaseHoliday instance with updated name
   */
  public MoveableFromBaseHoliday withName(String newName) {
    return new MoveableFromBaseHoliday(
        newName,
        description,
        date,
        localities,
        type,
        knownHoliday,
        baseHoliday,
        dayOffset,
        mondayisation);
  }

  /**
   * Returns a new MoveableFromBaseHoliday with the specified description.
   *
   * @param newDescription the new description for the holiday
   * @return new MoveableFromBaseHoliday instance with updated description
   */
  public MoveableFromBaseHoliday withDescription(String newDescription) {
    return new MoveableFromBaseHoliday(
        name,
        newDescription,
        date,
        localities,
        type,
        knownHoliday,
        baseHoliday,
        dayOffset,
        mondayisation);
  }

  /**
   * Returns a new MoveableFromBaseHoliday with the specified date. This is commonly used when
   * calculating holidays for different years.
   *
   * @param newDate the new date for the holiday
   * @return new MoveableFromBaseHoliday instance with updated date
   */
  public MoveableFromBaseHoliday withDate(LocalDate newDate) {
    return new MoveableFromBaseHoliday(
        name,
        description,
        newDate,
        localities,
        type,
        knownHoliday,
        baseHoliday,
        dayOffset,
        mondayisation);
  }

  /**
   * Returns a new MoveableFromBaseHoliday with the specified localities.
   *
   * @param newLocalities the new localities for the holiday
   * @return new MoveableFromBaseHoliday instance with updated localities
   */
  public MoveableFromBaseHoliday withLocalities(List<Locality> newLocalities) {
    return new MoveableFromBaseHoliday(
        name,
        description,
        date,
        newLocalities,
        type,
        knownHoliday,
        baseHoliday,
        dayOffset,
        mondayisation);
  }

  /**
   * Returns a new MoveableFromBaseHoliday with the specified type.
   *
   * @param newType the new type for the holiday
   * @return new MoveableFromBaseHoliday instance with updated type
   */
  public MoveableFromBaseHoliday withType(HolidayType newType) {
    return new MoveableFromBaseHoliday(
        name,
        description,
        date,
        localities,
        newType,
        knownHoliday,
        baseHoliday,
        dayOffset,
        mondayisation);
  }

  /**
   * Returns a new MoveableFromBaseHoliday with the specified known holiday.
   *
   * @param newKnownHoliday the new known holiday type
   * @return new MoveableFromBaseHoliday instance with updated known holiday
   */
  public MoveableFromBaseHoliday withKnownHoliday(KnownHoliday newKnownHoliday) {
    return new MoveableFromBaseHoliday(
        name,
        description,
        date,
        localities,
        type,
        newKnownHoliday,
        baseHoliday,
        dayOffset,
        mondayisation);
  }

  /**
   * Returns a new MoveableFromBaseHoliday with the specified base holiday.
   *
   * @param newBaseHoliday the new base holiday
   * @return new MoveableFromBaseHoliday instance with updated base holiday
   */
  public MoveableFromBaseHoliday withBaseHoliday(Holiday newBaseHoliday) {
    return new MoveableFromBaseHoliday(
        name,
        description,
        date,
        localities,
        type,
        knownHoliday,
        newBaseHoliday,
        dayOffset,
        mondayisation);
  }

  /**
   * Returns a new MoveableFromBaseHoliday with the specified day offset.
   *
   * @param newDayOffset the new day offset
   * @return new MoveableFromBaseHoliday instance with updated day offset
   */
  public MoveableFromBaseHoliday withDayOffset(int newDayOffset) {
    return new MoveableFromBaseHoliday(
        name,
        description,
        date,
        localities,
        type,
        knownHoliday,
        baseHoliday,
        newDayOffset,
        mondayisation);
  }

  /**
   * Returns a new MoveableFromBaseHoliday with the specified mondayisation setting.
   *
   * @param newMondayisation the new mondayisation setting
   * @return new MoveableFromBaseHoliday instance with updated mondayisation
   */
  public MoveableFromBaseHoliday withMondayisation(boolean newMondayisation) {
    return new MoveableFromBaseHoliday(
        name,
        description,
        date,
        localities,
        type,
        knownHoliday,
        baseHoliday,
        dayOffset,
        newMondayisation);
  }

  // ===== DERIVED-SPECIFIC METHODS =====

  /**
   * Gets the year for which this holiday's date was calculated.
   *
   * @return the year of the current date
   */
  public int getCalculatedYear() {
    return date.getYear();
  }

  /**
   * Checks if this holiday occurs before its base holiday.
   *
   * @return true if this holiday occurs before the base holiday (negative offset)
   */
  public boolean occursBefore() {
    return dayOffset < 0;
  }

  /**
   * Checks if this holiday occurs after its base holiday.
   *
   * @return true if this holiday occurs after the base holiday (positive offset)
   */
  public boolean occursAfter() {
    return dayOffset > 0;
  }

  /**
   * Checks if this holiday occurs on the same day as its base holiday.
   *
   * @return true if this holiday occurs on the same day (zero offset)
   */
  public boolean occursSameDay() {
    return dayOffset == 0;
  }

  /**
   * Gets the absolute number of days between this holiday and its base holiday.
   *
   * @return absolute number of days difference
   */
  public int getAbsoluteDayOffset() {
    return Math.abs(dayOffset);
  }

  /**
   * Gets a description of the relationship to the base holiday.
   *
   * @return relationship description like "2 days before Easter" or "1 day after Thanksgiving"
   */
  public String getRelationshipDescription() {
    if (dayOffset == 0) {
      return "same day as " + baseHoliday.name();
    } else if (dayOffset == 1) {
      return "1 day after " + baseHoliday.name();
    } else if (dayOffset == -1) {
      return "1 day before " + baseHoliday.name();
    } else if (dayOffset > 0) {
      return dayOffset + " days after " + baseHoliday.name();
    } else {
      return Math.abs(dayOffset) + " days before " + baseHoliday.name();
    }
  }

  /**
   * Gets information about the calculation method for this holiday.
   *
   * @return description of how this holiday is calculated
   */
  public String getCalculationInfo() {
    return "Calculated as " + getRelationshipDescription();
  }

  /**
   * Checks if this holiday requires algorithmic calculation.
   *
   * @return true (derived holidays always require calculation)
   */
  public boolean requiresCalculation() {
    return true;
  }

  /**
   * Gets the calculation category for this holiday.
   *
   * @return "Derived from " + base holiday type
   */
  public String getCalculationCategory() {
    String baseCategory =
        switch (baseHoliday) {
          case MoveableHoliday moveable -> moveable.getCalculationCategory();
          case MoveableFromBaseHoliday derived -> derived.getCalculationCategory();
          default -> "Fixed";
        };
    return "Derived from " + baseCategory;
  }

  /**
   * Checks if this holiday is ultimately based on lunar calculations. This traces through the chain
   * of base holidays to find the root calculation method.
   *
   * @return true if the root base holiday uses lunar calculations
   */
  public boolean isUltimatelyLunarBased() {
    return switch (baseHoliday) {
      case MoveableHoliday moveable -> moveable.isLunarBased();
      case MoveableFromBaseHoliday derived -> derived.isUltimatelyLunarBased();
      default -> false;
    };
  }

  /**
   * Gets the root base holiday by following the chain of derived holidays.
   *
   * @return the ultimate base holiday that is not derived from another holiday
   */
  public Holiday getRootBaseHoliday() {
    return switch (baseHoliday) {
      case MoveableFromBaseHoliday derived -> derived.getRootBaseHoliday();
      default -> baseHoliday;
    };
  }

  /**
   * Gets the depth of derivation (how many levels of derived holidays).
   *
   * @return depth level (1 for direct derivation, 2+ for nested derivation)
   */
  public int getDerivationDepth() {
    return switch (baseHoliday) {
      case MoveableFromBaseHoliday derived -> 1 + derived.getDerivationDepth();
      default -> 1;
    };
  }

  /**
   * Gets a detailed description including calculation method and mondayisation info.
   *
   * @return detailed description string
   */
  public String getDetailedDescription() {
    String base = description + " (" + getCalculationInfo() + ")";
    if (mondayisation) {
      base += " with mondayisation";
    }
    return base;
  }

  // ===== INHERITED METHODS FROM HOLIDAY INTERFACE =====
  // The following methods are automatically available from the Holiday interface:
  // - name(), description(), date(), localities(), type() (record accessors)
  // - isWeekend(), getDisplayName(), appliesTo(), getSummary(), isGovernmental(),
  // isObservedInCountry() (default methods)
}
