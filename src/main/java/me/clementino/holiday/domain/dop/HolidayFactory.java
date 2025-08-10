package me.clementino.holiday.domain.dop;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import me.clementino.holiday.domain.HolidayType;

/**
 * Factory for creating Holiday instances following DOP principles. Based on the OOP Holiday classes
 * but with factory methods for immutable records.
 *
 * <p>DOP Principles Applied:
 *
 * <ol>
 *   <li><strong>Model Data Immutably and Transparently</strong> - Creates immutable holiday records
 *   <li><strong>Model the Data, the Whole Data, and Nothing but the Data</strong> - Factory methods
 *       for complete data
 *   <li><strong>Make Illegal States Unrepresentable</strong> - Validates inputs to prevent invalid
 *       holidays
 *   <li><strong>Separate Operations from Data</strong> - Factory operations separate from holiday
 *       data
 * </ol>
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

  // ===== SPECIFIC HOLIDAY FACTORY METHODS =====

  /** Creates Christmas Day for a specific country. */
  public static FixedHoliday createChristmas(String country) {
    var countryObj = getCountryByName(country);
    return createFixed(
        "Christmas Day",
        "Christian celebration of the birth of Jesus Christ",
        25,
        Month.DECEMBER,
        List.of(countryObj),
        HolidayType.RELIGIOUS);
  }

  /** Creates New Year's Day for a specific country. */
  public static FixedHoliday createNewYear(String country) {
    var countryObj = getCountryByName(country);
    return createFixed(
        "New Year's Day",
        "First day of the Gregorian calendar year",
        1,
        Month.JANUARY,
        List.of(countryObj),
        HolidayType.NATIONAL);
  }

  /** Creates Independence Day for Brazil. */
  public static FixedHoliday createBrazilIndependenceDay() {
    return createFixed(
        "Independence Day",
        "Brazil's independence from Portugal",
        7,
        Month.SEPTEMBER,
        List.of(new Locality.Country("BR", "Brazil")),
        HolidayType.NATIONAL);
  }

  /** Creates Independence Day for the United States. */
  public static FixedHoliday createUSIndependenceDay() {
    return createFixed(
        "Independence Day",
        "United States independence celebration",
        4,
        Month.JULY,
        List.of(new Locality.Country("US", "United States")),
        HolidayType.NATIONAL);
  }

  /** Creates International Workers' Day. */
  public static FixedHoliday createLaborDay(String country) {
    var countryObj = getCountryByName(country);
    return createFixed(
        "International Workers' Day",
        "International celebration of workers",
        1,
        Month.MAY,
        List.of(countryObj),
        HolidayType.NATIONAL);
  }

  /** Creates Christmas with mondayisation for a specific country. */
  public static ObservedHoliday createChristmasWithMondayisation(String country) {
    var countryObj = getCountryByName(country);
    return createObserved(
        "Christmas Day",
        "Christian celebration with mondayisation",
        25,
        Month.DECEMBER,
        List.of(countryObj),
        HolidayType.RELIGIOUS,
        true);
  }

  /** Creates New Year with mondayisation for a specific country. */
  public static ObservedHoliday createNewYearWithMondayisation(String country) {
    var countryObj = getCountryByName(country);
    return createObserved(
        "New Year's Day",
        "First day of the year with mondayisation",
        1,
        Month.JANUARY,
        List.of(countryObj),
        HolidayType.NATIONAL,
        true);
  }

  // ===== NEW FACTORY METHODS USING KnownHoliday ENUM =====

  /** Creates Easter Sunday for a specific country using the new KnownHoliday enum. */
  public static MoveableHoliday createEasterWithEnum(String country) {
    LocalDate easterDate = HolidayOperations.calculateEaster(LocalDate.now().getYear());
    return new MoveableHoliday(
        KnownHoliday.EASTER,
        KnownHoliday.EASTER.getDescription(),
        easterDate,
        List.of(Locality.country(getCountryByName(country).name(), country)),
        HolidayType.RELIGIOUS,
        false);
  }

  /** Creates Good Friday (2 days before Easter) using the new MoveableFromBaseHoliday. */
  public static MoveableFromBaseHoliday createGoodFridayWithEnum(String country) {
    MoveableHoliday easter = createEasterWithEnum(country);
    LocalDate goodFridayDate = easter.date().plusDays(-2); // 2 days before Easter

    return new MoveableFromBaseHoliday(
        KnownHoliday.GOOD_FRIDAY,
        KnownHoliday.GOOD_FRIDAY.getDescription(),
        goodFridayDate,
        List.of(Locality.country(getCountryByName(country).name(), country)),
        HolidayType.RELIGIOUS,
        easter,
        -2, // 2 days before Easter
        false);
  }

  /** Creates Easter Monday (1 day after Easter) using the new MoveableFromBaseHoliday. */
  public static MoveableFromBaseHoliday createEasterMondayWithEnum(String country) {
    MoveableHoliday easter = createEasterWithEnum(country);
    LocalDate easterMondayDate = easter.date().plusDays(1); // 1 day after Easter

    return new MoveableFromBaseHoliday(
        KnownHoliday.EASTER_MONDAY,
        KnownHoliday.EASTER_MONDAY.getDescription(),
        easterMondayDate,
        List.of(Locality.country(getCountryByName(country).name(), country)),
        HolidayType.RELIGIOUS,
        easter,
        1, // 1 day after Easter
        false);
  }

  /** Creates Thanksgiving (US - 4th Thursday of November) using the new KnownHoliday enum. */
  public static MoveableHoliday createThanksgivingWithEnum() {
    LocalDate thanksgivingDate = HolidayOperations.calculateThanksgiving(LocalDate.now().getYear());
    return new MoveableHoliday(
        KnownHoliday.THANKSGIVING_US,
        KnownHoliday.THANKSGIVING_US.getDescription(),
        thanksgivingDate,
        List.of(Locality.country("US", "United States")),
        HolidayType.NATIONAL,
        false);
  }

  /** Creates New Year's Day using the new KnownHoliday enum. */
  public static FixedHoliday createNewYearWithEnum(String country) {
    return new FixedHoliday(
        KnownHoliday.NEW_YEAR.getDisplayName(),
        KnownHoliday.NEW_YEAR.getDescription(),
        LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1),
        List.of(Locality.country(getCountryByName(country).name(), country)),
        HolidayType.NATIONAL);
  }

  /** Creates Christmas Day using the new KnownHoliday enum. */
  public static ObservedHoliday createChristmasWithEnum(String country) {
    LocalDate christmasDate = LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 25);
    LocalDate observedDate = applyMondayisationRules(christmasDate);

    return new ObservedHoliday(
        KnownHoliday.CHRISTMAS.getDisplayName(),
        KnownHoliday.CHRISTMAS.getDescription(),
        christmasDate,
        observedDate,
        List.of(Locality.country(getCountryByName(country).name(), country)),
        HolidayType.RELIGIOUS,
        true);
  }

  // ===== HELPER METHODS =====

  /** Applies mondayisation rules to a date. */
  private static LocalDate applyMondayisationRules(LocalDate date) {
    return switch (date.getDayOfWeek()) {
      case SATURDAY -> date.minusDays(1); // Saturday -> Friday
      case SUNDAY -> date.plusDays(1); // Sunday -> Monday
      default -> date; // Weekdays remain unchanged
    };
  }

  /** Helper method to get country by name. */
  private static Locality.Country getCountryByName(String countryName) {
    return switch (countryName.toLowerCase()) {
      case "brazil", "br" -> new Locality.Country("BR", "Brazil");
      case "united states", "us", "usa" -> new Locality.Country("US", "United States");
      case "canada", "ca" -> new Locality.Country("CA", "Canada");
      case "united kingdom", "uk", "gb" -> new Locality.Country("GB", "United Kingdom");
      case "france", "fr" -> new Locality.Country("FR", "France");
      case "germany", "de" -> new Locality.Country("DE", "Germany");
      case "japan", "jp" -> new Locality.Country("JP", "Japan");
      case "australia", "au" -> new Locality.Country("AU", "Australia");
      default -> {
        String code = countryName.length() == 2 ? countryName.toUpperCase() : "XX";
        yield new Locality.Country(code, countryName);
      }
    };
  }

  /** Helper method to get state/subdivision full name. */
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
      default -> stateCode;
    };
  }
}
