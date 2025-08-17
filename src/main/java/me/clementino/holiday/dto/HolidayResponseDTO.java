package me.clementino.holiday.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import me.clementino.holiday.domain.dop.HolidayType;

/**
 * Comprehensive response DTO for holidays following DOP principles.
 *
 * <p>This record represents a complete holiday with all relevant information including when it
 * occurs, when it's observed, and where it applies. The structure is immutable and transparent,
 * containing exactly the data needed for API responses.
 *
 * <p>Null values are excluded from JSON serialization to keep responses clean.
 */
@Schema(description = "Complete holiday information")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record HolidayResponseDTO(
    @Schema(description = "Unique holiday identifier", example = "holiday-123") String id,
    @Schema(description = "Holiday name", example = "Christmas Day") String name,
    @Schema(description = "When the holiday occurs (date and weekday)", required = true)
        WhenInfo when,
    @Schema(description = "When the holiday is observed (if different from actual date)")
        WhenInfo observed,
    @Schema(description = "List of locations where this holiday applies", required = true)
        List<LocationInfo> where,
    @Schema(description = "Type of holiday", example = "NATIONAL") HolidayType type,
    @Schema(
            description = "Additional description or notes",
            example = "Christian holiday celebrating the birth of Jesus Christ")
        String description,
    @Schema(description = "When this holiday record was created", example = "2024-01-15T10:30:00")
        String created,
    @Schema(
            description = "When this holiday record was last updated",
            example = "2024-01-15T10:30:00")
        String updated) {

  /** Compact constructor for validation. */
  public HolidayResponseDTO {
    // Ensure required fields are not null
    if (when == null) {
      throw new IllegalArgumentException("'when' information is required");
    }

    if (where == null || where.isEmpty()) {
      throw new IllegalArgumentException("At least one location must be specified");
    }

    // Ensure all locations have at least a country
    where.forEach(
        location -> {
          if (location.country() == null || location.country().isBlank()) {
            throw new IllegalArgumentException("Each location must have a country");
          }
        });
  }
}
