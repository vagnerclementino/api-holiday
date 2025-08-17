package me.clementino.holiday.domain.dop;

import java.time.LocalDate;
import java.time.Month;
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
    String name,
    String description,
    LocalDate date,
    int day,
    Month month,
    List<Locality> localities,
    HolidayType type)
    implements Holiday {

  /**
   * Compact constructor with validation to ensure data integrity. This prevents the creation of
   * invalid holiday instances and validates day/month combinations.
   */
  public FixedHoliday {
    Objects.requireNonNull(name, "Holiday name cannot be null");
    Objects.requireNonNull(description, "Holiday description cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(month, "Holiday month cannot be null");
    Objects.requireNonNull(localities, "Holiday localities cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");

    if (name.isBlank()) {
      throw new IllegalArgumentException("Holiday name cannot be blank");
    }
    if (localities.isEmpty()) {
      throw new IllegalArgumentException("Holiday must have at least one locality");
    }
    if (day < 1 || day > 31) {
      throw new IllegalArgumentException("Day must be between 1 and 31");
    }

    // Validate day/month combination
    validateDayMonthCombination(day, month);

    // Ensure date matches day/month
    if (date.getDayOfMonth() != day || date.getMonth() != month) {
      throw new IllegalArgumentException("Date must match day and month fields");
    }

    // Ensure localities list is immutable
    localities = List.copyOf(localities);
  }

  /**
   * Validates that the day/month combination is valid. Prevents illegal states like February 31st.
   */
  private static void validateDayMonthCombination(int day, Month month) {
    // Check if the day is valid for the given month
    // Use a leap year (2024) to allow February 29th
    int maxDaysInMonth = month.length(true); // true = leap year

    if (day > maxDaysInMonth) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid day %d for month %s. Maximum days in %s: %d",
              day, month, month, maxDaysInMonth));
    }
  }

  // ===== TRANSFORMATION METHODS =====
  // These methods return new instances, maintaining immutability

  /**
   * Creates a new FixedHoliday with a different name.
   *
   * @param newName the new name for the holiday
   * @return a new FixedHoliday instance with the updated name
   */
  public FixedHoliday withName(String newName) {
    return new FixedHoliday(newName, description, date, day, month, localities, type);
  }

  /**
   * Creates a new FixedHoliday with a different description.
   *
   * @param newDescription the new description for the holiday
   * @return a new FixedHoliday instance with the updated description
   */
  public FixedHoliday withDescription(String newDescription) {
    return new FixedHoliday(name, newDescription, date, day, month, localities, type);
  }

  /**
   * Creates a new FixedHoliday with a different date (and matching day/month).
   *
   * @param newDate the new date for the holiday
   * @return a new FixedHoliday instance with the updated date
   */
  public FixedHoliday withDate(LocalDate newDate) {
    return new FixedHoliday(
        name, description, newDate, newDate.getDayOfMonth(), newDate.getMonth(), localities, type);
  }

  /**
   * Creates a new FixedHoliday with different day and month (and matching date).
   *
   * @param newDay the new day
   * @param newMonth the new month
   * @return a new FixedHoliday instance with the updated day/month
   */
  public FixedHoliday withDayAndMonth(int newDay, Month newMonth) {
    LocalDate newDate = LocalDate.of(date.getYear(), newMonth, newDay);
    return new FixedHoliday(name, description, newDate, newDay, newMonth, localities, type);
  }

  /**
   * Creates a new FixedHoliday with different localities.
   *
   * @param newLocalities the new localities for the holiday
   * @return a new FixedHoliday instance with the updated localities
   */
  public FixedHoliday withLocalities(List<Locality> newLocalities) {
    return new FixedHoliday(name, description, date, day, month, newLocalities, type);
  }

  /**
   * Creates a new FixedHoliday with a different type.
   *
   * @param newType the new type for the holiday
   * @return a new FixedHoliday instance with the updated type
   */
  public FixedHoliday withType(HolidayType newType) {
    return new FixedHoliday(name, description, date, day, month, localities, newType);
  }

  /**
   * Check if this holiday is recurring (occurs every year on the same day/month). For FixedHoliday,
   * this is always true.
   *
   * @return true (fixed holidays are always recurring)
   */
  public boolean isRecurring() {
    return true;
  }

  /**
   * Get the holiday for a specific year.
   *
   * @param year the target year
   * @return a new FixedHoliday instance for the specified year
   */
  public FixedHoliday forYear(int year) {
    LocalDate newDate = LocalDate.of(year, month, day);
    return new FixedHoliday(name, description, newDate, day, month, localities, type);
  }
}
