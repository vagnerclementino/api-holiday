package me.clementino.holiday.dto;

import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayType;

/**
 * Simple DTO for updating holidays.
 *
 * <p>This record demonstrates DOP principles by using Optional for partial updates.
 */
public record UpdateHolidayRequest(
    @Size(max = 255, message = "Name must not exceed 255 characters") Optional<String> name,
    Optional<LocalDate> date,
    Optional<LocalDate> observed,
    @Size(max = 255, message = "Country must not exceed 255 characters") Optional<String> country,
    @Size(max = 255, message = "State must not exceed 255 characters") Optional<String> state,
    @Size(max = 255, message = "City must not exceed 255 characters") Optional<String> city,
    Optional<HolidayType> type,
    Optional<Boolean> recurring,
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
        Optional<String> description) {

  /** Creates an empty update request with all fields set to Optional.empty(). */
  public static UpdateHolidayRequest empty() {
    return new UpdateHolidayRequest(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
  }
}
