package me.clementino.holiday.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import me.clementino.holiday.domain.dop.HolidayType;
import me.clementino.holiday.domain.dop.Location;

/**
 * Immutable record representing holiday data. Following DOP v1.1 Principle 1: Model Data Immutably
 * and Transparently. This record is: - Immutable: All fields are final, no setters - Transparent:
 * All data is accessible through accessor methods - Thread-safe: Immutability guarantees thread
 * safety - Predictable: Operations return new instances
 */
public record HolidayDataDTO(
    String id,
    String name,
    LocalDate date,
    Optional<LocalDate> observed,
    Location location,
    HolidayType type,
    boolean recurring,
    Optional<String> description,
    Optional<LocalDateTime> dateCreated,
    Optional<LocalDateTime> lastUpdated,
    Optional<Integer> version) {
  public HolidayDataDTO {
    Objects.requireNonNull(name, "Holiday name cannot be null");
    if (name.isBlank()) {
      throw new IllegalArgumentException("Holiday name cannot be blank");
    }

    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(location, "Holiday location cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");

    observed = Objects.requireNonNullElse(observed, Optional.empty());
    description = Objects.requireNonNullElse(description, Optional.empty());
    dateCreated = Objects.requireNonNullElse(dateCreated, Optional.empty());
    lastUpdated = Objects.requireNonNullElse(lastUpdated, Optional.empty());
    version = Objects.requireNonNullElse(version, Optional.empty());
  }

  /** Convenience constructor for creating new holidays (without persistence metadata). */
  public HolidayDataDTO(
      String name, LocalDate date, Location location, HolidayType type, boolean recurring) {
    this(
        null,
        name,
        date,
        Optional.empty(),
        location,
        type,
        recurring,
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
  }

  /** Convenience constructor with description. */
  public HolidayDataDTO(
      String name,
      LocalDate date,
      Location location,
      HolidayType type,
      boolean recurring,
      String description) {
    this(
        null,
        name,
        date,
        Optional.empty(),
        location,
        type,
        recurring,
        Optional.ofNullable(description),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
  }

  public HolidayDataDTO withId(String newId) {
    return new HolidayDataDTO(
        newId,
        name,
        date,
        observed,
        location,
        type,
        recurring,
        description,
        dateCreated,
        lastUpdated,
        version);
  }

  public HolidayDataDTO withName(String newName) {
    return new HolidayDataDTO(
        id,
        newName,
        date,
        observed,
        location,
        type,
        recurring,
        description,
        dateCreated,
        lastUpdated,
        version);
  }

  public HolidayDataDTO withDate(LocalDate newDate) {
    return new HolidayDataDTO(
        id,
        name,
        newDate,
        observed,
        location,
        type,
        recurring,
        description,
        dateCreated,
        lastUpdated,
        version);
  }

  public HolidayDataDTO withObserved(LocalDate observedDate) {
    return new HolidayDataDTO(
        id,
        name,
        date,
        Optional.ofNullable(observedDate),
        location,
        type,
        recurring,
        description,
        dateCreated,
        lastUpdated,
        version);
  }

  public HolidayDataDTO withLocation(Location newLocation) {
    return new HolidayDataDTO(
        id,
        name,
        date,
        observed,
        newLocation,
        type,
        recurring,
        description,
        dateCreated,
        lastUpdated,
        version);
  }

  public HolidayDataDTO withType(HolidayType newType) {
    return new HolidayDataDTO(
        id,
        name,
        date,
        observed,
        location,
        newType,
        recurring,
        description,
        dateCreated,
        lastUpdated,
        version);
  }

  public HolidayDataDTO withRecurring(boolean isRecurring) {
    return new HolidayDataDTO(
        id,
        name,
        date,
        observed,
        location,
        type,
        isRecurring,
        description,
        dateCreated,
        lastUpdated,
        version);
  }

  public HolidayDataDTO withDescription(String newDescription) {
    return new HolidayDataDTO(
        id,
        name,
        date,
        observed,
        location,
        type,
        recurring,
        Optional.ofNullable(newDescription),
        dateCreated,
        lastUpdated,
        version);
  }

  public HolidayDataDTO withMetadata(LocalDateTime created, LocalDateTime updated, Integer ver) {
    return new HolidayDataDTO(
        id,
        name,
        date,
        observed,
        location,
        type,
        recurring,
        description,
        Optional.ofNullable(created),
        Optional.ofNullable(updated),
        Optional.ofNullable(ver));
  }

  public boolean isNational() {
    return location.isNational();
  }

  public boolean isState() {
    return location.isState();
  }

  public boolean isCity() {
    return location.isCity();
  }

  public boolean hasObservedDate() {
    return observed.isPresent();
  }

  public boolean hasDescription() {
    return description.isPresent() && !description.get().isBlank();
  }

  public boolean isGovernmental() {
    return type.isGovernmental();
  }
}
