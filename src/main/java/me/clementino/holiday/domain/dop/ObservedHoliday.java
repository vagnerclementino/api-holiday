package me.clementino.holiday.domain.dop;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import me.clementino.holiday.domain.HolidayType;

/**
 * Observed holiday that may have a different observed date due to mondayisation rules.
 *
 * <p>This record implements the Holiday interface, automatically inheriting all common methods
 * without repetition. It adds observed-specific attributes: observed date and mondayisation flag.
 *
 * <p><strong>Mondayisation Rules:</strong>
 *
 * <ul>
 *   <li>If a holiday falls on Saturday, it may be observed on Friday
 *   <li>If a holiday falls on Sunday, it may be observed on Monday
 *   <li>Weekday holidays are observed on their actual date
 * </ul>
 *
 * <p><strong>Examples:</strong>
 *
 * <ul>
 *   <li>Christmas Day with mondayisation (observed on Monday if falls on Sunday)
 *   <li>New Year's Day with mondayisation (observed on Friday if falls on Saturday)
 *   <li>Bank holidays that shift to avoid weekends
 * </ul>
 *
 * <p><strong>DOP Principles:</strong>
 *
 * <ul>
 *   <li><strong>Immutable</strong>: All fields are final, transformation methods return new
 *       instances
 *   <li><strong>Transparent</strong>: All data is directly accessible
 *   <li><strong>Complete</strong>: Contains exactly the data needed for an observed holiday
 *   <li><strong>Valid</strong>: Constructor validation prevents illegal states
 * </ul>
 */
