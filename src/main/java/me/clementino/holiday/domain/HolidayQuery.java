package me.clementino.holiday.domain;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import me.clementino.holiday.domain.dop.HolidayType;
import me.clementino.holiday.domain.dop.Locality;

/**
 * Enhanced immutable record representing a holiday query/filter using comprehensive DOP principles.
 * Following DOP v1.1 Principle 2: Model the Data, the Whole Data, and Nothing but the Data.
 *
 * <p>This record models exactly what a holiday query contains - no more, no less. It represents all
 * possible query parameters in a type-safe way, enhanced to work with the DOP domain model.
 */
public record HolidayQuery(
    Optional<String> namePattern,
    Optional<Locality> locality,
    Optional<String> countryCode,
    Optional<String> subdivisionCode,
    Optional<String> cityName,
    Optional<HolidayType> type,
    Optional<LocalDate> startDate,
    Optional<LocalDate> endDate,
    Optional<Integer> year,
    Optional<Boolean> includeObserved,
    Optional<Boolean> governmentalOnly,
    Optional<Boolean> calculatedOnly,
    Optional<SortCriteria> sortCriteria,
    Optional<PaginationCriteria> paginationCriteria) {

  /** Record for sort criteria. */
  public record SortCriteria(SortField field, SortDirection direction) {
    public enum SortField {
      NAME,
      DATE,
      TYPE,
      COUNTRY,
      CREATED_AT,
      UPDATED_AT
    }

    public enum SortDirection {
      ASC,
      DESC
    }

    public static SortCriteria byName() {
      return new SortCriteria(SortField.NAME, SortDirection.ASC);
    }

    public static SortCriteria byDate() {
      return new SortCriteria(SortField.DATE, SortDirection.ASC);
    }

    public static SortCriteria byType() {
      return new SortCriteria(SortField.TYPE, SortDirection.ASC);
    }
  }

  /** Record for pagination criteria. */
  public record PaginationCriteria(int page, int size) {
    public PaginationCriteria {
      if (page < 0) {
        throw new IllegalArgumentException("Page must be non-negative");
      }
      if (size < 1 || size > 100) {
        throw new IllegalArgumentException("Size must be between 1 and 100");
      }
    }

    public static PaginationCriteria of(int page, int size) {
      return new PaginationCriteria(page, size);
    }

    public static PaginationCriteria defaultPagination() {
      return new PaginationCriteria(0, 20);
    }

    public int getOffset() {
      return page * size;
    }
  }

  public HolidayQuery {
    namePattern = Objects.requireNonNullElse(namePattern, Optional.empty());
    locality = Objects.requireNonNullElse(locality, Optional.empty());
    countryCode = Objects.requireNonNullElse(countryCode, Optional.empty());
    subdivisionCode = Objects.requireNonNullElse(subdivisionCode, Optional.empty());
    cityName = Objects.requireNonNullElse(cityName, Optional.empty());
    type = Objects.requireNonNullElse(type, Optional.empty());
    startDate = Objects.requireNonNullElse(startDate, Optional.empty());
    endDate = Objects.requireNonNullElse(endDate, Optional.empty());
    year = Objects.requireNonNullElse(year, Optional.empty());
    includeObserved = Objects.requireNonNullElse(includeObserved, Optional.empty());
    governmentalOnly = Objects.requireNonNullElse(governmentalOnly, Optional.empty());
    calculatedOnly = Objects.requireNonNullElse(calculatedOnly, Optional.empty());
    sortCriteria = Objects.requireNonNullElse(sortCriteria, Optional.empty());
    paginationCriteria = Objects.requireNonNullElse(paginationCriteria, Optional.empty());

    if (startDate.isPresent() && endDate.isPresent()) {
      if (startDate.get().isAfter(endDate.get())) {
        throw new IllegalArgumentException("Start date cannot be after end date");
      }
    }

    if (year.isPresent()) {
      int yearValue = year.get();
      if (yearValue < 1900 || yearValue > 2200) {
        throw new IllegalArgumentException("Year must be between 1900 and 2200");
      }
    }
  }

  /** Empty query (no filters). */
  public static HolidayQuery empty() {
    return new HolidayQuery(
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

  /** Query by country code only. */
  public static HolidayQuery byCountry(String countryCode) {
    return new HolidayQuery(
        Optional.empty(),
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
        Optional.empty());
  }

  /** Query by locality. */
  public static HolidayQuery byLocality(Locality locality) {
    return new HolidayQuery(
        Optional.empty(),
        Optional.of(locality),
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

  /** Query by type. */
  public static HolidayQuery byType(HolidayType type) {
    return new HolidayQuery(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.of(type),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
  }

  /** Query by date range. */
  public static HolidayQuery byDateRange(LocalDate startDate, LocalDate endDate) {
    return new HolidayQuery(
        Optional.empty(),
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
        Optional.empty());
  }

  /** Query by year. */
  public static HolidayQuery byYear(int year) {
    return new HolidayQuery(
        Optional.empty(),
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
        Optional.empty());
  }

  /** Query for governmental holidays only. */
  public static HolidayQuery forGovernmentalOnly() {
    return new HolidayQuery(
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
        Optional.of(true),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
  }

  public boolean isEmpty() {
    return namePattern.isEmpty()
        && locality.isEmpty()
        && countryCode.isEmpty()
        && subdivisionCode.isEmpty()
        && cityName.isEmpty()
        && type.isEmpty()
        && startDate.isEmpty()
        && endDate.isEmpty()
        && year.isEmpty()
        && includeObserved.isEmpty()
        && governmentalOnly.isEmpty()
        && calculatedOnly.isEmpty();
  }

  public boolean hasLocationFilter() {
    return locality.isPresent()
        || countryCode.isPresent()
        || subdivisionCode.isPresent()
        || cityName.isPresent();
  }

  public boolean hasDateFilter() {
    return startDate.isPresent() || endDate.isPresent() || year.isPresent();
  }

  public boolean hasTypeFilter() {
    return type.isPresent() || governmentalOnly.isPresent();
  }

  public boolean hasNameFilter() {
    return namePattern.isPresent() && !namePattern.get().isBlank();
  }

  public boolean hasSorting() {
    return sortCriteria.isPresent();
  }

  public boolean hasPagination() {
    return paginationCriteria.isPresent();
  }

  /** Gets the effective sort criteria (defaults to name ascending). */
  public SortCriteria getEffectiveSortCriteria() {
    return sortCriteria.orElse(SortCriteria.byName());
  }

  /** Gets the effective pagination criteria (defaults to page 0, size 20). */
  public PaginationCriteria getEffectivePaginationCriteria() {
    return paginationCriteria.orElse(PaginationCriteria.defaultPagination());
  }

  /** Creates a new query with pagination. */
  public HolidayQuery withPagination(int page, int size) {
    return new HolidayQuery(
        namePattern,
        locality,
        countryCode,
        subdivisionCode,
        cityName,
        type,
        startDate,
        endDate,
        year,
        includeObserved,
        governmentalOnly,
        calculatedOnly,
        sortCriteria,
        Optional.of(new PaginationCriteria(page, size)));
  }

  /** Creates a new query with sorting. */
  public HolidayQuery withSorting(
      SortCriteria.SortField field, SortCriteria.SortDirection direction) {
    return new HolidayQuery(
        namePattern,
        locality,
        countryCode,
        subdivisionCode,
        cityName,
        type,
        startDate,
        endDate,
        year,
        includeObserved,
        governmentalOnly,
        calculatedOnly,
        Optional.of(new SortCriteria(field, direction)),
        paginationCriteria);
  }
}
