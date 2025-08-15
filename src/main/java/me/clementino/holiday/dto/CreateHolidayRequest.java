package me.clementino.holiday.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.domain.dop.KnownHoliday;
import me.clementino.holiday.domain.dop.MoveableHolidayType;

/**
 * Request DTO for creating a new holiday using Java 24 record.
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
    @Size(max = 1000, message = "Description must not exceed 1000 characters") String description,
    @NotNull(message = "Holiday type is required") HolidayType type,
    @NotNull(message = "Locality is required") @Valid LocalityDto locality,
    @NotNull(message = "Holiday variant is required") @Valid HolidayVariantDto variant) {

  /** DTO for locality information in requests. */
  public record LocalityDto(
      @NotBlank(message = "Country code is required")
          @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
          String countryCode,
      @NotBlank(message = "Country name is required")
          @Size(max = 255, message = "Country name must not exceed 255 characters")
          String countryName,
      @Size(max = 10, message = "Subdivision code must not exceed 10 characters")
          Optional<String> subdivisionCode,
      @Size(max = 255, message = "Subdivision name must not exceed 255 characters")
          Optional<String> subdivisionName,
      @Size(max = 255, message = "City name must not exceed 255 characters")
          Optional<String> cityName) {}

  /** Sealed interface for different holiday variant types in requests. */
  public sealed interface HolidayVariantDto
      permits HolidayVariantDto.Fixed,
          HolidayVariantDto.Observed,
          HolidayVariantDto.Moveable,
          HolidayVariantDto.MoveableFromBase {

    /** Fixed holiday variant - occurs on the same date every year. */
    record Fixed(
        @NotNull(message = "Date is required for fixed holidays")
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate date)
        implements HolidayVariantDto {}

    /** Observed holiday variant - has mondayisation rules for weekend adjustments. */
    record Observed(
        @NotNull(message = "Date is required for observed holidays")
            @JsonFormat(pattern = "yyyy-MM-dd")
            LocalDate date,
        @JsonFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> observedDate,
        boolean mondayisation)
        implements HolidayVariantDto {}

    /** Moveable holiday variant - calculated using algorithms. */
    record Moveable(
        @NotNull(message = "Known holiday is required for moveable holidays")
            KnownHoliday knownHoliday,
        @NotNull(message = "Moveable holiday type is required") MoveableHolidayType moveableType,
        boolean mondayisation,
        @JsonFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> calculatedDate)
        implements HolidayVariantDto {}

    /** Moveable from base holiday variant - calculated as offset from another holiday. */
    record MoveableFromBase(
        @NotBlank(message = "Base holiday ID is required") String baseHolidayId,
        int dayOffset,
        boolean mondayisation,
        @JsonFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> calculatedDate)
        implements HolidayVariantDto {}
  }
}
