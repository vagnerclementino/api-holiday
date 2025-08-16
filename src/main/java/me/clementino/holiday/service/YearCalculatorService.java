package me.clementino.holiday.service;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayData;
import me.clementino.holiday.domain.HolidayType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * Service for calculating holidays for specific years using Data-Oriented Programming principles.
 *
 * <p>This service demonstrates DOP principles by:
 *
 * <ul>
 *   <li><strong>Separating Operations from Data</strong> - Pure calculation functions
 *   <li><strong>Immutable Data</strong> - All calculations return new HolidayData instances
 *   <li><strong>Transparent Operations</strong> - Clear input/output relationships
 * </ul>
 *
 * <p>The service integrates with HolidayService to check for existing calculated holidays before
 * performing calculations, and includes caching for performance optimization.
 */
@Service
public class YearCalculatorService {

  private static final Logger logger = LoggerFactory.getLogger(YearCalculatorService.class);

  private final HolidayService holidayService;

  public YearCalculatorService(HolidayService holidayService) {
    this.holidayService = holidayService;
  }

  /**
   * Calculate holidays for a specific year, checking cache and existing data first.
   *
   * @param year the year to calculate holidays for
   * @return list of calculated holidays for the year
   */
  @Cacheable(value = "yearHolidays", key = "#year")
  public List<HolidayData> calculateHolidaysForYear(int year) {
    logger.debug("Calculating holidays for year: {}", year);

    // Get all base holidays (templates) from the service
    List<HolidayData> baseHolidays = holidayService.findAll();

    // Calculate specific dates for the year
    List<HolidayData> calculatedHolidays =
        baseHolidays.stream()
            .filter(holiday -> holiday.recurring()) // Only process recurring holidays
            .map(holiday -> calculateHolidayForYear(holiday, year))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

    logger.debug("Calculated {} holidays for year {}", calculatedHolidays.size(), year);
    return calculatedHolidays;
  }

