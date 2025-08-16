package me.clementino.holiday.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import me.clementino.holiday.domain.HolidayType;

/**
 * Simple DTO for creating holidays that matches the current API structure.
 *
 * <p>This record demonstrates DOP principles:
 *
 * <ul>
 *   <li>Model Data Immutably and Transparently - immutable record structure
 *   <li>Model the Data, the Whole Data, and Nothing but the Data - contains exactly what's needed
 *       for creation
 * </ul>
 */
public record CreateHolidayRequest(
    @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must not exceed 255 characters")
        String name,
    @NotNull(message = "Date is required") LocalDate date,
    LocalDate observed,
    @NotBlank(message = "Country is required")
        @Size(max = 255, message = "Country must not exceed 255 characters")
        String country,
    @Size(max = 255, message = "State must not exceed 255 characters") String state,
    @Size(max = 255, message = "City must not exceed 255 characters") String city,
    @NotNull(message = "Holiday type is required") HolidayType type,
    Boolean recurring,
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
        String description) {}
