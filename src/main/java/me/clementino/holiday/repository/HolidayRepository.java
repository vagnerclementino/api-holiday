package me.clementino.holiday.repository;

import java.time.LocalDate;
import java.util.List;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.entity.HolidayEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for HolidayEntity persistence operations.
 *
 * <p>This repository provides comprehensive querying capabilities for holiday data, including
 * year-based calculations, locality-based filtering, and complex search operations. It supports
 * both traditional field-based queries and advanced MongoDB aggregation operations.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li>Year-based holiday queries for calculation caching
 *   <li>Hierarchical locality filtering (country → state → city)
 *   <li>Holiday type and date range filtering
 *   <li>Calculated vs base holiday differentiation
 *   <li>Complex multi-criteria search operations
 * </ul>
 */
@Repository
public interface HolidayRepository extends MongoRepository<HolidayEntity, String> {

  // ===== BASIC LOCALITY QUERIES =====

  /** Find holidays by country (case insensitive). */
  List<HolidayEntity> findByCountryIgnoreCase(String country);

  /** Find holidays by country and state. */
  List<HolidayEntity> findByCountryIgnoreCaseAndStateIgnoreCase(String country, String state);

  /** Find holidays by country, state, and city. */
  List<HolidayEntity> findByCountryIgnoreCaseAndStateIgnoreCaseAndCityIgnoreCase(
      String country, String state, String city);

  // ===== TYPE AND DATE QUERIES =====

  /** Find holidays by type. */
  List<HolidayEntity> findByType(HolidayType type);

  /** Find holidays by date range. */
  List<HolidayEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

  /** Find holidays by date and country. */
  List<HolidayEntity> findByDateAndCountryIgnoreCase(LocalDate date, String country);

  /** Find holidays by name (case insensitive). */
  List<HolidayEntity> findByNameIgnoreCase(String name);

  // ===== YEAR-BASED QUERIES FOR CALCULATIONS =====

  /** Find holidays by year and country. */
  List<HolidayEntity> findByYearAndCountryIgnoreCase(Integer year, String country);

  /** Find calculated holidays for a specific year. */
  List<HolidayEntity> findByYearAndIsCalculatedTrue(Integer year);

  /** Find base holidays (not calculated) for a country. */
  List<HolidayEntity> findByCountryIgnoreCaseAndIsCalculatedFalse(String country);

  /** Find holidays by year, country, and type. */
  List<HolidayEntity> findByYearAndCountryIgnoreCaseAndType(
      Integer year, String country, HolidayType type);

  // ===== DERIVED HOLIDAY QUERIES =====

  /** Find derived holidays by base holiday ID. */
  List<HolidayEntity> findByBaseHolidayId(String baseHolidayId);

  /** Find derived holidays by base holiday ID and year. */
  List<HolidayEntity> findByBaseHolidayIdAndYear(String baseHolidayId, Integer year);

  // ===== RECURRING AND SPECIAL QUERIES =====

  /** Find recurring holidays. */
  List<HolidayEntity> findByRecurringTrue();

  /** Find holidays with mondayisation enabled. */
  List<HolidayEntity> findByMondayisationTrue();

  /** Find holidays by holiday variant type. */
  List<HolidayEntity> findByHolidayVariant(String holidayVariant);

  // ===== COMPLEX CUSTOM QUERIES =====

  /** Custom query to find holidays by multiple criteria. */
  @Query(
      "{ $and: [ "
          + "{ $or: [ { 'country': { $regex: ?0, $options: 'i' } }, { ?0: null } ] }, "
          + "{ $or: [ { 'type': ?1 }, { ?1: null } ] }, "
          + "{ $or: [ { 'date': { $gte: ?2 } }, { ?2: null } ] }, "
          + "{ $or: [ { 'date': { $lte: ?3 } }, { ?3: null } ] } "
          + "] }")
  List<HolidayEntity> findWithFilters(
      String country, HolidayType type, LocalDate startDate, LocalDate endDate);

  /** Find holidays for a specific year with locality and type filters. */
  @Query(
      "{ $and: [ "
          + "{ 'year': ?0 }, "
          + "{ $or: [ { 'country': { $regex: ?1, $options: 'i' } }, { ?1: null } ] }, "
          + "{ $or: [ { 'state': { $regex: ?2, $options: 'i' } }, { ?2: null } ] }, "
          + "{ $or: [ { 'city': { $regex: ?3, $options: 'i' } }, { ?3: null } ] }, "
          + "{ $or: [ { 'type': ?4 }, { ?4: null } ] } "
          + "] }")
  List<HolidayEntity> findByYearWithLocalityAndType(
      Integer year, String country, String state, String city, HolidayType type);

  /** Find holidays that need calculation for a specific year and country. */
  @Query(
      "{ $and: [ "
          + "{ 'country': { $regex: ?1, $options: 'i' } }, "
          + "{ 'recurring': true }, "
          + "{ 'isCalculated': false }, "
          + "{ $or: [ { 'year': null }, { 'year': { $ne: ?0 } } ] } "
          + "] }")
  List<HolidayEntity> findHolidaysNeedingCalculation(Integer year, String country);

  /** Check if a holiday already exists for a specific year, country, and name. */
  @Query(
      "{ 'year': ?0, 'country': { $regex: ?1, $options: 'i' }, 'name': { $regex: ?2, $options: 'i' } }")
  List<HolidayEntity> findExistingHolidayForYear(Integer year, String country, String name);

  /** Find holidays by effective date (considering observed date). */
  @Query(
      "{ $and: [ "
          + "{ $or: [ { 'observed': ?0 }, { $and: [ { 'observed': null }, { 'date': ?0 } ] } ] }, "
          + "{ 'country': { $regex: ?1, $options: 'i' } } "
          + "] }")
  List<HolidayEntity> findByEffectiveDateAndCountry(LocalDate effectiveDate, String country);
}
