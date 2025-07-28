package me.clementino.holiday.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Size;
import me.clementino.holiday.domain.HolidayType;

import java.time.LocalDate;

/**
 * Request DTO for updating an existing holiday.
 * All fields are optional for partial updates.
 */
public class UpdateHolidayRequest {

    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate observed;

    @Size(max = 255, message = "Country must not exceed 255 characters")
    private String country;

    @Size(max = 255, message = "State must not exceed 255 characters")
    private String state;

    @Size(max = 255, message = "City must not exceed 255 characters")
    private String city;

    private HolidayType type;

    private boolean recurring = false;

    @Size(max = 1000, message = "Description must not exceed 1000 characters")
    private String description;

    // Default constructor
    public UpdateHolidayRequest() {}

    // Getters and setters
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
}
