package me.clementino.holiday.mapper;

import java.time.OffsetDateTime;
import java.util.List;
import me.clementino.holiday.domain.Holiday;
import me.clementino.holiday.domain.HolidayData;
import me.clementino.holiday.dto.HolidayResponse;
import me.clementino.holiday.dto.LocalityResponse;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between domain objects and DTOs. Following DOP principles - pure
 * transformation functions working with immutable data.
 */
@Component
public class HolidayMapper {

  /** Convert HolidayData domain object to HolidayResponse DTO. */
  public HolidayResponse toResponse(HolidayData holidayData) {
    // Create locality response
    LocalityResponse locality = createLocalityResponse(holidayData);

    // Create variant response - for now, create a simple Fixed variant
    // TODO: This needs to be enhanced to handle all DOP Holiday variants
    HolidayResponse.HolidayVariantResponse variant =
        new HolidayResponse.HolidayVariantResponse.Fixed(holidayData.date());

    return new HolidayResponse(
        holidayData.id(),
        holidayData.name(),
        holidayData.description().orElse(""),
        holidayData.type(),
        locality,
        variant,
        holidayData
            .dateCreated()
            .map(ldt -> ldt.atOffset(java.time.ZoneOffset.UTC))
            .orElse(OffsetDateTime.now()),
        holidayData
            .lastUpdated()
            .map(ldt -> ldt.atOffset(java.time.ZoneOffset.UTC))
            .orElse(OffsetDateTime.now()),
        holidayData.version().orElse(1));
  }

  /** Convert list of HolidayData domain objects to list of HolidayResponse DTOs. */
  public List<HolidayResponse> toHolidayDataResponseList(List<HolidayData> holidayDataList) {
    return holidayDataList.stream().map(this::toResponse).toList();
  }

  private LocalityResponse createLocalityResponse(HolidayData holidayData) {
    String country = holidayData.location().country();
    String state = holidayData.location().state().orElse(null);
    String city = holidayData.location().city().orElse(null);

    if (city != null && state != null) {
      return LocalityResponse.city(country, country, state, state, city);
    } else if (state != null) {
      return LocalityResponse.subdivision(country, country, state, state);
    } else {
      return LocalityResponse.country(country, country);
    }
  }

  // Legacy methods for backward compatibility with persistence layer

  /**
   * Convert Holiday persistence entity to HolidayResponse DTO.
   *
   * @deprecated Use toResponse(HolidayData) instead for DOP approach
   */
  @Deprecated
  public HolidayResponse toResponseFromEntity(Holiday holiday) {
    // Create locality response from entity
    LocalityResponse locality = createLocalityResponseFromEntity(holiday);

    // Create variant response - for now, create a simple Fixed variant
    HolidayResponse.HolidayVariantResponse variant =
        new HolidayResponse.HolidayVariantResponse.Fixed(holiday.getDate());

    return new HolidayResponse(
        holiday.getId(),
        holiday.getName(),
        holiday.getDescription() != null ? holiday.getDescription() : "",
        holiday.getType(),
        locality,
        variant,
        holiday.getDateCreated() != null
            ? holiday.getDateCreated().atOffset(java.time.ZoneOffset.UTC)
            : OffsetDateTime.now(),
        holiday.getLastUpdated() != null
            ? holiday.getLastUpdated().atOffset(java.time.ZoneOffset.UTC)
            : OffsetDateTime.now(),
        holiday.getVersion() != null ? holiday.getVersion() : 1);
  }

  /**
   * Convert list of Holiday persistence entities to list of HolidayResponse DTOs.
   *
   * @deprecated Use toHolidayDataResponseList instead for DOP approach
   */
  @Deprecated
  public List<HolidayResponse> toResponseList(List<Holiday> holidays) {
    return holidays.stream().map(this::toResponseFromEntity).toList();
  }

  private LocalityResponse createLocalityResponseFromEntity(Holiday holiday) {
    String country = holiday.getCountry();
    String state = holiday.getState();
    String city = holiday.getCity();

    if (city != null && state != null) {
      return LocalityResponse.city(country, country, state, state, city);
    } else if (state != null) {
      return LocalityResponse.subdivision(country, country, state, state);
    } else {
      return LocalityResponse.country(country, country);
    }
  }
}
