package me.clementino.holiday.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.domain.dop.KnownHoliday;
import me.clementino.holiday.domain.dop.MoveableHolidayType;

/**
 * Response DTO for holiday data using Java 24 record.
 *
 * <p>This record demonstrates DOP principles:
 *
 * <ul>
 *   <li>Model Data Immutably and Transparently - immutable record structure
 *   <li>Model the Data, the Whole Data, and Nothing but the Data - contains exactly what's needed
 *       for responses
 * </ul>
 */
public record HolidayResponse(
    String id,
    String name,
    String description,
    HolidayType type,
    LocalityResponse locality,
    HolidayVariantResponse variant,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") OffsetDateTime createdAt,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") OffsetDateTime updatedAt,
    Integer version) {

  /** Sealed interface for different holiday variant types in responses. */
  public sealed interface HolidayVariantResponse
      permits HolidayVariantResponse.Fixed,
          HolidayVariantResponse.Observed,
          HolidayVariantResponse.Moveable,
          HolidayVariantResponse.MoveableFromBase {

    /** Fixed holiday variant response. */
    record Fixed(@JsonFormat(pattern = "yyyy-MM-dd") LocalDate date)
        implements HolidayVariantResponse {}

    /** Observed holiday variant response. */
    record Observed(
        @JsonFormat(pattern = "yyyy-MM-dd") LocalDate date,
        @JsonFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> observedDate,
        boolean mondayisation,
        boolean isWeekend)
        implements HolidayVariantResponse {}

    /** Moveable holiday variant response. */
    record Moveable(
        KnownHoliday knownHoliday,
        MoveableHolidayType moveableType,
        boolean mondayisation,
        @JsonFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> calculatedDate,
        Optional<Integer> calculatedYear)
        implements HolidayVariantResponse {}

    /** Moveable from base holiday variant response. */
    record MoveableFromBase(
        String baseHolidayId,
        String baseHolidayName,
        int dayOffset,
        boolean mondayisation,
        @JsonFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> calculatedDate,
        Optional<Integer> calculatedYear)
        implements HolidayVariantResponse {}
  }

  /** Creates a summary response with minimal information. */
  public HolidaySummaryResponse toSummary() {
    return new HolidaySummaryResponse(id, name, type, locality.displayName(), getEffectiveDate());
  }

  /** Gets the effective date for this holiday (calculated or fixed). */
  public Optional<LocalDate> getEffectiveDate() {
    return switch (variant) {
      case HolidayVariantResponse.Fixed fixed -> Optional.of(fixed.date());
      case HolidayVariantResponse.Observed observed ->
          observed.observedDate().or(() -> Optional.of(observed.date()));
      case HolidayVariantResponse.Moveable moveable -> moveable.calculatedDate();
      case HolidayVariantResponse.MoveableFromBase moveableFromBase ->
          moveableFromBase.calculatedDate();
    };
  }

  /** Checks if this holiday is governmental. */
  public boolean isGovernmental() {
    return type.isGovernmental();
  }

  /** Gets a display name including type information. */
  public String getDisplayName() {
    return name + " (" + type.name().toLowerCase() + ")";
  }
}