public record ObservedHoliday(
    String name,
    String description,
    LocalDate date,
    List<Locality> localities,
    HolidayType type,
    // Additional attributes specific to ObservedHoliday
    LocalDate observed,
    boolean mondayisation)
    implements Holiday {

  /**
   * Compact constructor with validation to ensure data integrity. This prevents the creation of
   * invalid observed holiday instances.
   */
  public ObservedHoliday {
    Objects.requireNonNull(name, "Holiday name cannot be null");
    Objects.requireNonNull(description, "Holiday description cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(observed, "Observed date cannot be null");
    Objects.requireNonNull(localities, "Holiday localities cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");

    if (name.isBlank()) {
      throw new IllegalArgumentException("Holiday name cannot be blank");
    }
    if (localities.isEmpty()) {
      throw new IllegalArgumentException("Holiday must have at least one locality");
    }

    // Validate mondayisation logic
    if (mondayisation && date.equals(observed)) {
      DayOfWeek dayOfWeek = date.getDayOfWeek();
      if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
        throw new IllegalArgumentException(
            "Mondayisation is enabled but observed date equals original weekend date. "
                + "Expected observed date to be adjusted for weekend.");
      }
    }

    // Ensure localities list is immutable
    localities = List.copyOf(localities);
  }

  // ===== TRANSFORMATION METHODS =====
  // These methods return new instances, maintaining immutability

  /**
   * Returns a new ObservedHoliday with the specified name.
   *
   * @param newName the new name for the holiday
   * @return new ObservedHoliday instance with updated name
   */
  public ObservedHoliday withName(String newName) {
    return new ObservedHoliday(
        newName, description, date, localities, type, observed, mondayisation);
  }

  /**
   * Returns a new ObservedHoliday with the specified description.
   *
   * @param newDescription the new description for the holiday
   * @return new ObservedHoliday instance with updated description
   */
  public ObservedHoliday withDescription(String newDescription) {
    return new ObservedHoliday(
        name, newDescription, date, localities, type, observed, mondayisation);
  }

  /**
   * Returns a new ObservedHoliday with the specified date. This is commonly used when calculating
   * holidays for different years.
   *
   * @param newDate the new date for the holiday
   * @return new ObservedHoliday instance with updated date
   */
  public ObservedHoliday withDate(LocalDate newDate) {
    return new ObservedHoliday(
        name, description, newDate, localities, type, observed, mondayisation);
  }

  /**
   * Returns a new ObservedHoliday with the specified observed date.
   *
   * @param newObserved the new observed date for the holiday
   * @return new ObservedHoliday instance with updated observed date
   */
  public ObservedHoliday withObserved(LocalDate newObserved) {
    return new ObservedHoliday(
        name, description, date, localities, type, newObserved, mondayisation);
  }

  /**
   * Returns a new ObservedHoliday with both date and observed date updated. This is useful when
   * recalculating for a different year.
   *
   * @param newDate the new actual date
   * @param newObserved the new observed date
   * @return new ObservedHoliday instance with updated dates
   */
  public ObservedHoliday withDates(LocalDate newDate, LocalDate newObserved) {
    return new ObservedHoliday(
        name, description, newDate, localities, type, newObserved, mondayisation);
  }

  /**
   * Returns a new ObservedHoliday with the specified localities.
   *
   * @param newLocalities the new localities for the holiday
   * @return new ObservedHoliday instance with updated localities
   */
  public ObservedHoliday withLocalities(List<Locality> newLocalities) {
    return new ObservedHoliday(
        name, description, date, newLocalities, type, observed, mondayisation);
  }

  /**
   * Returns a new ObservedHoliday with the specified type.
   *
   * @param newType the new type for the holiday
   * @return new ObservedHoliday instance with updated type
   */
  public ObservedHoliday withType(HolidayType newType) {
    return new ObservedHoliday(
        name, description, date, localities, newType, observed, mondayisation);
  }

  /**
   * Returns a new ObservedHoliday with the specified mondayisation setting.
   *
   * @param newMondayisation the new mondayisation setting
   * @return new ObservedHoliday instance with updated mondayisation
   */
  public ObservedHoliday withMondayisation(boolean newMondayisation) {
    return new ObservedHoliday(
        name, description, date, localities, type, observed, newMondayisation);
  }

  /**
   * Returns a new ObservedHoliday with the date adjusted to the specified year. This maintains the
   * same month and day but changes the year for both dates.
   *
   * @param year the target year
   * @return new ObservedHoliday instance with dates in the specified year
   */
  public ObservedHoliday forYear(int year) {
    if (year <= 0) {
      throw new IllegalArgumentException("Year must be positive, got: " + year);
    }
    LocalDate newDate = date.withYear(year);
    LocalDate newObserved = observed.withYear(year);
    return withDates(newDate, newObserved);
  }

  // ===== OBSERVED-SPECIFIC METHODS =====

  /**
   * Checks if the observed date is different from the actual date.
   *
   * @return true if the holiday is observed on a different date than it actually occurs
   */
  public boolean isDateShifted() {
    return !date.equals(observed);
  }

  /**
   * Gets the number of days between the actual date and observed date. Positive values mean
   * observed date is after actual date. Negative values mean observed date is before actual date.
   *
   * @return number of days difference
   */
  public long getDaysDifference() {
    return java.time.temporal.ChronoUnit.DAYS.between(date, observed);
  }

  /**
   * Checks if the actual date falls on a weekend.
   *
   * @return true if the actual date is Saturday or Sunday
   */
  public boolean actualDateIsWeekend() {
    DayOfWeek dayOfWeek = date.getDayOfWeek();
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
  }

  /**
   * Checks if the observed date falls on a weekend.
   *
   * @return true if the observed date is Saturday or Sunday
   */
  public boolean observedDateIsWeekend() {
    DayOfWeek dayOfWeek = observed.getDayOfWeek();
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
  }

  /**
   * Gets a formatted string showing both actual and observed dates.
   *
   * @return formatted string like "December 25 (observed December 26)"
   */
  public String getDateInfo() {
    if (date.equals(observed)) {
      return date.getMonth() + " " + date.getDayOfMonth();
    } else {
      return date.getMonth()
          + " "
          + date.getDayOfMonth()
          + " (observed "
          + observed.getMonth()
          + " "
          + observed.getDayOfMonth()
          + ")";
    }
  }

  /**
   * Applies mondayisation rules to a given date.
   *
   * @param dateToAdjust the date to potentially adjust
   * @return adjusted date according to mondayisation rules
   */
  public static LocalDate applyMondayisationRules(LocalDate dateToAdjust) {
    return switch (dateToAdjust.getDayOfWeek()) {
      case SATURDAY -> dateToAdjust.minusDays(1); // Saturday -> Friday
      case SUNDAY -> dateToAdjust.plusDays(1); // Sunday -> Monday
      default -> dateToAdjust; // Weekdays remain unchanged
    };
  }

  // ===== INHERITED METHODS FROM HOLIDAY INTERFACE =====
  // The following methods are automatically available from the Holiday interface:
  // - name(), description(), date(), localities(), type() (record accessors)
  // - isWeekend(), getDisplayName(), appliesTo(), getSummary(), isGovernmental(),
  // isObservedInCountry() (default methods)

  // Note: isWeekend() from the interface checks the actual date, not the observed date
  // Use observedDateIsWeekend() if you need to check the observed date specifically
}
