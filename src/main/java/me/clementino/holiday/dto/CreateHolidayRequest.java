package me.clementino.holiday.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.KnownHoliday;
import me.clementino.holiday.domain.dop.Locality;

/**
 * Sealed interface for creating different types of holidays following DOP principles.
 *
 * <p>This interface represents the data needed to create holidays, with each variant containing
 * exactly the data required for that specific type of holiday implementation (FixedHoliday,
 * ObservedHoliday, MoveableHoliday, MoveableFromBaseHoliday).
 *
 * <p>DOP Principles Applied: - Model the Data, the Whole Data, and Nothing but the Data - Make
 * Illegal States Unrepresentable - Immutable data structures using records
 *
 * <p>Jackson Configuration: - Uses @JsonTypeInfo to determine which concrete type to deserialize -
 * Property "type" in JSON determines the implementation (Fixed, Observed, etc.)
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
  @JsonSubTypes.Type(value = CreateHolidayRequest.Fixed.class, name = "Fixed"),
  @JsonSubTypes.Type(value = CreateHolidayRequest.Observed.class, name = "Observed"),
  @JsonSubTypes.Type(value = CreateHolidayRequest.Moveable.class, name = "Moveable"),
  @JsonSubTypes.Type(value = CreateHolidayRequest.MoveableFromBase.class, name = "MoveableFromBase")
})
public sealed interface CreateHolidayRequest
    permits CreateHolidayRequest.Fixed,
        CreateHolidayRequest.Observed,
        CreateHolidayRequest.Moveable,
        CreateHolidayRequest.MoveableFromBase {

  /** Common data for all holiday types (except date which varies by type). */
  @NotBlank
  String name();

  String description();

  @NotNull
  List<Locality> localities();

  @NotNull
  HolidayType holidayType(); // Renamed to avoid conflict with Jackson "type"

  /**
   * Request to create a fixed holiday. Fixed holidays occur on the same date every year (e.g.,
   * Christmas, New Year).
   *
   * <p>Uses day and month (required) and year (optional) to model recurring holidays. When year is
   * null, the holiday recurs every year on the same day/month.
   *
   * <p>Validates that day/month combinations are valid (e.g., no February 31st).
   */
  record Fixed(
      @NotBlank String name,
      String description,
      @NotNull @Min(1) @Max(31) Integer day,
      @NotNull Month month,
      @Min(1) Integer year, // Optional - null means recurring every year
      @NotNull List<Locality> localities,
      @NotNull HolidayType holidayType)
      implements CreateHolidayRequest {

    /** Compact constructor with validation for day/month combinations. */
    public Fixed {
      if (day != null && month != null) {
        validateDayMonthCombination(day, month);
      }
    }

    /** Get the LocalDate for this holiday. */
    public LocalDate date() {
      // If year is provided, create specific date; otherwise use current year as default
      int effectiveYear = year != null ? year : LocalDate.now().getYear();
      return LocalDate.of(effectiveYear, month, day);
    }

    /** Check if this is a recurring holiday (year not specified). */
    public boolean isRecurring() {
      return year == null;
    }

    /**
     * Validates that the day/month combination is valid. Prevents illegal states like February
     * 31st.
     */
    private static void validateDayMonthCombination(int day, Month month) {
      // Check if the day is valid for the given month
      // Use a leap year (2024) to allow February 29th
      int maxDaysInMonth = month.length(true); // true = leap year

      if (day > maxDaysInMonth) {
        throw new IllegalArgumentException(
            String.format(
                "Invalid day %d for month %s. Maximum days in %s: %d",
                day, month, month, maxDaysInMonth));
      }
    }
  }

  /**
   * Request to create an observed holiday. Observed holidays have a different observed date when
   * they fall on weekends.
   */
  record Observed(
      @NotBlank String name,
      String description,
      @NotNull LocalDate date,
      @NotNull List<Locality> localities,
      @NotNull HolidayType holidayType,
      @NotNull LocalDate observed,
      boolean mondayisation)
      implements CreateHolidayRequest {}

  /**
   * Request to create a moveable holiday. Moveable holidays are calculated based on known holiday
   * rules (e.g., Easter-based holidays).
   */
  record Moveable(
      @NotBlank String name,
      String description,
      @NotNull LocalDate date,
      @NotNull List<Locality> localities,
      @NotNull HolidayType holidayType,
      @NotNull KnownHoliday knownHoliday,
      boolean mondayisation)
      implements CreateHolidayRequest {}

  /**
   * Request to create a moveable holiday derived from a base holiday. These holidays are calculated
   * as an offset from another holiday.
   */
  record MoveableFromBase(
      @NotBlank String name,
      String description,
      @NotNull LocalDate date,
      @NotNull List<Locality> localities,
      @NotNull HolidayType holidayType,
      @NotNull KnownHoliday knownHoliday,
      @NotNull Holiday baseHoliday,
      int dayOffset,
      boolean mondayisation)
      implements CreateHolidayRequest {}
}
