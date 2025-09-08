package me.clementino.holiday.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import me.clementino.holiday.domain.dop.HolidayType;

/**
 * Simple DTO for updating holidays using nullable fields for partial updates.
 *
 * <p>This record demonstrates DOP principles by using nullable fields instead of Optional for
 * simpler serialization and partial updates. Only non-null fields will be updated.
 */
public record UpdateHolidayRequestDTO(
    @Size(max = 255, message = "Name must not exceed 255 characters") String name,
    LocalDate date,
    LocalDate observed,
    @Size(max = 255, message = "Country must not exceed 255 characters") String country,
    @Size(max = 255, message = "State must not exceed 255 characters") String state,
    @Size(max = 255, message = "City must not exceed 255 characters") String city,
    HolidayType type,
    Boolean recurring,
    @Size(max = 1000, message = "Description must not exceed 1000 characters") String description) {

  /** Creates an empty update request with all fields set to null. */
  public static UpdateHolidayRequestDTO empty() {
    return new UpdateHolidayRequestDTO(null, null, null, null, null, null, null, null, null);
  }
}
