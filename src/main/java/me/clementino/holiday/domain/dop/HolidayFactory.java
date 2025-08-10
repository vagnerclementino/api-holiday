package me.clementino.holiday.domain.dop;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayType;

/**
 * Factory for creating Holiday instances following DOP principles. Based on the OOP Holiday classes
 * but with factory methods for immutable records.
 *
 * <p>DOP Principles Applied: 1. Model Data Immutably and Transparently - Creates immutable holiday
 * records 2. Model the Data, the Whole Data, and Nothing but the Data - Factory methods for
 * complete data 3. Make Illegal States Unrepresentable - Validates inputs to prevent invalid
 * holidays 4. Separate Operations from Data - Factory operations separate from holiday data
 */
public final class HolidayFactory {

  private HolidayFactory() {
    // Utility class - prevent instantiation
  }

  /** Creates a fixed holiday with all required fields. */
  public static FixedHoliday createFixed(
      String name,
      String description,
      int day,
      Month month,
      List<Locality> localities,
      HolidayType type) {

    // Calculate date for current year as default, with validation
    LocalDate calculatedDate;
    try {
      calculatedDate = LocalDate.of(LocalDate.now().getYear(), month, day);
    } catch (DateTimeException e) {
      throw new IllegalArgumentException(
          "Invalid day/month combination: day=" + day + ", month=" + month, e);
    }

    return new FixedHoliday(name, description, calculatedDate, localities, type);
  }

  /** Creates a fixed holiday with minimal required fields. */
  public static FixedHoliday createFixed(
      String name, int day, Month month, List<Locality> localities, HolidayType type) {

    return createFixed(name, "", day, month, localities, type);
  }

  /** Creates an observed holiday with all required fields. */
  public static ObservedHoliday createObserved(
      String name,
      String description,
      int day,
      Month month,
      List<Locality> localities,
      HolidayType type,
      boolean mondayisation) {

    // Calculate date for current year as default, with validation
    LocalDate calculatedDate;
    try {
      calculatedDate = LocalDate.of(LocalDate.now().getYear(), month, day);
    } catch (DateTimeException e) {
      throw new IllegalArgumentException(
          "Invalid day/month combination: day=" + day + ", month=" + month, e);
    }

    // Calculate observed date based on mondayisation rules
    LocalDate observedDate = calculatedDate;
    if (mondayisation) {
      observedDate = applyMondayisationRules(calculatedDate);
    }

    return new ObservedHoliday(
        name, description, calculatedDate, observedDate, localities, type, mondayisation);
  }

  /** Creates an observed holiday with minimal required fields. */
  public static ObservedHoliday createObserved(
      String name,
      int day,
      Month month,
      List<Locality> localities,
      HolidayType type,
      boolean mondayisation) {

    return createObserved(name, "", day, month, localities, type, mondayisation);
  }

  /**
   * Creates a holiday with pre-calculated values - returns appropriate type based on mondayisation.
   */
  public static Holiday createHolidayWithCalculatedValues(
      String name,
      String description,
      int day,
      Month month,
      List<Locality> localities,
      HolidayType type,
      boolean mondayisation,
      LocalDate date,
      LocalDate observed,
      DayOfWeek dateWeekDay,
      DayOfWeek observedWeekDay) {

    // If mondayisation is needed, create ObservedHoliday
    if (mondayisation) {
      return new ObservedHoliday(
          name, description, date, observed, localities, type, mondayisation);
    }

    // Otherwise create FixedHoliday
    return new FixedHoliday(name, description, date, localities, type);
  }

  /** Creates a moveable holiday with all required fields. */
  public static MoveableHoliday createMoveable(
      String name,
      String description,
      int day,
      Month month,
      List<Locality> localities,
      HolidayType type,
      boolean mondayisation,
      MoveableHolidayType moveableType,
      Optional<Holiday> baseHoliday,
      int dayOffset) {

    // For moveable holidays, we'll use a placeholder date that should be calculated by operations
    LocalDate placeholderDate = LocalDate.of(LocalDate.now().getYear(), month, Math.max(1, day));

    return new MoveableHoliday(
        name,
        description,
        placeholderDate,
        localities,
        type,
        mondayisation,
        moveableType,
        baseHoliday,
        dayOffset);
  }

