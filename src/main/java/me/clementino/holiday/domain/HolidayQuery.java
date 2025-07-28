package me.clementino.holiday.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

/**
 * Immutable record representing a holiday query/filter.
 * Following DOP v1.1 Principle 2: Model the Data, the Whole Data, and Nothing but the Data.
 * 
 * This record models exactly what a holiday query contains - no more, no less.
 * It represents all possible query parameters in a type-safe way.
 */
public record HolidayQuery(
    Optional<String> country,
    Optional<String> state,
    Optional<String> city,
    Optional<HolidayType> type,
    Optional<LocalDate> startDate,
    Optional<LocalDate> endDate,
    Optional<Boolean> recurring,
    Optional<String> namePattern
) {
    // Compact constructor for validation and normalization
    public HolidayQuery {
        // Ensure Optional fields are never null
        country = Objects.requireNonNullElse(country, Optional.empty());
        state = Objects.requireNonNullElse(state, Optional.empty());
        city = Objects.requireNonNullElse(city, Optional.empty());
        type = Objects.requireNonNullElse(type, Optional.empty());
        startDate = Objects.requireNonNullElse(startDate, Optional.empty());
        endDate = Objects.requireNonNullElse(endDate, Optional.empty());
        recurring = Objects.requireNonNullElse(recurring, Optional.empty());
        namePattern = Objects.requireNonNullElse(namePattern, Optional.empty());
        
        // Validate date range if both dates are present
        if (startDate.isPresent() && endDate.isPresent()) {
            if (startDate.get().isAfter(endDate.get())) {
                throw new IllegalArgumentException("Start date cannot be after end date");
            }
        }
    }
    
    /**
     * Empty query (no filters).
     */
    public static HolidayQuery empty() {
        return new HolidayQuery(Optional.empty(), Optional.empty(), Optional.empty(),
                               Optional.empty(), Optional.empty(), Optional.empty(),
                               Optional.empty(), Optional.empty());
    }
    
    /**
     * Query by country only.
     */
    public static HolidayQuery byCountry(String country) {
        return new HolidayQuery(Optional.of(country), Optional.empty(), Optional.empty(),
                               Optional.empty(), Optional.empty(), Optional.empty(),
                               Optional.empty(), Optional.empty());
    }
    
    /**
     * Query by location.
     */
    public static HolidayQuery byLocation(Location location) {
        return new HolidayQuery(Optional.of(location.country()), location.state(), location.city(),
                               Optional.empty(), Optional.empty(), Optional.empty(),
                               Optional.empty(), Optional.empty());
    }
    
    /**
     * Query by type.
     */
    public static HolidayQuery byType(HolidayType type) {
        return new HolidayQuery(Optional.empty(), Optional.empty(), Optional.empty(),
                               Optional.of(type), Optional.empty(), Optional.empty(),
                               Optional.empty(), Optional.empty());
    }
    
    /**
     * Query by date range.
     */
    public static HolidayQuery byDateRange(LocalDate startDate, LocalDate endDate) {
        return new HolidayQuery(Optional.empty(), Optional.empty(), Optional.empty(),
                               Optional.empty(), Optional.of(startDate), Optional.of(endDate),
                               Optional.empty(), Optional.empty());
    }
    
    // Query methods
    
    public boolean isEmpty() {
        return country.isEmpty() && state.isEmpty() && city.isEmpty() && 
               type.isEmpty() && startDate.isEmpty() && endDate.isEmpty() &&
               recurring.isEmpty() && namePattern.isEmpty();
    }
    
    public boolean hasLocationFilter() {
        return country.isPresent() || state.isPresent() || city.isPresent();
    }
    
    public boolean hasDateFilter() {
        return startDate.isPresent() || endDate.isPresent();
    }
    
    public boolean hasTypeFilter() {
        return type.isPresent();
    }
    
    public boolean hasRecurringFilter() {
        return recurring.isPresent();
    }
    
    public boolean hasNameFilter() {
        return namePattern.isPresent() && !namePattern.get().isBlank();
    }
}
