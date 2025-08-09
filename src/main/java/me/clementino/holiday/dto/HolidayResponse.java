package me.clementino.holiday.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import me.clementino.holiday.domain.HolidayType;

/** Response DTO for holiday data. Simple POJO for JSON serialization. */
public class HolidayResponse {

  private String id; // Changed from UUID to String
  private String name;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate observed;

  private String country;
  private String state;
  private String city;
  private HolidayType type;
  private boolean recurring;
  private String description;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime dateCreated; // Changed to LocalDateTime

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private LocalDateTime lastUpdated; // Changed to LocalDateTime

  private Integer version;

  // Default constructor
  public HolidayResponse() {}

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
}