  /** Creates a moveable holiday with minimal required fields. */
  public static MoveableHoliday createMoveable(
      String name, List<Locality> localities, HolidayType type, MoveableHolidayType moveableType) {

    return createMoveable(
        name, "", 0, Month.JANUARY, localities, type, false, moveableType, Optional.empty(), 0);
  }

  /** Creates a lunar-based moveable holiday (like Easter). */
  public static MoveableHoliday createLunarBased(
      String name,
      String description,
      List<Locality> localities,
      HolidayType type,
      boolean mondayisation) {

    return createMoveable(
        name,
        description,
        0,
        Month.JANUARY,
        localities,
        type,
        mondayisation,
        MoveableHolidayType.LUNAR_BASED,
        Optional.empty(),
        0);
  }

  /** Creates a holiday relative to another holiday. */
  public static MoveableHoliday createRelativeToHoliday(
      String name,
      String description,
      List<Locality> localities,
      HolidayType type,
      Holiday baseHoliday,
      int dayOffset,
      boolean mondayisation) {

    return createMoveable(
        name,
        description,
        0,
        Month.JANUARY,
        localities,
        type,
        mondayisation,
        MoveableHolidayType.RELATIVE_TO_HOLIDAY,
        Optional.of(baseHoliday),
        dayOffset);
  }

  /** Creates a weekday-based moveable holiday. */
  public static MoveableHoliday createWeekdayBased(
      String name,
      String description,
      int day,
      Month month,
      List<Locality> localities,
      HolidayType type,
      boolean mondayisation) {

    return createMoveable(
        name,
        description,
        day,
        month,
        localities,
        type,
        mondayisation,
        MoveableHolidayType.WEEKDAY_BASED,
        Optional.empty(),
        0);
  }

  // Pre-defined common holidays

  // Helper method to create country locality from name
  private static Locality.Country getCountryByName(String countryName) {
    return switch (countryName.toLowerCase()) {
      case "brazil" -> Locality.brazil();
      case "united states" -> Locality.unitedStates();
      case "canada" -> Locality.canada();
      default ->
          new Locality.Country(
              countryName.substring(0, Math.min(2, countryName.length())).toUpperCase(),
              countryName);
    };
  }

  // Helper method to get full state name from code
  private static String getStateFullName(String stateCode) {
    return switch (stateCode.toUpperCase()) {
      case "SP" -> "São Paulo";
      case "RJ" -> "Rio de Janeiro";
      case "MG" -> "Minas Gerais";
      case "RS" -> "Rio Grande do Sul";
      case "PR" -> "Paraná";
      case "SC" -> "Santa Catarina";
      case "BA" -> "Bahia";
      case "GO" -> "Goiás";
      case "ES" -> "Espírito Santo";
      case "DF" -> "Distrito Federal";
      case "CA" -> "California";
      case "NY" -> "New York";
      case "TX" -> "Texas";
      case "FL" -> "Florida";
      default -> stateCode; // Fallback to code if not found
    };
  }

  /**
   * Creates Christmas Day for a specific country - uses ObservedHoliday since it often has
   * mondayisation.
   */
  public static ObservedHoliday createChristmas(String country) {
    return createObserved(
        "Christmas Day",
        "Christian holiday celebrating the birth of Jesus Christ",
        25,
        Month.DECEMBER,
        List.of(getCountryByName(country)),
        HolidayType.RELIGIOUS,
        true);
  }

  /**
   * Creates New Year's Day for a specific country - uses ObservedHoliday since it often has
   * mondayisation.
   */
  public static ObservedHoliday createNewYear(String country) {
    return createObserved(
        "New Year's Day",
        "First day of the Gregorian calendar year",
        1,
        Month.JANUARY,
        List.of(getCountryByName(country)),
        HolidayType.NATIONAL,
        true);
  }

  /** Creates Independence Day for Brazil (September 7). */
  public static FixedHoliday createBrazilIndependenceDay() {
    return createFixed(
        "Independence Day",
        "Brazil's independence from Portugal",
        7,
        Month.SEPTEMBER,
        List.of(Locality.brazil()),
        HolidayType.NATIONAL);
  }

  /** Creates Independence Day for the United States (July 4). */
  public static FixedHoliday createUSIndependenceDay() {
    return createFixed(
        "Independence Day",
        "Celebrates the Declaration of Independence",
        4,
        Month.JULY,
        List.of(Locality.unitedStates()),
        HolidayType.NATIONAL);
  }

