package me.clementino.holiday.domain;

import java.util.Objects;
import java.util.Optional;

/**
 * Immutable record representing a location.
 * Following DOP principles: Model data immutably and transparently.
 * Models the data, the whole data, and nothing but the data.
 */
public record Location(
    String country,
    Optional<String> state,
    Optional<String> city
) {
    // Compact constructor for validation
    public Location {
        Objects.requireNonNull(country, "Country cannot be null");
        if (country.isBlank()) {
            throw new IllegalArgumentException("Country cannot be blank");
        }
        
        // Ensure Optional fields are never null
        state = Objects.requireNonNullElse(state, Optional.empty());
        city = Objects.requireNonNullElse(city, Optional.empty());
    }
    
    /**
     * Convenience constructor for country-only location.
     */
    public Location(String country) {
        this(country, Optional.empty(), Optional.empty());
    }
    
    /**
     * Convenience constructor for country and state.
     */
    public Location(String country, String state) {
        this(country, Optional.ofNullable(state), Optional.empty());
    }
    
    /**
     * Convenience constructor for country, state, and city.
     */
    public Location(String country, String state, String city) {
        this(country, Optional.ofNullable(state), Optional.ofNullable(city));
    }
    
    /**
     * Returns true if this is a national-level location (country only).
     */
    public boolean isNational() {
        return state.isEmpty() && city.isEmpty();
    }
    
    /**
     * Returns true if this is a state-level location.
     */
    public boolean isState() {
        return state.isPresent() && city.isEmpty();
    }
    
    /**
     * Returns true if this is a city-level location.
     */
    public boolean isCity() {
        return state.isPresent() && city.isPresent();
    }
}
