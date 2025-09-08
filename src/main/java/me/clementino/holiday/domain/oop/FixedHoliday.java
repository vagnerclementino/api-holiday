package me.clementino.holiday.domain.oop;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import me.clementino.holiday.domain.dop.HolidayType;

/**
 * Represents a fixed holiday that occurs on the same date every year.
 *
 * <p>Fixed holidays are the most common type of holidays, occurring on a specific day and month
 * annually. Examples include New Year's Day (January 1st), Christmas Day (December 25th), and
 * Independence Day (July 4th in the US).
 *
 * <p><strong>Key Characteristics:</strong>
 *
 * <ul>
 *   <li><strong>Predictable:</strong> Always falls on the same calendar date
 *   <li><strong>Simple calculation:</strong> No complex algorithms needed
 *   <li><strong>Mondayisation support:</strong> Can be moved to weekdays when falling on weekends
 *   <li><strong>Multi-year consistency:</strong> Same date across all years
 * </ul>
 *
 * <p><strong>Mondayisation Example:</strong>
 *
 * <pre>{@code
 * FixedHoliday christmas = new FixedHoliday("Christmas Day", "Birth of Jesus Christ",
 *                                          25, Month.DECEMBER, localities,
 *                                          HolidayType.NATIONAL, true);
 *
 * LocalDate actual = christmas.getDate(2024);
 * LocalDate observed = christmas.getObserved(2024);
 *
 *
 * }</pre>
 *
 * <p><strong>Common Fixed Holidays:</strong>
 *
 * <ul>
 *   <li>New Year's Day (January 1)
 *   <li>Christmas Day (December 25)
 *   <li>Independence Day (varies by country)
 *   <li>Valentine's Day (February 14)
 *   <li>Halloween (October 31)
 * </ul>
 *
 * <p>This class follows Data-Oriented Programming principles by treating holidays as immutable data
 * structures with clear separation between data representation and behavioral operations.
 *
 * @author Vagner Clementino
 * @since 1.0
 * @see Holiday
 * @see MoveableHoliday
 * @see HolidayType
 */
public class FixedHoliday extends Holiday {

  /**
   * Creates a new FixedHoliday with complete specification of all properties.
   *
   * <p>This constructor is typically used when you have pre-calculated all the temporal properties
   * of the holiday for a specific year.
   *
   * @param name the name of the holiday (must not be null or blank)
   * @param description a detailed description of the holiday (can be null)
   * @param day the day of the month (1-31, must be valid for the specified month)
   * @param month the month of the year (must not be null)
   * @param date the exact date of the holiday (must not be null)
   * @param observed the observed date after applying mondayisation rules (must not be null)
   * @param dateWeekDay the day of the week for the actual date (must not be null)
   * @param observedWeekDay the day of the week for the observed date (must not be null)
   * @param localities the list of localities where this holiday is observed (must not be null)
   * @param type the category/type of the holiday (must not be null)
   * @param mondayisation whether weekend adjustment rules should be applied
   * @throws IllegalArgumentException if day is invalid for the specified month
   * @throws IllegalArgumentException if any required parameter is null
   */
  public FixedHoliday(
      String name,
      String description,
      int day,
      Month month,
      LocalDate date,
      LocalDate observed,
      DayOfWeek dateWeekDay,
      DayOfWeek observedWeekDay,
      List<Locality> localities,
      HolidayType type,
      boolean mondayisation) {
    super(
        name,
        description,
        day,
        month,
        date,
        observed,
        dateWeekDay,
        observedWeekDay,
        localities,
        type,
        mondayisation);
    validateDayForMonth(day, month);
  }

