package me.clementino.holiday.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * MongoDB document representing a holiday.
 * Uses String ID for simplicity and compatibility.
 */
@Document("holidays")
public class Holiday {

    @Id
    private String id;  // Simple String ID

    @NotNull
    @Size(max = 255)
    private String name;

    @NotNull
    private LocalDate date;

    private LocalDate observed;

    @NotNull
    @Size(max = 255)
    private String country;

    @Size(max = 255)
    private String state;

    @Size(max = 255)
    private String city;

    @NotNull
    private HolidayType type;

    private boolean recurring;

    @Size(max = 1000)
    private String description;

    @CreatedDate
    private LocalDateTime dateCreated;  // Changed to LocalDateTime

    @LastModifiedDate
    private LocalDateTime lastUpdated;  // Changed to LocalDateTime

    @Version
    private Integer version;

    // Default constructor for MongoDB
    public Holiday() {}

    // Constructor with required fields
    public Holiday(String name, LocalDate date, String country, HolidayType type) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.date = Objects.requireNonNull(date, "Date cannot be null");
        this.country = Objects.requireNonNull(country, "Country cannot be null");
        this.type = Objects.requireNonNull(type, "Type cannot be null");
        this.recurring = false;
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

    /**
     * Helper method to get location as a Location record.
     */
    public Location getLocation() {
        return new Location(country, 
                           Optional.ofNullable(state), 
                           Optional.ofNullable(city));
    }

    /**
     * Helper method to set location from a Location record.
     */
    public void setLocation(Location location) {
        this.country = location.country();
        this.state = location.state().orElse(null);
        this.city = location.city().orElse(null);
    }
}
