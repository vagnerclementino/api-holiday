package me.clementino.holiday.entity;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import me.clementino.holiday.domain.HolidayType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * MongoDB document representing a holiday entity for persistence. Enhanced to support year-based
 * calculations, caching, and DOP sealed interface serialization.
 *
 * <p>This entity is designed to persist DOP Holiday sealed interface variants while providing
 * efficient querying capabilities through proper indexing and embedded documents.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li>Year-based holiday calculations and caching
 *   <li>Support for DOP sealed interface serialization
 *   <li>Comprehensive MongoDB indexing for performance
 *   <li>Locality hierarchy support with embedded documents
 *   <li>Holiday variant serialization for complex types
 *   <li>Custom converters for sealed interface persistence
 * </ul>
 *
 * <p><strong>Indexing Strategy:</strong>
 *
 * <ul>
 *   <li>Country + Year: For efficient year-based queries
 *   <li>Locality + Type: For hierarchical locality filtering
 *   <li>Date + Year: For date range queries
 *   <li>Base Holiday + Year: For derived holiday lookups
 *   <li>Calculated Holidays: For cache management
 * </ul>
 */
@Document("holidays")
@CompoundIndexes({
  @CompoundIndex(name = "country_year_idx", def = "{'country': 1, 'year': 1}"),
  @CompoundIndex(
      name = "locality_type_idx",
      def = "{'country': 1, 'state': 1, 'city': 1, 'type': 1}"),
  @CompoundIndex(name = "date_range_idx", def = "{'date': 1, 'year': 1}"),
  @CompoundIndex(name = "base_holiday_year_idx", def = "{'baseHolidayId': 1, 'year': 1}"),
  @CompoundIndex(
      name = "calculated_holidays_idx",
      def = "{'isCalculated': 1, 'year': 1, 'country': 1}"),
  @CompoundIndex(name = "holiday_variant_idx", def = "{'holidayVariant': 1, 'country': 1}"),
  @CompoundIndex(name = "recurring_holidays_idx", def = "{'recurring': 1, 'country': 1, 'type': 1}")
})
public class HolidayEntity {

  @Id
  private String id;

  @NotNull
  @Size(max = 255)
  @Indexed
  private String name;

  @NotNull
  @Size(max = 1000)
  private String description;

  @NotNull @Indexed
  private LocalDate date;

  @NotNull
  @Indexed
  private HolidayType type;

  @NotNull
  private List<LocalityEntity> localities;

  private LocalDate observed; // Observed date (different from actual date due to mondayisation)

  @CreatedDate private LocalDateTime dateCreated;

  @LastModifiedDate private LocalDateTime lastUpdated;

  @Version private Integer version; // Optimistic locking

  // Default constructor for MongoDB
  public HolidayEntity() {}

  // Constructor with required fields
  public HolidayEntity(
      String name, String description, LocalDate date, String country, HolidayType type) {
    this.name = Objects.requireNonNull(name, "Name cannot be null");
    this.description = description;
    this.date = Objects.requireNonNull(date, "Date cannot be null");
    this.type = Objects.requireNonNull(type, "Type cannot be null");
  }

  // Enhanced constructor for calculated holiday instances
  public HolidayEntity(
      String name,
      String description,
      LocalDate date,
      String country,
      HolidayType type,
      Integer year,
      boolean isCalculated) {
    this(name, description, date, country, type);
  }

  // Constructor for derived holidays with base holiday reference
  public HolidayEntity(
      String name,
      String description,
      LocalDate date,
      String country,
      HolidayType type,
      Integer year) {
    this(name, description, date, country, type, year, true);
  }

  /**
   * Gets the effective date for this holiday (observed date if available, otherwise actual date).
   *
   * @return the effective date
   */
  public LocalDate getEffectiveDate() {
    return observed != null ? observed : date;
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public HolidayType getType() {
    return type;
  }

  public void setType(HolidayType type) {
    this.type = type;
  }

  public List<LocalityEntity> getLocalities() {
    return localities;
  }

  public void setLocalities(List<LocalityEntity> localities) {
    this.localities = localities;
  }

  public LocalDateTime getDateCreated() {
    return dateCreated;
  }

  public void setDateCreated(LocalDateTime dateCreated) {
    this.dateCreated = dateCreated;
  }

  public LocalDateTime getLastUpdated() {
    return lastUpdated;
  }

  public void setLastUpdated(LocalDateTime lastUpdated) {
    this.lastUpdated = lastUpdated;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }
}
