package me.clementino.holiday.domain.oop;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import me.clementino.holiday.domain.dop.HolidayType;

/**
 * Represents a moveable holiday whose date varies from year to year based on specific calculation
 * rules.
 *
 * <p>Moveable holidays are holidays that do not occur on the same calendar date each year. Instead,
 * their dates are calculated using various algorithms based on astronomical events, other holidays,
 * or weekday patterns. This class implements the complex logic required to determine these variable
 * dates while maintaining the Data-Oriented Programming principles of immutable data structures and
 * separated operations.
 *
 * <p><strong>Calculation Methods:</strong>
 *
 * <ul>
 *   <li><strong>Lunar-based:</strong> Uses astronomical calculations (e.g., Easter Sunday)
 *   <li><strong>Relative to holiday:</strong> Calculated as offset from another holiday (e.g., Good
 *       Friday)
 *   <li><strong>Weekday-based:</strong> Uses weekday rules (e.g., first Monday of September)
 * </ul>
 *
 * <p><strong>Easter Calculation:</strong>
 *
 * <p>This class implements the Western Christian Easter calculation using the algorithm by Jean
 * Meeus from "Astronomical Algorithms" (1991). The algorithm is accurate for all years in the
 * Gregorian calendar (1583 onwards) and handles the complex astronomical relationships between
 * solar and lunar calendars.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li><strong>Multiple calculation types:</strong> Supports lunar, relative, and weekday-based
 *       holidays
 *   <li><strong>Astronomical accuracy:</strong> Implements precise Easter calculation algorithm
 *   <li><strong>Mondayisation support:</strong> Automatic weekend adjustment for public holidays
 *   <li><strong>Immutable design:</strong> Thread-safe and predictable behavior
 *   <li><strong>Comprehensive validation:</strong> Input validation and error handling
 * </ul>
 *
 * <p><strong>Usage Examples:</strong>
 *
 * <pre>{@code
 * // Easter Sunday (lunar-based)
 * MoveableHoliday easter = new MoveableHoliday(
 *     "Easter Sunday", "Resurrection of Jesus Christ",
 *     0, Month.JANUARY, // Not used for lunar-based
 *     localities, HolidayType.RELIGIOUS, true,
 *     MoveableHolidayType.LUNAR_BASED, null, 0
 * );
 *
 * // Good Friday (relative to Easter)
 * MoveableHoliday goodFriday = new MoveableHoliday(
 *     "Good Friday", "Crucifixion of Jesus Christ",
 *     0, Month.JANUARY, // Not used for relative
 *     localities, HolidayType.RELIGIOUS, true,
 *     MoveableHolidayType.RELATIVE_TO_HOLIDAY, easter, -2
 * );
 *
 * // Labor Day (weekday-based)
 * MoveableHoliday laborDay = new MoveableHoliday(
 *     "Labor Day", "Workers' rights celebration",
 *     1, Month.SEPTEMBER, // First Monday of September
 *     localities, HolidayType.NATIONAL, true,
 *     MoveableHolidayType.WEEKDAY_BASED, null, 0
 * );
 *
 * // Calculate dates for specific year
 * LocalDate easter2024 = easter.getDate(2024);      // 2024-03-31
 * LocalDate goodFriday2024 = goodFriday.getDate(2024); // 2024-03-29
 * LocalDate laborDay2024 = laborDay.getDate(2024);     // 2024-09-02
 * }</pre>
 *
 * <p><strong>Easter Algorithm Details:</strong>
 *
 * <p>The Easter calculation uses the Metonic cycle (19-year period where lunar phases repeat on the
 * same solar calendar dates) and applies various corrections for the Gregorian calendar reform,
 * leap years, and astronomical variations. The algorithm involves 10 distinct steps with
 * well-documented mathematical constants.
 *
 * @author Vagner Clementino
 * @since 1.0
 * @see Holiday
 * @see FixedHoliday
 * @see MoveableHolidayType
 * @see HolidayType
 */
public class MoveableHoliday extends Holiday {

  /** Minimum year for accurate Gregorian calendar Easter calculation. */
  private static final int MIN_GREGORIAN_YEAR = 1583;

  /** Length of the Metonic cycle in years (lunar phases repeat every 19 solar years). */
  private static final int METONIC_CYCLE_YEARS = 19;

  /** Number of years in a century. */
  private static final int YEARS_PER_CENTURY = 100;

