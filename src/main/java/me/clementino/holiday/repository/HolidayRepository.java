package me.clementino.holiday.repository;

import me.clementino.holiday.domain.Holiday;
import me.clementino.holiday.domain.HolidayType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends MongoRepository<Holiday, String> {  // Using String

    /**
     * Find holidays by country (case insensitive).
     */
    List<Holiday> findByCountryIgnoreCase(String country);

    /**
     * Find holidays by type.
     */
    List<Holiday> findByType(HolidayType type);

    /**
     * Find holidays by date range.
     */
    List<Holiday> findByDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Find holidays by country and state.
     */
    List<Holiday> findByCountryIgnoreCaseAndStateIgnoreCase(String country, String state);

    /**
     * Find holidays by country, state, and city.
     */
    List<Holiday> findByCountryIgnoreCaseAndStateIgnoreCaseAndCityIgnoreCase(
            String country, String state, String city);

    /**
     * Find holidays by name (case insensitive).
     */
    List<Holiday> findByNameIgnoreCase(String name);

    /**
     * Find recurring holidays.
     */
    List<Holiday> findByRecurringTrue();

    /**
     * Find holidays by date and country.
     */
    List<Holiday> findByDateAndCountryIgnoreCase(LocalDate date, String country);

    /**
     * Custom query to find holidays by multiple criteria.
     */
    @Query("{ $and: [ " +
           "{ $or: [ { 'country': { $regex: ?0, $options: 'i' } }, { ?0: null } ] }, " +
           "{ $or: [ { 'type': ?1 }, { ?1: null } ] }, " +
           "{ $or: [ { 'date': { $gte: ?2 } }, { ?2: null } ] }, " +
           "{ $or: [ { 'date': { $lte: ?3 } }, { ?3: null } ] } " +
           "] }")
    List<Holiday> findWithFilters(String country, HolidayType type, LocalDate startDate, LocalDate endDate);
}
