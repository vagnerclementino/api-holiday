package me.clementino.holiday.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Optional;
import me.clementino.holiday.domain.dop.HolidayType;

/**
 * Request DTO for querying holidays with filtering options using Java 24 record.
 *
 * <p>This record demonstrates DOP principles:
 *
 * <ul>
 *   <li>Model Data Immutably and Transparently - immutable record structure
 *   <li>Model the Data, the Whole Data, and Nothing but the Data - contains exactly what's needed
 *       for filtering
 * </ul>
 */
public record HolidayQueryRequest(
    @Size(max = 255, message = "Name filter must not exceed 255 characters") Optional<String> name,
    @Size(min = 2, max = 2, message = "Country code must be exactly 2 characters")
        Optional<String> countryCode,
    @Size(max = 10, message = "Subdivision code must not exceed 10 characters")
        Optional<String> subdivisionCode,
    @Size(max = 255, message = "City name must not exceed 255 characters")
        Optional<String> cityName,
    Optional<HolidayType> type,
    @JsonFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> startDate,
    @JsonFormat(pattern = "yyyy-MM-dd") Optional<LocalDate> endDate,
    @Min(value = 1900, message = "Year must be at least 1900")
        @Max(value = 2200, message = "Year must not exceed 2200")
        Optional<Integer> year,
    Optional<Boolean> includeObserved,
    Optional<Boolean> governmentalOnly,
    @Min(value = 0, message = "Page must be non-negative") Optional<Integer> page,
    @Min(value = 1, message = "Size must be at least 1")
        @Max(value = 100, message = "Size must not exceed 100")
        Optional<Integer> size,
    @Size(max = 50, message = "Sort field must not exceed 50 characters") Optional<String> sortBy,
    Optional<SortDirection> sortDirection) {

  /** Enum for sort direction. */
  public enum SortDirection {
    ASC,
    DESC
  }

  /** Creates an empty query request with all filters set to Optional.empty(). */
  public static HolidayQueryRequest empty() {
    return new HolidayQueryRequest(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
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

  /** Creates a query request for a specific country. */
  public static HolidayQueryRequest forCountry(String countryCode) {
    return new HolidayQueryRequest(
        Optional.empty(),
        Optional.of(countryCode),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
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

  /** Creates a query request for a specific year. */
  public static HolidayQueryRequest forYear(int year) {
    return new HolidayQueryRequest(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.of(year),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
  }

  /** Creates a query request for a date range. */
  public static HolidayQueryRequest forDateRange(LocalDate startDate, LocalDate endDate) {
    return new HolidayQueryRequest(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.of(startDate),
        Optional.of(endDate),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
  }

  /** Creates a query request for governmental holidays only. */
  public static HolidayQueryRequest forGovernmentalOnly() {
    return new HolidayQueryRequest(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.of(true),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
  }

  /** Checks if this query has any active filters. */
  public boolean hasFilters() {
    return name.isPresent()
        || countryCode.isPresent()
        || subdivisionCode.isPresent()
        || cityName.isPresent()
        || type.isPresent()
        || startDate.isPresent()
        || endDate.isPresent()
        || year.isPresent()
        || includeObserved.isPresent()
        || governmentalOnly.isPresent();
  }

  /** Gets the effective page number (defaults to 0 if not specified). */
  public int getEffectivePage() {
    return page.orElse(0);
  }

  /** Gets the effective page size (defaults to 20 if not specified). */
  public int getEffectiveSize() {
    return size.orElse(20);
  }

  /** Gets the effective sort field (defaults to "name" if not specified). */
  public String getEffectiveSortBy() {
    return sortBy.orElse("name");
  }

  /** Gets the effective sort direction (defaults to ASC if not specified). */
  public SortDirection getEffectiveSortDirection() {
    return sortDirection.orElse(SortDirection.ASC);
  }
}
