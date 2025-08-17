package me.clementino.holiday.domain.dop;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Holiday domain record following DOP principles.
 *
 * <p>This record represents the core business data for a holiday, following Data-Oriented
 * Programming principles: - Immutable data structure - Transparent data access - No behavior mixed
 * with data - Pure data representation
 */
public record Holiday(
    String id,
    String name,
    LocalDate date,
    Optional<LocalDate> observedDate,
    Location location,
    HolidayType type,
    boolean recurring,
    Optional<String> description,
    Optional<LocalDateTime> dateCreated,
    Optional<LocalDateTime> lastUpdated) {

  /** Compact constructor for validation. */
  public Holiday {
    Objects.requireNonNull(name, "Holiday name cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(location, "Holiday location cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");

    if (name.isBlank()) {
      throw new IllegalArgumentException("Holiday name cannot be blank");
    }
  }

  /**
   * Create a new Holiday with updated name.
   *
   * @param newName the new name
   * @return new Holiday instance with updated name
   */
  public Holiday withName(String newName) {
    return new Holiday(
        id,
        newName,
        date,
        observedDate,
        location,
        type,
        recurring,
        description,
        dateCreated,
        lastUpdated);
  }

  /**
   * Create a new Holiday with updated date.
   *
   * @param newDate the new date
   * @return new Holiday instance with updated date
   */
  public Holiday withDate(LocalDate newDate) {
    return new Holiday(
        id,
        name,
        newDate,
        observedDate,
        location,
        type,
        recurring,
        description,
        dateCreated,
        lastUpdated);
  }

  /**
   * Create a new Holiday with updated observed date.
   *
   * @param newObservedDate the new observed date
   * @return new Holiday instance with updated observed date
   */
  public Holiday withObservedDate(Optional<LocalDate> newObservedDate) {
    return new Holiday(
        id,
        name,
        date,
        newObservedDate,
        location,
        type,
        recurring,
        description,
        dateCreated,
        lastUpdated);
  }

  /**
   * Create a new Holiday with updated location.
   *
   * @param newLocation the new location
   * @return new Holiday instance with updated location
   */
  public Holiday withLocation(Location newLocation) {
    return new Holiday(
        id,
        name,
        date,
        observedDate,
        newLocation,
        type,
        recurring,
        description,
        dateCreated,
        lastUpdated);
  }

  /**
   * Create a new Holiday with updated type.
   *
   * @param newType the new type
   * @return new Holiday instance with updated type
   */
  public Holiday withType(HolidayType newType) {
    return new Holiday(
        id,
        name,
        date,
        observedDate,
        location,
        newType,
        recurring,
        description,
        dateCreated,
        lastUpdated);
  }

  /**
   * Create a new Holiday with updated recurring flag.
   *
   * @param newRecurring the new recurring flag
   * @return new Holiday instance with updated recurring flag
   */
  public Holiday withRecurring(boolean newRecurring) {
    return new Holiday(
        id,
        name,
        date,
        observedDate,
        location,
        type,
        newRecurring,
        description,
        dateCreated,
        lastUpdated);
  }

  /**
   * Create a new Holiday with updated description.
   *
   * @param newDescription the new description
   * @return new Holiday instance with updated description
   */
  public Holiday withDescription(Optional<String> newDescription) {
    return new Holiday(
        id,
        name,
        date,
        observedDate,
        location,
        type,
        recurring,
        newDescription,
        dateCreated,
        lastUpdated);
  }

  /**
   * Create a new Holiday with updated ID.
   *
   * @param newId the new ID
   * @return new Holiday instance with updated ID
   */
  public Holiday withId(String newId) {
    return new Holiday(
        newId,
        name,
        date,
        observedDate,
        location,
        type,
        recurring,
        description,
        dateCreated,
        lastUpdated);
  }

  /**
   * Create a new Holiday with updated creation timestamp.
   *
   * @param newDateCreated the new creation timestamp
   * @return new Holiday instance with updated creation timestamp
   */
  public Holiday withDateCreated(Optional<LocalDateTime> newDateCreated) {
    return new Holiday(
        id,
        name,
        date,
        observedDate,
        location,
        type,
        recurring,
        description,
        newDateCreated,
        lastUpdated);
  }

  /**
   * Create a new Holiday with updated last update timestamp.
   *
   * @param newLastUpdated the new last update timestamp
   * @return new Holiday instance with updated last update timestamp
   */
  public Holiday withLastUpdated(Optional<LocalDateTime> newLastUpdated) {
    return new Holiday(
        id,
        name,
        date,
        observedDate,
        location,
        type,
        recurring,
        description,
        dateCreated,
        newLastUpdated);
  }
}