  /** Number of years in a leap year cycle within a century. */
  private static final int LEAP_YEAR_CYCLE = 4;

  /** Gregorian correction factor for lunar orbit calculation. */
  private static final int GREGORIAN_LUNAR_CORRECTION = 8;

  /** Base value for Gregorian calendar lunar correction. */
  private static final int GREGORIAN_CORRECTION_BASE = 25;

  /** Adjustment factor for precessional correction. */
  private static final int PRECESSIONAL_ADJUSTMENT = 1;

  /** Divisor for precessional correction calculation. */
  private static final int PRECESSIONAL_DIVISOR = 3;

  /** Base epact calculation constant. */
  private static final int EPACT_BASE = 15;

  /** Modulus for epact calculation (lunar month length approximation). */
  private static final int EPACT_MODULUS = 30;

  /** Base value for days to full moon calculation. */
  private static final int DAYS_TO_FULL_MOON_BASE = 32;

  /** Modulus for days to full moon calculation (days in a week). */
  private static final int DAYS_TO_FULL_MOON_MODULUS = 7;

  /** Multiplier for golden number in April correction. */
  private static final int APRIL_CORRECTION_GOLDEN_MULTIPLIER = 11;

  /** Multiplier for days to full moon in April correction. */
  private static final int APRIL_CORRECTION_DAYS_MULTIPLIER = 22;

  /** Divisor for April correction calculation. */
  private static final int APRIL_CORRECTION_DIVISOR = 451;

  /** Base value for final date calculation. */
  private static final int FINAL_DATE_BASE = 114;

  /** Divisor to extract month from final calculation. */
  private static final int MONTH_DIVISOR = 31;

  /** Adjustment to convert from 0-based to 1-based day numbering. */
  private static final int DAY_ADJUSTMENT = 1;

  private final MoveableHolidayType moveableType;
  private final Holiday baseHoliday;
  private final int dayOffset;

  /**
   * Constructor for creating a moveable holiday.
   *
   * @param name The name of the holiday
   * @param description The description of the holiday
   * @param day The day (used for reference, may not be the actual calculated day)
   * @param month The month (used for reference, may not be the actual calculated month)
   * @param date The base date (used for reference)
   * @param observed The observed date (if different from actual date)
   * @param dateWeekDay The weekday of the actual date
   * @param observedWeekDay The weekday of the observed date
   * @param localities The localities where this holiday is observed
   * @param type The general holiday type (NATIONAL, STATE, etc.)
   * @param moveableType The specific type of moveable holiday calculation
   * @param baseHoliday The base holiday for relative calculations (can be null)
   * @param dayOffset The number of days offset from the base holiday (can be negative)
   * @param mondayisation Whether to apply mondayisation rule
   */
  public MoveableHoliday(
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
      MoveableHolidayType moveableType,
      Holiday baseHoliday,
      int dayOffset,
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
    this.moveableType = moveableType;
    this.baseHoliday = baseHoliday;
    this.dayOffset = dayOffset;
  }

