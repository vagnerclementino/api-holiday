package me.clementino.holiday.domain;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Immutable filter criteria for holiday queries.
 * Demonstrates Data-Oriented Programming by modeling query parameters as pure data.
 */
public record HolidayFilter(
    Optional<String> country,
    Optional<String> state,
    Optional<String> city,
    Optional<LocalDate> startDate,
    Optional<LocalDate> endDate,
    Optional<HolidayType> type
) {
    
    /**
     * Creates an empty filter (no filtering criteria).
     */
    public static HolidayFilter empty() {
        return new HolidayFilter(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
    }
    
    /**
     * Creates a filter for a specific country.
     */
    public static HolidayFilter byCountry(String country) {
        return new HolidayFilter(
            Optional.of(country),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty()
        );
    }
    
    /**
     * Creates a filter for a date range.
     */
    public static HolidayFilter byDateRange(LocalDate startDate, LocalDate endDate) {
        return new HolidayFilter(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.of(startDate),
            Optional.of(endDate),
            Optional.empty()
        );
    }
    
    /**
     * Creates a filter for a specific holiday type.
     */
    public static HolidayFilter byType(HolidayType type) {
        return new HolidayFilter(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            Optional.of(type)
        );
    }
}
