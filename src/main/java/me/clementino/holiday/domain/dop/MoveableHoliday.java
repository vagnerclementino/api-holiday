package me.clementino.holiday.domain.dop;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Moveable holiday that is calculated using algorithms and may vary from year to year.
 *
 * <p>This record implements the Holiday interface, automatically inheriting all common methods
 * without repetition. It adds moveable-specific attributes: known holiday type and mondayisation
 * flag.
 *
 * <p><strong>Examples:</strong>
 *
 * <ul>
 *   <li>Easter Sunday (calculated using lunar calendar)
 *   <li>Thanksgiving (4th Thursday of November in US)
 *   <li>Memorial Day (last Monday of May in US)
 *   <li>Labor Day (1st Monday of September in US)
 *   <li>Mother's Day (2nd Sunday of May)
 *   <li>Father's Day (3rd Sunday of June)
 * </ul>
 *
 * <p><strong>Calculation:</strong> The date field contains a calculated date for a specific year.
 * To get the date for a different year, use HolidayOperations.calculateDate() which will return a
 * new instance with the recalculated date.
 *
 * <p><strong>DOP Principles:</strong>
 *
 * <ul>
 *   <li><strong>Immutable</strong>: All fields are final, transformation methods return new
 *       instances
 *   <li><strong>Transparent</strong>: All data is directly accessible
 *   <li><strong>Complete</strong>: Contains exactly the data needed for a moveable holiday
 *   <li><strong>Valid</strong>: Constructor validation prevents illegal states
 * </ul>
 */
