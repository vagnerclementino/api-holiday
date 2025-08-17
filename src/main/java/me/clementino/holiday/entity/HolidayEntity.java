package me.clementino.holiday.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;
import me.clementino.holiday.domain.dop.HolidayType;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Immutable Holiday entity for MongoDB persistence.
 *
 * <p>This entity follows immutability principles: - All fields are final - No setters, only "with"
 * methods that return new instances - Auto-generation of ID and timestamps when missing - Defensive
 * copying for mutable fields
 */
@Document(collection = "holidays")
public final class HolidayEntity {

  @Id private final String id;
  private final String name;
  private final LocalDate date;
  private final LocalDate observedDate;
  private final String country;
  private final String state;
  private final String city;
  private final HolidayType type;
  private final boolean recurring;
  private final String description;
  private final LocalDateTime dateCreated;
  private final LocalDateTime lastUpdated;

  /** Constructor for creating new entities (auto-generates ID and timestamps). */
  public HolidayEntity(
      String name,
      LocalDate date,
      LocalDate observedDate,
      String country,
      String state,
      String city,
      HolidayType type,
      boolean recurring,
      String description) {
    this(
        UUID.randomUUID().toString(),
        name,
        date,
        observedDate,
        country,
        state,
        city,
        type,
        recurring,
        description,
        LocalDateTime.now(),
        LocalDateTime.now());
  }

  /** Full constructor for all fields. */
  public HolidayEntity(
      String id,
      String name,
      LocalDate date,
      LocalDate observedDate,
      String country,
      String state,
      String city,
      HolidayType type,
      boolean recurring,
      String description,
      LocalDateTime dateCreated,
      LocalDateTime lastUpdated) {

    this.id = id;
    this.name = Objects.requireNonNull(name, "Name cannot be null");
    this.date = Objects.requireNonNull(date, "Date cannot be null");
    this.observedDate = observedDate;
    this.country = Objects.requireNonNull(country, "Country cannot be null");
    this.state = state;
    this.city = city;
    this.type = Objects.requireNonNull(type, "Type cannot be null");
    this.recurring = recurring;
    this.description = description;
    this.dateCreated = dateCreated != null ? dateCreated : LocalDateTime.now();
    this.lastUpdated = lastUpdated != null ? lastUpdated : LocalDateTime.now();

    // Validation
    if (name.isBlank()) {
      throw new IllegalArgumentException("Name cannot be blank");
    }
    if (country.isBlank()) {
      throw new IllegalArgumentException("Country cannot be blank");
    }
  }

  // Getters
  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public LocalDate getDate() {
    return date;
  }

  public LocalDate getObservedDate() {
    return observedDate;
  }

  public String getCountry() {
    return country;
  }

  public String getState() {
    return state;
  }

  public String getCity() {
    return city;
  }

  public HolidayType getType() {
    return type;
  }

  public boolean isRecurring() {
    return recurring;
  }

  public String getDescription() {
    return description;
  }

  public LocalDateTime getDateCreated() {
    return dateCreated;
  }

  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }

  // Immutable update methods
  public HolidayEntity withId(String newId) {
    return new HolidayEntity(
        newId,
        name,
        date,
        observedDate,
        country,
        state,
        city,
        type,
        recurring,
        description,
        dateCreated,
        lastUpdated);
  }

  public HolidayEntity withName(String newName) {
    return new HolidayEntity(
        id,
        newName,
        date,
        observedDate,
        country,
        state,
        city,
        type,
        recurring,
        description,
        dateCreated,
        LocalDateTime.now());
  }

  public HolidayEntity withDate(LocalDate newDate) {
    return new HolidayEntity(
        id,
        name,
        newDate,
        observedDate,
        country,
        state,
        city,
        type,
        recurring,
        description,
        dateCreated,
        LocalDateTime.now());
  }

  public HolidayEntity withObservedDate(LocalDate newObservedDate) {
    return new HolidayEntity(
        id,
        name,
        date,
        newObservedDate,
        country,
        state,
        city,
        type,
        recurring,
        description,
        dateCreated,
        LocalDateTime.now());
  }

  public HolidayEntity withCountry(String newCountry) {
    return new HolidayEntity(
        id,
        name,
        date,
        observedDate,
        newCountry,
        state,
        city,
        type,
        recurring,
        description,
        dateCreated,
        LocalDateTime.now());
  }

  public HolidayEntity withState(String newState) {
    return new HolidayEntity(
        id,
        name,
        date,
        observedDate,
        country,
        newState,
        city,
        type,
        recurring,
        description,
        dateCreated,
        LocalDateTime.now());
  }

  public HolidayEntity withCity(String newCity) {
    return new HolidayEntity(
        id,
        name,
        date,
        observedDate,
        country,
        state,
        newCity,
        type,
        recurring,
        description,
        dateCreated,
        LocalDateTime.now());
  }

  public HolidayEntity withType(HolidayType newType) {
    return new HolidayEntity(
        id,
        name,
        date,
        observedDate,
        country,
        state,
        city,
        newType,
        recurring,
        description,
        dateCreated,
        LocalDateTime.now());
  }

  public HolidayEntity withRecurring(boolean newRecurring) {
    return new HolidayEntity(
        id,
        name,
        date,
        observedDate,
        country,
        state,
        city,
        type,
        newRecurring,
        description,
        dateCreated,
        LocalDateTime.now());
  }

  public HolidayEntity withDescription(String newDescription) {
    return new HolidayEntity(
        id,
        name,
        date,
        observedDate,
        country,
        state,
        city,
        type,
        recurring,
        newDescription,
        dateCreated,
        LocalDateTime.now());
  }

  public HolidayEntity withLastUpdated(LocalDateTime newLastUpdated) {
    return new HolidayEntity(
        id,
        name,
        date,
        observedDate,
        country,
        state,
        city,
        type,
        recurring,
        description,
        dateCreated,
        newLastUpdated);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    HolidayEntity that = (HolidayEntity) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "HolidayEntity{"
        + "id='"
        + id
        + '\''
        + ", name='"
        + name
        + '\''
        + ", date="
        + date
        + ", country='"
        + country
        + '\''
        + ", type="
        + type
        + '}';
  }
}
