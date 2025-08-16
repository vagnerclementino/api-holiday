package me.clementino.holiday.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
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
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li>Year-based holiday calculations and caching
 *   <li>Support for DOP sealed interface serialization
 *   <li>Comprehensive MongoDB indexing for performance
 *   <li>Locality hierarchy support with embedded documents
 *   <li>Holiday variant serialization for complex types
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
  @CompoundIndex(name = "holiday_variant_idx", def = "{'holidayVariant': 1, 'country': 1}")
})
public class Holiday {

  @Id private String id; // Simple String ID

  @NotNull
  @Size(max = 255)
  @Indexed
  private String name;

  @NotNull @Indexed private LocalDate date;

  private LocalDate observed;

  @NotNull
  @Size(max = 255)
  @Indexed
  private String country;

  @Size(max = 255)
  @Indexed
  private String state;

  @Size(max = 255)
  @Indexed
  private String city;

  @NotNull @Indexed private HolidayType type;

  private boolean recurring;

  @Size(max = 1000)
  private String description;

  // ===== ENHANCED FIELDS FOR YEAR-BASED CALCULATIONS AND CACHING =====

  @Indexed private Integer year; // Year for which this holiday instance was calculated

  @Indexed private boolean isCalculated; // Flag indicating if this is a calculated instance

  @Size(max = 500)
  private String calculationRule; // Rule used for calculation (for moveable holidays)

  private Integer dayOffset; // Day offset from base holiday (for derived holidays)

  private boolean mondayisation; // Whether mondayisation rules apply

  @Indexed private String baseHolidayId; // Reference to base holiday for derived holidays

  // ===== DOP SEALED INTERFACE SERIALIZATION SUPPORT =====

  @Size(max = 50)
  @Indexed
  private String holidayVariant; // Type of holiday variant (FixedHoliday, MoveableHoliday, etc.)

  @Size(max = 2000)
  private String localityData; // Serialized DOP Locality sealed interface data

  // Embedded locality entity for complex queries
  private LocalityEntity locality;

  // ===== METADATA FIELDS =====

  @CreatedDate private LocalDateTime dateCreated;

  @LastModifiedDate private LocalDateTime lastUpdated;

  @Version private Integer version; // Re-enabled version for optimistic locking

  // Default constructor for MongoDB
  public Holiday() {}

  // Constructor with required fields
  public Holiday(String name, LocalDate date, String country, HolidayType type) {
    this.name = Objects.requireNonNull(name, "Name cannot be null");
    this.date = Objects.requireNonNull(date, "Date cannot be null");
    this.country = Objects.requireNonNull(country, "Country cannot be null");
    this.type = Objects.requireNonNull(type, "Type cannot be null");
    this.recurring = false;
    this.isCalculated = false;
    this.mondayisation = false;
  }

  // Enhanced constructor for calculated holiday instances
  public Holiday(
      String name,
      LocalDate date,
      String country,
      HolidayType type,
      Integer year,
      boolean isCalculated) {
    this(name, date, country, type);
    this.year = year;
    this.isCalculated = isCalculated;
  }

  // Constructor for derived holidays with base holiday reference
  public Holiday(
      String name,
      LocalDate date,
      String country,
      HolidayType type,
      Integer year,
      String baseHolidayId,
      Integer dayOffset) {
    this(name, date, country, type, year, true);
    this.baseHolidayId = baseHolidayId;
    this.dayOffset = dayOffset;
  }

