package me.clementino.holiday.repository;

import java.time.LocalDate;
import java.util.List;
import me.clementino.holiday.domain.dop.HolidayType;
import me.clementino.holiday.entity.HolidayEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * MongoDB repository for HolidayEntity persistence operations.
 *
 * <p>This repository provides comprehensive querying capabilities for holiday data, including
 * locality-based filtering using the new LocalityEntity structure, date range queries, and
 * type-based filtering.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li>Locality-based filtering using embedded LocalityEntity documents
 *   <li>Holiday type and date range filtering
 *   <li>Name-based search operations
 *   <li>Complex multi-criteria search operations
 * </ul>
 */
@Repository
public interface HolidayRepository extends MongoRepository<HolidayEntity, String> {

  // ===== BASIC QUERIES =====

  /** Find holidays by type. */
  List<HolidayEntity> findByType(HolidayType type);

  /** Find holidays by date range. */
  List<HolidayEntity> findByDateBetween(LocalDate startDate, LocalDate endDate);

  /** Find holidays by name (case insensitive). */
  List<HolidayEntity> findByNameIgnoreCase(String name);

  /** Find holidays by date. */
  List<HolidayEntity> findByDate(LocalDate date);

  // ===== LOCALITY-BASED QUERIES =====

  /** Find holidays by country code in localities. */
  @Query("{ 'localities.countryCode': { $regex: ?0, $options: 'i' } }")
  List<HolidayEntity> findByCountryCode(String countryCode);

  /** Find holidays by country code and subdivision code. */
  @Query(
      "{ $and: [ "
          + "{ 'localities.countryCode': { $regex: ?0, $options: 'i' } }, "
          + "{ 'localities.subdivisionCode': { $regex: ?1, $options: 'i' } } "
          + "] }")
  List<HolidayEntity> findByCountryCodeAndSubdivisionCode(
      String countryCode, String subdivisionCode);

  /** Find holidays by country code, subdivision code, and city. */
  @Query(
      "{ $and: [ "
          + "{ 'localities.countryCode': { $regex: ?0, $options: 'i' } }, "
          + "{ 'localities.subdivisionCode': { $regex: ?1, $options: 'i' } }, "
          + "{ 'localities.cityName': { $regex: ?2, $options: 'i' } } "
          + "] }")
  List<HolidayEntity> findByCountryCodeAndSubdivisionCodeAndCityName(
      String countryCode, String subdivisionCode, String cityName);

  /** Find holidays by date and country code. */
  @Query(
      "{ $and: [ "
          + "{ 'date': ?0 }, "
          + "{ 'localities.countryCode': { $regex: ?1, $options: 'i' } } "
          + "] }")
  List<HolidayEntity> findByDateAndCountryCode(LocalDate date, String countryCode);

  // ===== COMPLEX CUSTOM QUERIES =====

  /** Custom query to find holidays by multiple criteria. */
  @Query(
      "{ $and: [ "
          + "{ $or: [ { 'localities.countryCode': { $regex: ?0, $options: 'i' } }, { ?0: null } ] }, "
          + "{ $or: [ { 'type': ?1 }, { ?1: null } ] }, "
          + "{ $or: [ { 'date': { $gte: ?2 } }, { ?2: null } ] }, "
          + "{ $or: [ { 'date': { $lte: ?3 } }, { ?3: null } ] } "
          + "] }")
  List<HolidayEntity> findWithFilters(
      String countryCode, HolidayType type, LocalDate startDate, LocalDate endDate);

  /** Find holidays by effective date (considering observed date). */
  @Query(
      "{ $and: [ "
          + "{ $or: [ "
          + "  { $and: [ { 'observed': { $ne: null } }, { 'observed': ?0 } ] }, "
          + "  { $and: [ { 'observed': null }, { 'date': ?0 } ] } "
          + "] }, "
          + "{ 'localities.countryCode': { $regex: ?1, $options: 'i' } } "
          + "] }")
  List<HolidayEntity> findByEffectiveDateAndCountryCode(
      LocalDate effectiveDate, String countryCode);

  /** Find holidays by type and country code. */
  @Query(
      "{ $and: [ "
          + "{ 'type': ?0 }, "
          + "{ 'localities.countryCode': { $regex: ?1, $options: 'i' } } "
          + "] }")
  List<HolidayEntity> findByTypeAndCountryCode(HolidayType type, String countryCode);
}