public record MoveableHoliday(
    String name,
    String description,
    LocalDate date,
    List<Locality> localities,
    HolidayType type,
    KnownHoliday knownHoliday,
    boolean mondayisation)
    implements Holiday {

  /**
   * Compact constructor with validation to ensure data integrity. This prevents the creation of
   * invalid moveable holiday instances.
   */
  public MoveableHoliday {
    Objects.requireNonNull(name, "Holiday name cannot be null");
    Objects.requireNonNull(description, "Holiday description cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(localities, "Holiday localities cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");
    Objects.requireNonNull(knownHoliday, "Known holiday cannot be null");

    if (name.isBlank()) {
      throw new IllegalArgumentException("Holiday name cannot be blank");
    }
    if (localities.isEmpty()) {
      throw new IllegalArgumentException("Holiday must have at least one locality");
    }

    localities = List.copyOf(localities);
  }

  /**
   * Returns a new MoveableHoliday with the specified name.
   *
   * @param newName the new name for the holiday
   * @return new MoveableHoliday instance with updated name
   */
  public MoveableHoliday withName(String newName) {
    return new MoveableHoliday(
        newName, description, date, localities, type, knownHoliday, mondayisation);
  }

  /**
   * Returns a new MoveableHoliday with the specified description.
   *
   * @param newDescription the new description for the holiday
   * @return new MoveableHoliday instance with updated description
   */
  public MoveableHoliday withDescription(String newDescription) {
    return new MoveableHoliday(
        name, newDescription, date, localities, type, knownHoliday, mondayisation);
  }

  /**
   * Returns a new MoveableHoliday with the specified date. This is commonly used when calculating
   * holidays for different years.
   *
   * @param newDate the new date for the holiday
   * @return new MoveableHoliday instance with updated date
   */
  public MoveableHoliday withDate(LocalDate newDate) {
    return new MoveableHoliday(
        name, description, newDate, localities, type, knownHoliday, mondayisation);
  }

  /**
   * Returns a new MoveableHoliday with the specified localities.
   *
   * @param newLocalities the new localities for the holiday
   * @return new MoveableHoliday instance with updated localities
   */
  public MoveableHoliday withLocalities(List<Locality> newLocalities) {
    return new MoveableHoliday(
        name, description, date, newLocalities, type, knownHoliday, mondayisation);
  }

  /**
   * Returns a new MoveableHoliday with the specified type.
   *
   * @param newType the new type for the holiday
   * @return new MoveableHoliday instance with updated type
   */
  public MoveableHoliday withType(HolidayType newType) {
    return new MoveableHoliday(
        name, description, date, localities, newType, knownHoliday, mondayisation);
  }

  /**
   * Returns a new MoveableHoliday with the specified known holiday.
   *
   * @param newKnownHoliday the new known holiday type
   * @return new MoveableHoliday instance with updated known holiday
   */
  public MoveableHoliday withKnownHoliday(KnownHoliday newKnownHoliday) {
    return new MoveableHoliday(
        name, description, date, localities, type, newKnownHoliday, mondayisation);
  }

  /**
   * Returns a new MoveableHoliday with the specified mondayisation setting.
   *
   * @param newMondayisation the new mondayisation setting
   * @return new MoveableHoliday instance with updated mondayisation
   */
  public MoveableHoliday withMondayisation(boolean newMondayisation) {
    return new MoveableHoliday(
        name, description, date, localities, type, knownHoliday, newMondayisation);
  }

  /**
   * Gets the year for which this holiday's date was calculated.
   *
   * @return the year of the current date
   */
  public int getCalculatedYear() {
    return date.getYear();
  }

  /**
   * Gets information about the calculation method for this holiday.
   *
   * @return description of how this holiday is calculated
   */
  public String getCalculationInfo() {
    return switch (knownHoliday) {
      case EASTER -> "Calculated using lunar calendar (Jean Meeus algorithm)";
      case THANKSGIVING_US -> "4th Thursday of November";
      case MEMORIAL_DAY_US -> "Last Monday of May";
      case LABOR_DAY_US -> "1st Monday of September";
      case MOTHERS_DAY -> "2nd Sunday of May";
      case FATHERS_DAY -> "3rd Sunday of June";
      default -> "Calculated using specific algorithm for " + knownHoliday.getDisplayName();
    };
  }

  /**
   * Checks if this holiday is based on lunar calculations.
   *
   * @return true if this holiday uses lunar calendar calculations
   */
  public boolean isLunarBased() {
    return knownHoliday == KnownHoliday.EASTER;
  }

  /**
   * Checks if this holiday is based on weekday calculations.
   *
   * @return true if this holiday is calculated based on specific weekdays
   */
  public boolean isWeekdayBased() {
    return switch (knownHoliday) {
      case THANKSGIVING_US, MEMORIAL_DAY_US, LABOR_DAY_US, MOTHERS_DAY, FATHERS_DAY -> true;
      default -> false;
    };
  }

  /**
   * Gets the category of calculation method used for this holiday.
   *
   * @return calculation category
   */
  public String getCalculationCategory() {
    if (isLunarBased()) {
      return "Lunar-based";
    } else if (isWeekdayBased()) {
      return "Weekday-based";
    } else {
      return "Algorithm-based";
    }
  }

  /**
   * Checks if this holiday typically falls in the same month every year.
   *
   * @return true if the holiday always occurs in the same month
   */
  public boolean hasFixedMonth() {
    return switch (knownHoliday) {
      case THANKSGIVING_US, MEMORIAL_DAY_US, LABOR_DAY_US, MOTHERS_DAY, FATHERS_DAY -> true;
      default -> false;
    };
  }

  /**
   * Gets the typical month for this holiday, if it has a fixed month.
   *
   * @return the month, or null if the holiday doesn't have a fixed month
   */
  public java.time.Month getTypicalMonth() {
    return switch (knownHoliday) {
      case THANKSGIVING_US -> java.time.Month.NOVEMBER;
      case MEMORIAL_DAY_US, MOTHERS_DAY -> java.time.Month.MAY;
      case LABOR_DAY_US -> java.time.Month.SEPTEMBER;
      case FATHERS_DAY -> java.time.Month.JUNE;
      default -> null;
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
}
