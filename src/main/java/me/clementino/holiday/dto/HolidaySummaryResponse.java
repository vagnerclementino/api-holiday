package me.clementino.holiday.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayType;

/**
 * Summary response DTO for holiday data using Java 24 record. Contains minimal information for list
 * views and quick references.
 *
 * <p>This record demonstrates DOP principles:
 *
 * <ul>
 *   <li>Model Data Immutably and Transparently - immutable record structure
 *   <li>Model the Data, the Whole Data, and Nothing but the Data - contains exactly what's needed
 *       for summaries
 * </ul>
 */
public record HolidaySummaryResponse(
    String id,
    String name,
    HolidayType type,
    String locality,
    @JsonFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> date) {

  /** Gets a display name including type information. */
  public String getDisplayName() {
    return name + " (" + type.name().toLowerCase() + ")";
  }

  /** Gets a formatted summary string. */
  public String getSummary() {
    String dateStr = date.map(LocalDate::toString).orElse("TBD");
    return String.format("%s - %s in %s (%s)", name, type.name().toLowerCase(), locality, dateStr);
  }

  /** Checks if this holiday is governmental. */
  public boolean isGovernmental() {
    return type.isGovernmental();
  }

  /** Checks if this holiday has a calculated date. */
  public boolean hasDate() {
    return date.isPresent();
  }
}