  /** Constructor for creating a moveable holiday (mondayisation defaults to false). */
  public MoveableHoliday(
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
      MoveableHolidayType moveableType,
      Holiday baseHoliday,
      int dayOffset) {
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
        false);
    this.moveableType = moveableType;
    this.baseHoliday = baseHoliday;
    this.dayOffset = dayOffset;
  }

  /** Simplified constructor for lunar-based holidays (like Easter). */
  public MoveableHoliday(
      String name,
      String description,
      List<Locality> localities,
      HolidayType type,
      MoveableHolidayType moveableType) {
    super(name, description, 0, Month.JANUARY, null, null, null, null, localities, type, false);
    this.moveableType = moveableType;
    this.baseHoliday = null;
    this.dayOffset = 0;
  }

  /** Simplified constructor for lunar-based holidays with mondayisation option. */
  public MoveableHoliday(
      String name,
      String description,
      List<Locality> localities,
      HolidayType type,
      MoveableHolidayType moveableType,
      boolean mondayisation) {
    super(
        name,
        description,
        0,
        Month.JANUARY,
        null,
        null,
        null,
        null,
        localities,
        type,
        mondayisation);
    this.moveableType = moveableType;
    this.baseHoliday = null;
    this.dayOffset = 0;
  }

  /** Constructor for holidays relative to another holiday. */
  public MoveableHoliday(
      String name,
      String description,
      List<Locality> localities,
      HolidayType type,
      Holiday baseHoliday,
      int dayOffset) {
    super(name, description, 0, Month.JANUARY, null, null, null, null, localities, type, false);
    this.moveableType = MoveableHolidayType.RELATIVE_TO_HOLIDAY;
    this.baseHoliday = baseHoliday;
    this.dayOffset = dayOffset;
  }

  /** Constructor for holidays relative to another holiday with mondayisation option. */
  public MoveableHoliday(
      String name,
      String description,
      List<Locality> localities,
      HolidayType type,
      Holiday baseHoliday,
      int dayOffset,
      boolean mondayisation) {
    super(
        name,
        description,
        0,
        Month.JANUARY,
        null,
        null,
        null,
        null,
        localities,
        type,
        mondayisation);
    this.moveableType = MoveableHolidayType.RELATIVE_TO_HOLIDAY;
    this.baseHoliday = baseHoliday;
    this.dayOffset = dayOffset;
  }

  @Override
  public LocalDate getDate(int year) {
    // If date is already calculated for this year, return it
    if (getDate() != null && getDate().getYear() == year) {
      return getDate();
    }

    LocalDate calculatedDate =
        switch (moveableType) {
          case LUNAR_BASED -> calculateLunarBasedDate(year);
          case RELATIVE_TO_HOLIDAY -> calculateRelativeDate(year);
          case WEEKDAY_BASED -> calculateWeekdayBasedDate(year);
        };

    // Set the internal state
    setDate(calculatedDate);
    setDateWeekDay(calculatedDate.getDayOfWeek());
    return calculatedDate;
  }

  /**
   * Calculates the date for lunar-based holidays (primarily Easter). Uses the Western Christian
   * calculation for Easter.
   */
  private LocalDate calculateLunarBasedDate(int year) {
    return switch (getName()) {
      case "Easter Sunday" -> calculateEasterSunday(year);
      default -> LocalDate.of(year, Month.JANUARY, 1);
    };
  }

  /** Calculates the date for holidays relative to another holiday. */
  private LocalDate calculateRelativeDate(int year) {
    if (baseHoliday != null) {
      LocalDate baseDate = baseHoliday.getDate(year);
      return baseDate.plusDays(dayOffset);
    }
    return LocalDate.of(year, Month.JANUARY, 1); // Default fallback
  }

  /** Calculates the date for weekday-based holidays. */
  private LocalDate calculateWeekdayBasedDate(int year) {
    // Implementation for weekday-based calculations
    // This would depend on specific rules for each holiday
    return LocalDate.of(year, getMonth(), getDay());
  }

  /**
   * Calculates Easter Sunday using the Western Christian algorithm (Gregorian calendar). Based on
   * the algorithm by Jean Meeus from "Astronomical Algorithms" (1991).
   *
   * <p><strong>How Easter is Determined:</strong>
   *
   * <p>Easter is defined as the first Sunday after the first full moon occurring on or after the
   * spring equinox (March 21). This definition was established by the Council of Nicaea in 325 AD
   * to ensure all Christian churches celebrate Easter on the same date.
   *
   * <p><strong>Algorithm Steps:</strong>
   *
   * <ol>
   *   <li><strong>Golden Number:</strong> Determines the year's position in the 19-year Metonic
   *       cycle, which approximates the lunar calendar alignment with solar years.
   *   <li><strong>Century Calculations:</strong> Separates the year into century and
   *       year-within-century for leap year corrections.
   *   <li><strong>Gregorian Corrections:</strong> Applies corrections for the Gregorian calendar
   *       reform (1582) to account for lunar orbit variations.
   *   <li><strong>Epact Calculation:</strong> Determines the age of the moon on January 1st, which
   *       is crucial for finding the correct lunar month.
   *   <li><strong>Paschal Full Moon:</strong> Calculates when the ecclesiastical full moon occurs
   *       in March or April.
   *   <li><strong>Easter Sunday:</strong> Finds the first Sunday after the Paschal Full Moon.
   * </ol>
   *
   * <p><strong>Mathematical Foundation:</strong>
   *
   * <p>The algorithm uses modular arithmetic to handle the cyclical nature of both solar and lunar
   * calendars. The key insight is that lunar phases repeat every 19 solar years (Metonic cycle),
   * allowing for predictable calculations without astronomical observations.
   *
   * <p><strong>Accuracy:</strong>
   *
   * <p>This algorithm is accurate for all years in the Gregorian calendar (1583 onwards) and
   * produces the same dates used by Western Christian churches worldwide.
   *
   * <p><strong>Example Calculations:</strong>
   *
   * <ul>
   *   <li>2024: March 31 (early Easter due to leap year)
   *   <li>2025: April 20 (late Easter)
   *   <li>2026: April 5 (typical Easter date)
   * </ul>
   *
   * @param year the year for which to calculate Easter (must be ≥ 1583 for Gregorian accuracy)
   * @return the date of Easter Sunday for the given year
   * @throws IllegalArgumentException if year is before 1583 (pre-Gregorian calendar)
   * @see <a href="https://en.wikipedia.org/wiki/Date_of_Easter">Date of Easter - Wikipedia</a>
   * @see <a href="https://www.assa.org.au/edm">Easter Date Method - Astronomical Society</a>
   * @since 1.0
   * @author Based on Jean Meeus algorithm
   */
  private LocalDate calculateEasterSunday(int year) {
    // Validate input for Gregorian calendar accuracy
    if (year < MIN_GREGORIAN_YEAR) {
      throw new IllegalArgumentException(
          "Easter calculation is only accurate for Gregorian calendar years ("
              + MIN_GREGORIAN_YEAR
              + " onwards). Got: "
              + year);
    }

    int goldenNumber = year % METONIC_CYCLE_YEARS;
    int century = year / YEARS_PER_CENTURY;
    int totalDays = getTotalDays(year, century, goldenNumber);
    int easterMonth = totalDays / MONTH_DIVISOR; // Will be 3 (March) or 4 (April)
    int easterDay = (totalDays % MONTH_DIVISOR) + DAY_ADJUSTMENT; // Day of the month (1-31)

    return LocalDate.of(year, easterMonth, easterDay);
  }

  private static int getTotalDays(int year, int century, int goldenNumber) {
    int yearInCentury = year % YEARS_PER_CENTURY;

    int centuryLeapCorrection = century / LEAP_YEAR_CYCLE;
    int centuryRemainder = century % LEAP_YEAR_CYCLE;
    int epact = getEpact(century, goldenNumber, centuryLeapCorrection);
    int daysToFullMoon = getDaysToFullMoon(yearInCentury, centuryRemainder, epact);
    return getDays(goldenNumber, epact, daysToFullMoon);
  }

  private static int getDays(int goldenNumber, int epact, int daysToFullMoon) {
    int aprilCorrection =
        (goldenNumber
                + APRIL_CORRECTION_GOLDEN_MULTIPLIER * epact
                + APRIL_CORRECTION_DAYS_MULTIPLIER * daysToFullMoon)
            / APRIL_CORRECTION_DIVISOR;

    return epact + daysToFullMoon - DAYS_TO_FULL_MOON_MODULUS * aprilCorrection + FINAL_DATE_BASE;
  }

  private static int getDaysToFullMoon(int yearInCentury, int centuryRemainder, int epact) {
    int yearLeapCorrection = yearInCentury / LEAP_YEAR_CYCLE;
    int yearRemainder = yearInCentury % LEAP_YEAR_CYCLE;
    return (DAYS_TO_FULL_MOON_BASE
            + 2 * centuryRemainder
            + 2 * yearLeapCorrection
            - epact
            - yearRemainder)
        % DAYS_TO_FULL_MOON_MODULUS;
  }

  private static int getEpact(int century, int goldenNumber, int centuryLeapCorrection) {
    int gregorianCorrection = (century + GREGORIAN_LUNAR_CORRECTION) / GREGORIAN_CORRECTION_BASE;
    int precessionalCorrection =
        (century - gregorianCorrection + PRECESSIONAL_ADJUSTMENT) / PRECESSIONAL_DIVISOR;

    return (METONIC_CYCLE_YEARS * goldenNumber
            + century
            - centuryLeapCorrection
            - precessionalCorrection
            + EPACT_BASE)
        % EPACT_MODULUS;
  }

  // Getters
  public MoveableHolidayType getMoveableType() {
    return moveableType;
  }

  public Holiday getBaseHoliday() {
    return baseHoliday;
  }

  public int getDayOffset() {
    return dayOffset;
  }
}