  // Getters and setters
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

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }

  public LocalDate getObserved() {
    return observed;
  }

  public void setObserved(LocalDate observed) {
    this.observed = observed;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public HolidayType getType() {
    return type;
  }

  public void setType(HolidayType type) {
    this.type = type;
  }

  public boolean isRecurring() {
    return recurring;
  }

  public void setRecurring(boolean recurring) {
    this.recurring = recurring;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
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

  // ===== GETTERS AND SETTERS FOR ENHANCED FIELDS =====

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  public boolean isCalculated() {
    return isCalculated;
  }

  public void setCalculated(boolean calculated) {
    isCalculated = calculated;
  }

  public String getCalculationRule() {
    return calculationRule;
  }

  public void setCalculationRule(String calculationRule) {
    this.calculationRule = calculationRule;
  }

  public Integer getDayOffset() {
    return dayOffset;
  }

  public void setDayOffset(Integer dayOffset) {
    this.dayOffset = dayOffset;
  }

  public boolean isMondayisation() {
    return mondayisation;
  }

  public void setMondayisation(boolean mondayisation) {
    this.mondayisation = mondayisation;
  }

  public String getBaseHolidayId() {
    return baseHolidayId;
  }

  public void setBaseHolidayId(String baseHolidayId) {
    this.baseHolidayId = baseHolidayId;
  }

  public String getHolidayVariant() {
    return holidayVariant;
  }

  public void setHolidayVariant(String holidayVariant) {
    this.holidayVariant = holidayVariant;
  }

  public String getLocalityData() {
    return localityData;
  }

  public void setLocalityData(String localityData) {
    this.localityData = localityData;
  }

  public LocalityEntity getLocality() {
    return locality;
  }

  public void setLocality(LocalityEntity locality) {
    this.locality = locality;
  }

  /** Helper method to get location as a Location record. */
  public Location getLocation() {
    return new Location(country, Optional.ofNullable(state), Optional.ofNullable(city));
  }

  /** Helper method to set location from a Location record. */
  public void setLocation(Location location) {
    this.country = location.country();
    this.state = location.state().orElse(null);
    this.city = location.city().orElse(null);
  }

  /** Helper method to get LocalityEntity from current location fields. */
  public LocalityEntity getLocalityEntity() {
    if (locality != null) {
      return locality;
    }

    // Create LocalityEntity from current fields
    if (city != null && !city.isBlank()) {
      return new LocalityEntity(country, country, state, state, city);
    } else if (state != null && !state.isBlank()) {
      return new LocalityEntity(country, country, state, state);
    } else {
      return new LocalityEntity(country, country);
    }
  }

  /** Helper method to set locality from LocalityEntity. */
  public void setLocalityEntity(LocalityEntity localityEntity) {
    this.locality = localityEntity;

    // Sync with traditional fields for backward compatibility
    if (localityEntity != null) {
      this.country = localityEntity.getCountryCode();
      this.state = localityEntity.getSubdivisionCode();
      this.city = localityEntity.getCityName();
    }
  }

  // ===== BUSINESS LOGIC HELPER METHODS =====

  /**
   * Checks if this holiday is a base holiday (not derived from another holiday).
   *
   * @return true if this is a base holiday, false if derived
   */
  public boolean isBaseHoliday() {
    return baseHolidayId == null || baseHolidayId.isBlank();
  }

  /**
   * Checks if this holiday is a derived holiday (calculated from a base holiday).
   *
   * @return true if this is a derived holiday, false if base
   */
  public boolean isDerivedHoliday() {
    return !isBaseHoliday();
  }

  /**
   * Checks if this holiday has mondayisation rules applied.
   *
   * @return true if mondayisation is enabled
   */
  public boolean hasMondayisation() {
    return mondayisation;
  }

  /**
   * Gets the effective date for this holiday (observed date if available, otherwise actual date).
   *
   * @return the effective date
   */
  public LocalDate getEffectiveDate() {
    return observed != null ? observed : date;
  }

  /**
   * Checks if this holiday applies to a specific locality level.
   *
   * @param checkCountry the country to check
   * @param checkState the state to check (optional)
   * @param checkCity the city to check (optional)
   * @return true if the holiday applies to the specified locality
   */
  public boolean appliesTo(String checkCountry, String checkState, String checkCity) {
    if (!Objects.equals(this.country, checkCountry)) {
      return false;
    }

    // If holiday is national level, it applies everywhere in the country
    if (this.state == null || this.state.isBlank()) {
      return true;
    }

    // If holiday is state level, check state match
    if (!Objects.equals(this.state, checkState)) {
      return false;
    }

    // If holiday is city level, check city match
    if (this.city != null && !this.city.isBlank()) {
      return Objects.equals(this.city, checkCity);
    }

    // State level holiday applies to all cities in the state
    return true;
  }
}