  /**
   * Creates a new FixedHoliday with basic properties.
   *
   * <p>This is the most commonly used constructor for creating fixed holidays. The temporal
   * properties (date, observed date, weekdays) will be calculated dynamically when needed.
   *
   * @param name the name of the holiday (must not be null or blank)
   * @param description a detailed description of the holiday (can be null)
   * @param day the day of the month (1-31, must be valid for the specified month)
   * @param month the month of the year (must not be null)
   * @param localities the list of localities where this holiday is observed (must not be null)
   * @param type the category/type of the holiday (must not be null)
   * @param mondayisation whether weekend adjustment rules should be applied
   * @throws IllegalArgumentException if day is invalid for the specified month
   * @throws IllegalArgumentException if any required parameter is null
   * @example
   *     <pre>{@code
   * List<Locality> usLocalities = List.of(new Locality("US"));
   * FixedHoliday independenceDay = new FixedHoliday(
   *     "Independence Day",
   *     "Celebrates the Declaration of Independence",
   *     4, Month.JULY,
   *     usLocalities,
   *     HolidayType.NATIONAL,
   *     true
   * );
   * }</pre>
   */
  public FixedHoliday(
      String name,
      String description,
      int day,
      Month month,
      List<Locality> localities,
      HolidayType type,
      boolean mondayisation) {
    super(name, description, day, month, localities, type, mondayisation);
    validateDayForMonth(day, month);
  }

  /**
   * Returns the date of this fixed holiday for the specified year.
   *
   * <p>Since this is a fixed holiday, it always returns the same day and month combination for any
   * valid year.
   *
   * @param year the year for which to get the holiday date (must be positive)
   * @return the date of the holiday in the specified year
   * @throws IllegalArgumentException if year is not positive
   * @throws IllegalArgumentException if the day/month combination is invalid for the year (e.g.,
   *     February 29 in a non-leap year)
   * @example
   *     <pre>{@code
   * FixedHoliday christmas = new FixedHoliday("Christmas", "Birth of Christ",
   *                                          25, Month.DECEMBER, localities,
   *                                          HolidayType.RELIGIOUS, true);
   *
   * LocalDate christmas2024 = christmas.getDate(2024);
   * LocalDate christmas2025 = christmas.getDate(2025);
   * }</pre>
   */
  @Override
  public LocalDate getDate(int year) {
    validateYear(year);

    if (getDate() != null && getDate().getYear() == year) {
      return getDate();
    }

    try {
      LocalDate calculatedDate = LocalDate.of(year, getMonth(), getDay());
      setDate(calculatedDate);
      setDateWeekDay(calculatedDate.getDayOfWeek());
      return calculatedDate;
    } catch (Exception e) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid date: %s %d for year %d. %s", getMonth(), getDay(), year, e.getMessage()),
          e);
    }
  }

  /**
   * /** Returns a detailed string representation of this fixed holiday.
   *
   * @return a formatted string containing the holiday's key information
   */
  @Override
  public String toString() {
    return String.format(
        "FixedHoliday{name='%s', date='%s %d', type=%s, mondayisation=%s}",
        getName(), getMonth(), getDay(), getType(), isMondayisation());
  }

  /**
   * Validates that the specified day is valid for the given month.
   *
   * <p>This method performs basic validation to ensure the day is within the valid range for the
   * specified month. It handles months with different numbers of days but does not account for leap
   * years (February 29 validation is deferred to LocalDate creation).
   *
   * @param day the day to validate (1-31)
   * @param month the month context for validation
   * @throws IllegalArgumentException if the day is invalid for the month
   */
  private void validateDayForMonth(int day, Month month) {
    if (day < 1 || day > month.maxLength()) {
      throw new IllegalArgumentException(
          String.format(
              "Invalid day %d for month %s. Valid range: 1-%d", day, month, month.maxLength()));
    }
  }

  /**
   * Validates that the specified year is positive.
   *
   * @param year the year to validate
   * @throws IllegalArgumentException if year is not positive
   */
  private void validateYear(int year) {
    if (year <= 0) {
      throw new IllegalArgumentException("Year must be positive, got: " + year);
    }
  }
}