  /**
   * Calculate a specific holiday for a given year.
   *
   * @param baseHoliday the base holiday template
   * @param year the target year
   * @return calculated holiday for the year, or empty if not applicable
   */
  public Optional<HolidayData> calculateHolidayForYear(HolidayData baseHoliday, int year) {
    if (!baseHoliday.recurring()) {
      // Non-recurring holidays are only valid for their original year
      return baseHoliday.date().getYear() == year ? Optional.of(baseHoliday) : Optional.empty();
    }

    try {
      // For recurring holidays, calculate the date for the specific year
      LocalDate calculatedDate = calculateDateForYear(baseHoliday.date(), year);

      // Create new holiday instance with calculated date
      HolidayData calculatedHoliday =
          new HolidayData(
              generateYearSpecificId(baseHoliday.id(), year),
              baseHoliday.name(),
              calculatedDate,
              baseHoliday.observed().map(observed -> calculateDateForYear(observed, year)),
              baseHoliday.location(),
              baseHoliday.type(),
              false, // Year-specific holidays are not recurring
              baseHoliday.description().map(desc -> desc + " (" + year + ")"),
              Optional.empty(), // Will be set when persisted
              Optional.empty(), // Will be set when persisted
              Optional.empty() // Will be set when persisted
              );

      return Optional.of(calculatedHoliday);

    } catch (Exception e) {
      logger.warn(
          "Failed to calculate holiday {} for year {}: {}",
          baseHoliday.name(),
          year,
          e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Calculate holidays for a specific year and location.
   *
   * @param year the year to calculate holidays for
   * @param country the country code
   * @param state optional state/province
   * @param city optional city
   * @return list of calculated holidays for the year and location
   */
  @Cacheable(
      value = "yearLocationHolidays",
      key =
          "#year + '_' + #country + '_' + (#state != null ? #state : 'null') + '_' + (#city != null ? #city : 'null')")
  public List<HolidayData> calculateHolidaysForYearAndLocation(
      int year, String country, String state, String city) {

    logger.debug(
        "Calculating holidays for year {} and location: {}, {}, {}", year, country, state, city);

    // Get holidays filtered by location
    List<HolidayData> locationHolidays =
        holidayService.findAllWithFilters(country, state, city, null, null, null, null, null);

    // Calculate specific dates for the year
    List<HolidayData> calculatedHolidays =
        locationHolidays.stream()
            .filter(holiday -> holiday.recurring())
            .map(holiday -> calculateHolidayForYear(holiday, year))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

    logger.debug(
        "Calculated {} holidays for year {} and location", calculatedHolidays.size(), year);
    return calculatedHolidays;
  }

  /**
   * Calculate holidays for a specific year and type.
   *
   * @param year the year to calculate holidays for
   * @param type the holiday type
   * @return list of calculated holidays for the year and type
   */
  @Cacheable(value = "yearTypeHolidays", key = "#year + '_' + #type")
  public List<HolidayData> calculateHolidaysForYearAndType(int year, HolidayType type) {
    logger.debug("Calculating holidays for year {} and type: {}", year, type);

    // Get holidays filtered by type
    List<HolidayData> typeHolidays =
        holidayService.findAllWithFilters(null, null, null, type, null, null, null, null);

    // Calculate specific dates for the year
    List<HolidayData> calculatedHolidays =
        typeHolidays.stream()
            .filter(holiday -> holiday.recurring())
            .map(holiday -> calculateHolidayForYear(holiday, year))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();

    logger.debug(
        "Calculated {} holidays for year {} and type {}", calculatedHolidays.size(), year, type);
    return calculatedHolidays;
  }

  /**
   * Check if a holiday exists for a specific year before calculating.
   *
   * @param baseHolidayId the base holiday ID
   * @param year the target year
   * @return true if holiday already exists for the year
   */
  public boolean holidayExistsForYear(String baseHolidayId, int year) {
    String yearSpecificId = generateYearSpecificId(baseHolidayId, year);
    return holidayService.findById(yearSpecificId).isPresent();
  }

  /**
   * Calculate and persist a holiday for a specific year if it doesn't exist.
   *
   * @param baseHolidayId the base holiday ID
   * @param year the target year
   * @return the calculated and persisted holiday, or existing if already present
   */
  public Optional<HolidayData> calculateAndPersistHolidayForYear(String baseHolidayId, int year) {
    // Check if already exists
    if (holidayExistsForYear(baseHolidayId, year)) {
      String yearSpecificId = generateYearSpecificId(baseHolidayId, year);
      return holidayService.findById(yearSpecificId);
    }

    // Get base holiday
    Optional<HolidayData> baseHoliday = holidayService.findById(baseHolidayId);
    if (baseHoliday.isEmpty()) {
      logger.warn("Base holiday not found: {}", baseHolidayId);
      return Optional.empty();
    }

    // Calculate for year
    Optional<HolidayData> calculatedHoliday = calculateHolidayForYear(baseHoliday.get(), year);
    if (calculatedHoliday.isEmpty()) {
      logger.warn("Failed to calculate holiday {} for year {}", baseHolidayId, year);
      return Optional.empty();
    }

    // Persist calculated holiday
    try {
      HolidayData persistedHoliday = holidayService.create(calculatedHoliday.get());
      logger.info("Calculated and persisted holiday {} for year {}", baseHolidayId, year);
      return Optional.of(persistedHoliday);
    } catch (Exception e) {
      logger.error(
          "Failed to persist calculated holiday {} for year {}: {}",
          baseHolidayId,
          year,
          e.getMessage());
      return Optional.empty();
    }
  }

  /**
   * Pure function to calculate a date for a specific year.
   *
   * <p>For fixed holidays, simply changes the year while keeping month and day. This is a
   * simplified implementation - in a real system, this would handle complex moveable holidays like
   * Easter, Thanksgiving, etc.
   *
   * @param originalDate the original date
   * @param targetYear the target year
   * @return the calculated date for the target year
   */
  private LocalDate calculateDateForYear(LocalDate originalDate, int targetYear) {
    try {
      // Simple year substitution for fixed holidays
      // In a real implementation, this would handle moveable holidays
      return originalDate.withYear(targetYear);
    } catch (Exception e) {
      // Handle leap year issues (e.g., Feb 29 in non-leap years)
      if (originalDate.getMonthValue() == 2 && originalDate.getDayOfMonth() == 29) {
        // Move Feb 29 to Feb 28 in non-leap years
        return LocalDate.of(targetYear, 2, 28);
      }
      throw e;
    }
  }

  /**
   * Generate a year-specific ID for a calculated holiday.
   *
   * @param baseId the base holiday ID
   * @param year the target year
   * @return year-specific holiday ID
   */
  private String generateYearSpecificId(String baseId, int year) {
    return baseId + "_" + year;
  }

  /**
   * Validate that a year is within acceptable range.
   *
   * @param year the year to validate
   * @return true if year is valid
   */
  public boolean isValidYear(int year) {
    int currentYear = Year.now().getValue();
    // Allow calculations for 100 years in the past and 50 years in the future
    return year >= (currentYear - 100) && year <= (currentYear + 50);
  }
}
