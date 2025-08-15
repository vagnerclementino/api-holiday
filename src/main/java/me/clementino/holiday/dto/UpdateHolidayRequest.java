package me.clementino.holiday.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayType;

/**
 * Request DTO for updating an existing holiday using Java 24 record. All fields are optional for
 * partial updates.
 *
 * <p>This record demonstrates DOP principles:
 *
 * <ul>
 *   <li>Model Data Immutably and Transparently - immutable record structure
 *   <li>Model the Data, the Whole Data, and Nothing but the Data - contains exactly what's needed
 *       for updates
 * </ul>
 */
public record UpdateHolidayRequest(
    @Size(max = 255, message = "Name must not exceed 255 characters") Optional<String> name,
    @Size(max = 1000, message = "Description must not exceed 1000 characters")
        Optional<String> description,
    Optional<HolidayType> type,
    @Valid Optional<CreateHolidayRequest.LocalityDto> locality,
    @Valid Optional<CreateHolidayRequest.HolidayVariantDto> variant) {

  /** Creates an empty update request with all fields set to Optional.empty(). */
  public static UpdateHolidayRequest empty() {
    return new UpdateHolidayRequest(
        Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
  }

  /** Checks if this update request has any fields to update. */
  public boolean hasUpdates() {
    return name.isPresent()
        || description.isPresent()
        || type.isPresent()
        || locality.isPresent()
        || variant.isPresent();
  }
}
