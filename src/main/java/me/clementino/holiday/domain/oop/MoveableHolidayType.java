package me.clementino.holiday.domain.oop;

/**
 * Enum representing different types of moveable holidays based on their calculation method.
 *
 * <p>This enum defines the various algorithmic approaches used to calculate moveable holidays,
 * which are holidays that do not fall on the same calendar date each year. Each type represents a
 * different mathematical or astronomical basis for determining the holiday's date.
 *
 * <p><strong>Calculation Methods:</strong>
 *
 * <ul>
 *   <li><strong>Lunar-based:</strong> Uses lunar calendar calculations (astronomical)
 *   <li><strong>Relative:</strong> Calculated as an offset from another holiday
 *   <li><strong>Weekday-based:</strong> Uses weekday rules (e.g., "first Monday of month")
 * </ul>
 *
 * <p><strong>Real-world Examples:</strong>
 *
 * <pre>{@code
 * MoveableHoliday easter = new MoveableHoliday("Easter Sunday", ..., LUNAR_BASED);
 * MoveableHoliday chineseNewYear = new MoveableHoliday("Chinese New Year", ..., LUNAR_BASED);
 *
 *
 * MoveableHoliday goodFriday = new MoveableHoliday("Good Friday", ..., RELATIVE_TO_HOLIDAY);
 *
 *
 *
 * MoveableHoliday laborDay = new MoveableHoliday("Labor Day", ..., WEEKDAY_BASED);
 *
 * }</pre>
 *
 * <p><strong>Data-Oriented Design:</strong>
 *
 * <p>This enum follows DOP principles by clearly categorizing the different types of calculations
 * without embedding the calculation logic itself. The actual algorithms are implemented in the
 * {@link MoveableHoliday} class, maintaining separation between data classification and operations.
 *
 * @author Vagner Clementino
 * @since 1.0
 * @see MoveableHoliday
 * @see Holiday
 * @see FixedHoliday
 */
public enum MoveableHolidayType {

  /**
   * Holidays calculated based on lunar calendar observations and astronomical events.
   *
   * <p><strong>Characteristics:</strong>
   *
   * <ul>
   *   <li>Based on moon phases and astronomical calculations
   *   <li>Often involves complex mathematical algorithms
   *   <li>May require different calculations for different calendar systems
   *   <li>Typically has religious or cultural significance
   * </ul>
   *
   * <p><strong>Examples:</strong>
   *
   * <ul>
   *   <li><strong>Easter Sunday:</strong> First Sunday after first full moon on/after spring
   *       equinox
   *   <li><strong>Chinese New Year:</strong> Based on lunar calendar
   *   <li><strong>Ramadan:</strong> Based on Islamic lunar calendar
   *   <li><strong>Passover:</strong> Based on Hebrew lunar calendar
   * </ul>
   *
   * <p><strong>Calculation Complexity:</strong> High - requires astronomical algorithms
   */
  LUNAR_BASED("Lunar-based calculation using astronomical events and moon phases"),

  /**
   * Holidays calculated as a fixed offset relative to another base holiday.
   *
   * <p><strong>Characteristics:</strong>
   *
   * <ul>
   *   <li>Depends on the date of another holiday (base holiday)
   *   <li>Simple arithmetic calculation (addition/subtraction of days)
   *   <li>Often part of a holiday sequence or religious observance period
   *   <li>Maintains consistent relationship with the base holiday
   * </ul>
   *
   * <p><strong>Examples:</strong>
   *
   * <ul>
   *   <li><strong>Good Friday:</strong> Easter Sunday - 2 days
   *   <li><strong>Easter Monday:</strong> Easter Sunday + 1 day
   *   <li><strong>Ash Wednesday:</strong> Easter Sunday - 46 days
   *   <li><strong>Palm Sunday:</strong> Easter Sunday - 7 days
   * </ul>
   *
   * <p><strong>Calculation Complexity:</strong> Low - simple date arithmetic
   */
  RELATIVE_TO_HOLIDAY("Calculated as a fixed offset relative to another base holiday"),

  /**
   * Holidays calculated based on specific weekday rules within a month or year.
   *
   * <p><strong>Characteristics:</strong>
   *
   * <ul>
   *   <li>Based on weekday patterns (e.g., "first Monday", "last Friday")
   *   <li>Often used for secular holidays and observances
   *   <li>Provides consistent weekday placement for long weekends
   *   <li>May vary by country or jurisdiction
   * </ul>
   *
   * <p><strong>Common Patterns:</strong>
   *
   * <ul>
   *   <li><strong>First [Weekday] of [Month]:</strong> Labor Day (first Monday of September)
   *   <li><strong>Last [Weekday] of [Month]:</strong> Memorial Day (last Monday of May)
   *   <li><strong>Second [Weekday] of [Month]:</strong> Columbus Day (second Monday of October)
   *   <li><strong>Third [Weekday] of [Month]:</strong> Presidents Day (third Monday of February)
   * </ul>
   *
   * <p><strong>Examples:</strong>
   *
   * <ul>
   *   <li><strong>Labor Day (US):</strong> First Monday of September
   *   <li><strong>Thanksgiving (US):</strong> Fourth Thursday of November
   *   <li><strong>Memorial Day (US):</strong> Last Monday of May
   *   <li><strong>Columbus Day (US):</strong> Second Monday of October
   * </ul>
   *
   * <p><strong>Calculation Complexity:</strong> Medium - requires weekday arithmetic
   */
  WEEKDAY_BASED("Calculated based on specific weekday rules within a month or year");

  private final String description;

  /**
   * Creates a new MoveableHolidayType with the specified description.
   *
   * @param description a detailed description of the calculation method and characteristics
   */
  MoveableHolidayType(String description) {
    this.description = description;
  }

  /**
   * Returns the detailed description of this moveable holiday type.
   *
   * @return a comprehensive description of the calculation method and its characteristics
   */
  public String getDescription() {
    return description;
  }

  /**
   * Checks if this type requires complex astronomical calculations.
   *
   * @return true if this type uses lunar or astronomical calculations, false otherwise
   */
  public boolean isAstronomicallyBased() {
    return this == LUNAR_BASED;
  }

  /**
   * Checks if this type depends on another holiday for its calculation.
   *
   * @return true if this type is calculated relative to another holiday, false otherwise
   */
  public boolean isRelativeBased() {
    return this == RELATIVE_TO_HOLIDAY;
  }

  /**
   * Checks if this type uses weekday-based rules for calculation.
   *
   * @return true if this type uses weekday patterns, false otherwise
   */
  public boolean isWeekdayBased() {
    return this == WEEKDAY_BASED;
  }

  /**
   * Returns the complexity level of calculations for this type.
   *
   * @return "HIGH" for lunar-based, "MEDIUM" for weekday-based, "LOW" for relative-based
   */
  public String getComplexityLevel() {
    return switch (this) {
      case LUNAR_BASED -> "HIGH";
      case WEEKDAY_BASED -> "MEDIUM";
      case RELATIVE_TO_HOLIDAY -> "LOW";
    };
  }

  /**
   * Returns a formatted string representation of this moveable holiday type.
   *
   * @return a string containing the name and description
   */
  @Override
  public String toString() {
    return String.format("%s: %s", name(), description);
  }
}
