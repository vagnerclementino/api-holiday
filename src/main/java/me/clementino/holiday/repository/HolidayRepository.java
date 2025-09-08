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

  @Query(
      "{ $and: [ "
          + "{ $or: [ { ?0: null }, { 'localities.country': ?0 } ] }, "
          + "{ $or: [ { ?1: null }, { 'localities.state': ?1 } ] }, "
          + "{ $or: [ { ?2: null }, { 'localities.city': ?2 } ] }, "
          + "{ $or: [ { ?3: null }, { 'type': ?3 } ] }, "
          + "{ $or: [ { ?4: null }, { 'date': { $gte: ?4 } } ] }, "
          + "{ $or: [ { ?5: null }, { 'date': { $lte: ?5 } } ] }, "
          + "{ $or: [ { ?6: null }, { 'recurring': ?6 } ] }, "
          + "{ $or: [ { ?7: null }, { 'name': { $regex: ?7, $options: 'i' } } ] } "
          + "] }")
  List<HolidayEntity> findWithFilters(
      String country,
      String state,
      String city,
      HolidayType type,
      LocalDate startDate,
      LocalDate endDate,
      Boolean recurring,
      String namePattern);
}
