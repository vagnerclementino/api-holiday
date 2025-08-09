package me.clementino.holiday.dto;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.domain.Location;

/**
 * Pure DOP record representing a holiday. Following DOP principles: 1. Model data immutably and
 * transparently 2. Model the data, the whole data, and nothing but the data 3. Make illegal states
 * unrepresentable
 */
public record HolidayRecord(
    UUID id,
    String name,
    LocalDate date,
    Optional<LocalDate> observed,
    Location location,
    HolidayType type,
    boolean recurring,
    Optional<String> description) {
  // Compact constructor for validation
  public HolidayRecord {
    Objects.requireNonNull(name, "Holiday name cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(location, "Holiday location cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");

    if (name.isBlank()) {
      throw new IllegalArgumentException("Holiday name cannot be blank");
    }

    // Ensure Optional fields are never null
    observed = Objects.requireNonNullElse(observed, Optional.empty());
    description = Objects.requireNonNullElse(description, Optional.empty());
  }

  /** Constructor without ID (for creation). */
  public HolidayRecord(
      String name,
      LocalDate date,
      Location location,
      HolidayType type,
      boolean recurring,
      Optional<String> description) {
    this(null, name, date, Optional.empty(), location, type, recurring, description);
  }

  /** Transformation method to create a new instance with different name. */
  public HolidayRecord withName(String newName) {
    return new HolidayRecord(id, newName, date, observed, location, type, recurring, description);
  }

  /** Transformation method to create a new instance with different date. */
  public HolidayRecord withDate(LocalDate newDate) {
    return new HolidayRecord(id, name, newDate, observed, location, type, recurring, description);
  }

  /** Transformation method to create a new instance with observed date. */
  public HolidayRecord withObserved(LocalDate observedDate) {
    return new HolidayRecord(
        id, name, date, Optional.ofNullable(observedDate), location, type, recurring, description);
  }

  /** Transformation method to create a new instance with different location. */
  public HolidayRecord withLocation(Location newLocation) {
    return new HolidayRecord(id, name, date, observed, newLocation, type, recurring, description);
  }

  /** Transformation method to create a new instance with different type. */
  public HolidayRecord withType(HolidayType newType) {
    return new HolidayRecord(id, name, date, observed, location, newType, recurring, description);
  }

  /** Transformation method to create a new instance with different recurring status. */
  public HolidayRecord withRecurring(boolean newRecurring) {
    return new HolidayRecord(id, name, date, observed, location, type, newRecurring, description);
  }

  /** Transformation method to create a new instance with different description. */
  public HolidayRecord withDescription(String newDescription) {
    return new HolidayRecord(
        id, name, date, observed, location, type, recurring, Optional.ofNullable(newDescription));
  }

  /** Transformation method to create a new instance with ID (after persistence). */
  public HolidayRecord withId(UUID newId) {
    return new HolidayRecord(newId, name, date, observed, location, type, recurring, description);
  }
}
