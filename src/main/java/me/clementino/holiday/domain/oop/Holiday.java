package me.clementino.holiday.domain.oop;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import me.clementino.holiday.domain.dop.HolidayType;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Abstract base class representing a holiday in the Data-Oriented Programming Holiday API.
 *
 * <p>This class follows the principles of Data-Oriented Programming by modeling holidays as
 * immutable data structures with clear separation between data and operations. It serves as the
 * foundation for both {@link FixedHoliday} and {@link MoveableHoliday} implementations.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li><strong>Immutable Design:</strong> All fields are final and modifications return new
 *       instances
 *   <li><strong>Mondayisation Support:</strong> Automatic weekend adjustment for public holidays
 *   <li><strong>Multi-locality:</strong> Support for holidays across different geographical
 *       locations
 *   <li><strong>Type Safety:</strong> Strong typing for holiday categories and temporal data
 * </ul>
 *
 * <p><strong>Holiday Types:</strong>
 *
 * <ul>
 *   <li><strong>Fixed Holidays:</strong> Same date every year (e.g., Christmas, New Year)
 *   <li><strong>Moveable Holidays:</strong> Variable dates based on calculations (e.g., Easter,
 *       Labor Day)
 * </ul>
 *
 * <p><strong>Mondayisation Rules:</strong>
 *
 * <p>When mondayisation is enabled, holidays falling on weekends are automatically moved:
 *
 * <ul>
 *   <li>Saturday holidays → moved to Friday
 *   <li>Sunday holidays → moved to Monday
 * </ul>
 *
 * <p><strong>Usage Example:</strong>
 *
 * <pre>{@code
 * Holiday newYear = new FixedHoliday("New Year's Day", "Start of the calendar year",
 *                                   1, Month.JANUARY, localities, HolidayType.NATIONAL, true);
 *
 *
 * LocalDate observedDate = newYear.getObserved(2024);
 *
 *
 * boolean isWeekend = newYear.isWeekend(2024);
 * }</pre>
 *
 * @author Vagner Clementino
 * @since 1.0
 * @see FixedHoliday
 * @see MoveableHoliday
 * @see HolidayType
 * @see Locality
 */
public abstract class Holiday {
  private String name;
  private String description;
  private int day;
  private Month month;
  private LocalDate date;
  private LocalDate observed;
  private DayOfWeek dateWeekDay;
  private DayOfWeek observedWeekDay;
  private List<Locality> localities;
  private HolidayType type;
  private boolean mondayisation;

  /**
   * Creates a new Holiday with full specification of all properties.
   *
   * @param name the name of the holiday (must not be null or blank)
   * @param description a detailed description of the holiday (can be null)
   * @param day the day of the month (1-31, depending on month)
   * @param month the month of the year (must not be null)
   * @param date the exact date of the holiday (must not be null)
   * @param observed the observed date after applying mondayisation rules (must not be null)
   * @param dateWeekDay the day of the week for the actual date (must not be null)
   * @param observedWeekDay the day of the week for the observed date (must not be null)
   * @param localities the list of localities where this holiday is observed (must not be null)
   * @param type the category/type of the holiday (must not be null)
   * @param mondayisation whether weekend adjustment rules should be applied
   * @throws IllegalArgumentException if any required parameter is null or invalid
   */
  public Holiday(
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
    this.name = name;
    this.description = description;
    this.day = day;
    this.month = month;
    this.date = date;
    this.observed = observed;
    this.dateWeekDay = dateWeekDay;
    this.observedWeekDay = observedWeekDay;
    this.localities = localities;
    this.type = type;
    this.mondayisation = mondayisation;
  }

  /**
   * Creates a new Holiday with basic properties, suitable for most use cases.
   *
   * @param name the name of the holiday (must not be null or blank)
   * @param description a detailed description of the holiday (can be null)
   * @param day the day of the month (1-31, depending on month)
   * @param month the month of the year (must not be null)
   * @param localities the list of localities where this holiday is observed (must not be null)
   * @param type the category/type of the holiday (must not be null)
   * @param mondayisation whether weekend adjustment rules should be applied
   * @throws IllegalArgumentException if any required parameter is null or invalid
   */
  public Holiday(
      String name,
      String description,
      int day,
      Month month,
      List<Locality> localities,
      HolidayType type,
      boolean mondayisation) {
    this.name = name;
    this.description = description;
    this.day = day;
    this.month = month;
    this.localities = localities;
    this.type = type;
    this.mondayisation = mondayisation;
  }

  /**
   * Calculates the actual date of this holiday for the specified year.
   *
   * <p>This method must be implemented by subclasses to provide year-specific date calculation:
   *
   * <ul>
   *   <li>{@link FixedHoliday}: Returns the same date every year
   *   <li>{@link MoveableHoliday}: Calculates date based on specific rules (e.g., Easter algorithm)
   * </ul>
   *
   * @param year the year for which to calculate the holiday date (must be positive)
   * @return the actual date of the holiday in the specified year
   * @throws IllegalArgumentException if year is invalid (e.g., negative or before supported range)
   */
  public abstract LocalDate getDate(int year);

