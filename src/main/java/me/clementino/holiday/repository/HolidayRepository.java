package me.clementino.holiday.repository;

import me.clementino.holiday.entity.HolidayEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
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
public interface HolidayRepository extends MongoRepository<HolidayEntity, String> {}