  /** Creates Easter Sunday (lunar-based). */
  public static MoveableHoliday createEasterSunday(String country) {
    return createLunarBased(
        "Easter Sunday",
        "Christian holiday celebrating the resurrection of Jesus Christ",
        List.of(getCountryByName(country)),
        HolidayType.RELIGIOUS,
        false);
  }

  /** Creates Good Friday (relative to Easter). */
  public static MoveableHoliday createGoodFriday(String country) {
    var easter = createEasterSunday(country);
    return createRelativeToHoliday(
        "Good Friday",
        "Christian holiday commemorating the crucifixion of Jesus Christ",
        List.of(getCountryByName(country)),
        HolidayType.RELIGIOUS,
        easter,
        -2, // 2 days before Easter
        false);
  }

  /** Creates Easter Monday (relative to Easter). */
  public static MoveableHoliday createEasterMonday(String country) {
    var easter = createEasterSunday(country);
    return createRelativeToHoliday(
        "Easter Monday",
        "Christian holiday celebrating the resurrection of Jesus Christ",
        List.of(getCountryByName(country)),
        HolidayType.RELIGIOUS,
        easter,
        1, // 1 day after Easter
        false);
  }

  /** Creates Thanksgiving for the United States (4th Thursday of November). */
  public static MoveableHoliday createUSThanksgiving() {
    return createWeekdayBased(
        "Thanksgiving Day",
        "National day of giving thanks, traditionally celebrated with family gatherings",
        4, // 4th occurrence (this would need special handling in operations)
        Month.NOVEMBER,
        List.of(Locality.unitedStates()),
        HolidayType.NATIONAL,
        false);
  }

  /** Creates Labor Day for the United States (1st Monday of September). */
  public static MoveableHoliday createUSLaborDay() {
    return createWeekdayBased(
        "Labor Day",
        "Federal holiday honoring the American labor movement",
        1, // 1st occurrence (this would need special handling in operations)
        Month.SEPTEMBER,
        List.of(Locality.unitedStates()),
        HolidayType.NATIONAL,
        false);
  }

  /** Creates Memorial Day for the United States (last Monday of May). */
  public static MoveableHoliday createUSMemorialDay() {
    return createWeekdayBased(
        "Memorial Day",
        "Federal holiday honoring military personnel who died in service",
        -1, // Last occurrence (this would need special handling in operations)
        Month.MAY,
        List.of(Locality.unitedStates()),
        HolidayType.NATIONAL,
        false);
  }

  /** Creates a state-level holiday - returns appropriate type based on mondayisation. */
  public static Holiday createStateHoliday(
      String name,
      String description,
      int day,
      Month month,
      String country,
      String state,
      boolean mondayisation) {

    // If mondayisation is needed, create ObservedHoliday
    if (mondayisation) {
      return createObserved(
          name,
          description,
          day,
          month,
          List.of(Locality.subdivision(getCountryByName(country), state, getStateFullName(state))),
          HolidayType.STATE,
          mondayisation);
    }

    // Otherwise create FixedHoliday
    return createFixed(
        name,
        description,
        day,
        month,
        List.of(Locality.subdivision(getCountryByName(country), state, getStateFullName(state))),
        HolidayType.STATE);
  }

  /** Creates a city-level holiday - returns appropriate type based on mondayisation. */
  public static Holiday createCityHoliday(
      String name,
      String description,
      int day,
      Month month,
      String country,
      String state,
      String city,
      boolean mondayisation) {

    // If mondayisation is needed, create ObservedHoliday
    if (mondayisation) {
      return createObserved(
          name,
          description,
          day,
          month,
          List.of(
              Locality.city(
                  city,
                  Locality.subdivision(getCountryByName(country), state, getStateFullName(state)),
                  getCountryByName(country))),
          HolidayType.MUNICIPAL,
          mondayisation);
    }

    // Otherwise create FixedHoliday
    return createFixed(
        name,
        description,
        day,
        month,
        List.of(
            Locality.city(
                city,
                Locality.subdivision(getCountryByName(country), state, getStateFullName(state)),
                getCountryByName(country))),
        HolidayType.MUNICIPAL);
  }

  /** Applies mondayisation rules to a date. */
  private static LocalDate applyMondayisationRules(LocalDate date) {
    return switch (date.getDayOfWeek()) {
      case SATURDAY -> date.minusDays(1); // Saturday -> Friday
      case SUNDAY -> date.plusDays(1); // Sunday -> Monday
      default -> date; // Weekdays remain unchanged
    };
  }
}