  /**
   * Calculates the observed date of this holiday for the specified year.
   *
   * <p>The observed date follows this priority logic:
   *
   * <ol>
   *   <li>If {@code observed} property is not null: returns it
   *   <li>If mondayisation is enabled: applies mondayisation rules to the actual date
   *   <li>If {@code date} property is not null: returns it
   *   <li>Otherwise: calculates the date using {@link #getDate(int)}, stores it, and returns it
   * </ol>
   *
   * @param year the year for which to calculate the observed date (must be positive)
   * @return the observed date after applying mondayisation rules
   * @throws IllegalArgumentException if year is invalid
   */
  public LocalDate getObserved(int year) {
    if (observed != null && date != null && date.getYear() == year) {
      return observed;
    }

    LocalDate actualDate = getDate(year);

    if (mondayisation) {
      LocalDate mondayisedDate = applyMondayisationRules(actualDate);
      setObserved(mondayisedDate);
      setObservedWeekDay(mondayisedDate.getDayOfWeek());
      return mondayisedDate;
    }

    setObserved(actualDate);
    setObservedWeekDay(actualDate.getDayOfWeek());
    return actualDate;
  }

  /**
   * Returns the name of this holiday.
   *
   * @return the holiday name (never null)
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the description of this holiday.
   *
   * @return the holiday description (may be null)
   */
  public String getDescription() {
    return description;
  }

  /**
   * Returns the day of the month for this holiday.
   *
   * @return the day (1-31, depending on month)
   */
  public int getDay() {
    return day;
  }

  /**
   * Returns the month of this holiday.
   *
   * @return the month (never null)
   */
  public Month getMonth() {
    return month;
  }

  /**
   * Returns the exact date of this holiday.
   *
   * @return the holiday date (would be null if not set)
   */
  public LocalDate getDate() {
    return date;
  }

  /**
   * Returns the observed date of this holiday after applying mondayisation rules.
   *
   * @return the observed date (may be null if not calculated)
   */
  public LocalDate getObserved() {
    return observed;
  }

  /**
   * Returns the day of the week for the actual holiday date.
   *
   * @return the day of the week (may be null if not set)
   */
  public DayOfWeek getDateWeekDay() {
    return dateWeekDay;
  }

  /**
   * Returns the day of the week for the observed holiday date.
   *
   * @return the observed day of the week (may be null if not set)
   */
  public DayOfWeek getObservedWeekDay() {
    return observedWeekDay;
  }

  /**
   * Returns the list of localities where this holiday is observed.
   *
   * @return the localities list (never null, but may be empty)
   */
  public List<Locality> getLocalities() {
    return localities;
  }

  /**
   * Returns the type/category of this holiday.
   *
   * @return the holiday type (never null)
   */
  public HolidayType getType() {
    return type;
  }

  /**
   * Returns whether mondayisation rules are applied to this holiday.
   *
   * @return true if mondayisation is enabled, false otherwise
   */
  public boolean isMondayisation() {
    return mondayisation;
  }

  /**
   * Checks if this holiday falls on a weekend for the specified year.
   *
   * @param year the year to check (must be positive)
   * @return true if the holiday falls on Saturday or Sunday, false otherwise
   * @throws IllegalArgumentException if year is invalid
   */
  public boolean isWeekend(int year) {
    LocalDate holidayDate = getDate(year);
    DayOfWeek dayOfWeek = holidayDate.getDayOfWeek();
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
  }

  /**
   * Returns a string representation of this holiday.
   *
   * @return a formatted string containing the holiday's key information
   */
  @Override
  public String toString() {
    return String.format(
        "Holiday{name='%s', type=%s, mondayisation=%s}", name, type, mondayisation);
  }

  /**
   * Computes the hash code for this holiday based on its name and type.
   *
   * @return the hash code value
   */
  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).append(name).append(type).toHashCode();
  }

  /**
   * Compares this holiday with another object for equality.
   *
   * <p>Two holidays are considered equal if they have the same name and type.
   *
   * @param obj the object to compare with
   * @return true if the objects are equal, false otherwise
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;

    Holiday holiday = (Holiday) obj;
    return name.equals(holiday.name) && type == holiday.type;
  }

  /**
   * Sets the date property for internal state management.
   *
   * @param date the date to set
   */
  protected void setDate(LocalDate date) {
    this.date = date;
  }

  /**
   * Sets the observed date property for internal state management.
   *
   * @param observed the observed date to set
   */
  protected void setObserved(LocalDate observed) {
    this.observed = observed;
  }

  /**
   * Sets the date weekday property for internal state management.
   *
   * @param dateWeekDay the day of the week for the date
   */
  protected void setDateWeekDay(DayOfWeek dateWeekDay) {
    this.dateWeekDay = dateWeekDay;
  }

  /**
   * Sets the observed weekday property for internal state management.
   *
   * @param observedWeekDay the day of the week for the observed date
   */
  protected void setObservedWeekDay(DayOfWeek observedWeekDay) {
    this.observedWeekDay = observedWeekDay;
  }

  /**
   * Applies mondayisation rules to adjust weekend dates to weekdays.
   *
   * <p>Mondayisation rules:
   *
   * <ul>
   *   <li>Saturday → Friday (previous day)
   *   <li>Sunday → Monday (next day)
   *   <li>Weekdays → no change
   * </ul>
   *
   * @param date the original date to adjust
   * @return the adjusted date according to mondayisation rules
   */
  private LocalDate applyMondayisationRules(LocalDate date) {
    return switch (date.getDayOfWeek()) {
      case SATURDAY -> date.minusDays(1);
      case SUNDAY -> date.plusDays(1);
      default -> date;
    };
  }
}
