package me.clementino.holiday.domain.dop;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import me.clementino.holiday.domain.HolidayType;

/**
 * Fixed holiday that occurs on the same date every year.
 *
 * <p>This record implements the Holiday interface, automatically inheriting all common methods
 * without repetition. Only the core data fields are defined here - all common functionality comes
 * from the interface.
 *
 * <p><strong>Examples:</strong>
 *
 * <ul>
 *   <li>Christmas Day (December 25)
 *   <li>New Year's Day (January 1)
 *   <li>Independence Day (July 4 in US, September 7 in Brazil)
 * </ul>
 *
 * <p><strong>DOP Principles:</strong>
 *
 * <ul>
 *   <li><strong>Immutable</strong>: All fields are final, transformation methods return new
 *       instances
 *   <li><strong>Transparent</strong>: All data is directly accessible
 *   <li><strong>Complete</strong>: Contains exactly the data needed for a fixed holiday
 *   <li><strong>Valid</strong>: Constructor validation prevents illegal states
 * </ul>
 */
public record FixedHoliday(
    String name, String description, LocalDate date, List<Locality> localities, HolidayType type)
    implements Holiday {

  /**
   * Compact constructor with validation to ensure data integrity. This prevents the creation of
   * invalid holiday instances.
   */
  public FixedHoliday {
    Objects.requireNonNull(name, "Holiday name cannot be null");
    Objects.requireNonNull(description, "Holiday description cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(localities, "Holiday localities cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");

    if (name.isBlank()) {
      throw new IllegalArgumentException("Holiday name cannot be blank");
    }
    if (localities.isEmpty()) {
      throw new IllegalArgumentException("Holiday must have at least one locality");
    }

    // Ensure localities list is immutable
    localities = List.copyOf(localities);
  }

  // ===== TRANSFORMATION METHODS =====
  // These methods return new instances, maintaining immutability

  /**
   * Returns a new FixedHoliday with the specified name.
   *
   * @param newName the new name for the holiday
   * @return new FixedHoliday instance with updated name
   */
  public FixedHoliday withName(String newName) {
    return new FixedHoliday(newName, description, date, localities, type);
  }

  /**
   * Returns a new FixedHoliday with the specified description.
   *
   * @param newDescription the new description for the holiday
   * @return new FixedHoliday instance with updated description
   */
  public FixedHoliday withDescription(String newDescription) {
    return new FixedHoliday(name, newDescription, date, localities, type);
  }

  /**
   * Returns a new FixedHoliday with the specified date. This is commonly used when calculating
   * holidays for different years.
   *
   * @param newDate the new date for the holiday
   * @return new FixedHoliday instance with updated date
   */
  public FixedHoliday withDate(LocalDate newDate) {
    return new FixedHoliday(name, description, newDate, localities, type);
  }

  /**
   * Returns a new FixedHoliday with the specified localities.
   *
   * @param newLocalities the new localities for the holiday
   * @return new FixedHoliday instance with updated localities
   */
  public FixedHoliday withLocalities(List<Locality> newLocalities) {
    return new FixedHoliday(name, description, date, newLocalities, type);
  }

  /**
   * Returns a new FixedHoliday with an additional locality.
   *
   * @param additionalLocality the locality to add
   * @return new FixedHoliday instance with the additional locality
   */
  public FixedHoliday withAdditionalLocality(Locality additionalLocality) {
    var newLocalities = new java.util.ArrayList<>(localities);
    newLocalities.add(additionalLocality);
    return new FixedHoliday(name, description, date, newLocalities, type);
  }

  /**
   * Returns a new FixedHoliday with the specified type.
   *
   * @param newType the new type for the holiday
   * @return new FixedHoliday instance with updated type
   */
  public FixedHoliday withType(HolidayType newType) {
    return new FixedHoliday(name, description, date, localities, newType);
  }

  /**
   * Returns a new FixedHoliday with the date adjusted to the specified year. This maintains the
   * same month and day but changes the year.
   *
   * @param year the target year
   * @return new FixedHoliday instance with date in the specified year
   */
  public FixedHoliday forYear(int year) {
    if (year <= 0) {
      throw new IllegalArgumentException("Year must be positive, got: " + year);
    }
    return withDate(date.withYear(year));
  }

  // ===== CONVENIENCE METHODS =====

  /**
   * Checks if this holiday occurs in the specified month.
   *
   * @param month the month to check
   * @return true if this holiday occurs in the specified month
   */
  public boolean occursInMonth(java.time.Month month) {
    return date.getMonth() == month;
  }

  /**
   * Gets the day of the month when this holiday occurs.
   *
   * @return day of the month (1-31)
   */
  public int getDayOfMonth() {
    return date.getDayOfMonth();
  }

  /**
   * Gets the month when this holiday occurs.
   *
   * @return the month
   */
  public java.time.Month getMonth() {
    return date.getMonth();
  }

  /**
   * Gets the year of this holiday instance.
   *
   * @return the year
   */
  public int getYear() {
    return date.getYear();
  }

  /**
   * Checks if this holiday occurs on the same day and month as another date. This ignores the year
   * component.
   *
   * @param otherDate the date to compare with
   * @return true if same day and month
   */
  public boolean isSameDayAndMonth(LocalDate otherDate) {
    return date.getMonth() == otherDate.getMonth()
        && date.getDayOfMonth() == otherDate.getDayOfMonth();
  }

  // ===== INHERITED METHODS FROM HOLIDAY INTERFACE =====
  // The following methods are automatically available from the Holiday interface:
  // - name(), description(), date(), localities(), type() (record accessors)
  // - isWeekend(), getDisplayName(), appliesTo(), getSummary(), isGovernmental(),
  // isObservedInCountry() (default methods)
}
