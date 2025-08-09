package me.clementino.holiday.mapper;

import java.util.List;
import me.clementino.holiday.domain.*;
import me.clementino.holiday.dto.CreateHolidayRequest;
import me.clementino.holiday.dto.HolidayResponse;
import me.clementino.holiday.dto.UpdateHolidayRequest;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between domain objects and DTOs. Following DOP principles - pure
 * transformation functions working with immutable data.
 */
@Component
public class HolidayMapper {

  /** Convert HolidayData domain object to HolidayResponse DTO. */
  public HolidayResponse toResponse(HolidayData holidayData) {
    HolidayResponse response = new HolidayResponse();

    response.setId(holidayData.id());
    response.setName(holidayData.name());
    response.setDate(holidayData.date());
    response.setObserved(holidayData.observed().orElse(null));
    response.setCountry(holidayData.location().country());
    response.setState(holidayData.location().state().orElse(null));
    response.setCity(holidayData.location().city().orElse(null));
    response.setType(holidayData.type());
    response.setRecurring(holidayData.recurring());
    response.setDescription(holidayData.description().orElse(null));
    response.setDateCreated(holidayData.dateCreated().orElse(null));
    response.setLastUpdated(holidayData.lastUpdated().orElse(null));
    response.setVersion(holidayData.version().orElse(null));

    return response;
  }

  /** Convert list of HolidayData domain objects to list of HolidayResponse DTOs. */
  public List<HolidayResponse> toHolidayDataResponseList(List<HolidayData> holidayDataList) {
    return holidayDataList.stream().map(this::toResponse).toList();
  }

  // Legacy methods for backward compatibility with persistence layer

  /**
   * Convert CreateHolidayRequest to Holiday persistence entity.
   *
   * @deprecated Use DOP command pattern instead
   */
  @Deprecated
  public Holiday toEntity(CreateHolidayRequest request) {
    Holiday holiday =
        new Holiday(request.getName(), request.getDate(), request.getCountry(), request.getType());

    holiday.setObserved(request.getObserved());
    holiday.setState(request.getState());
    holiday.setCity(request.getCity());
    holiday.setRecurring(request.isRecurring());
    holiday.setDescription(request.getDescription());

    return holiday;
  }

  /**
   * Convert Holiday persistence entity to HolidayResponse DTO.
   *
   * @deprecated Use toResponse(HolidayData) instead for DOP approach
   */
  @Deprecated
  public HolidayResponse toResponseFromEntity(Holiday holiday) {
    HolidayResponse response = new HolidayResponse();

    response.setId(holiday.getId());
    response.setName(holiday.getName());
    response.setDate(holiday.getDate());
    response.setObserved(holiday.getObserved());
    response.setCountry(holiday.getCountry());
    response.setState(holiday.getState());
    response.setCity(holiday.getCity());
    response.setType(holiday.getType());
    response.setRecurring(holiday.isRecurring());
    response.setDescription(holiday.getDescription());
    response.setDateCreated(holiday.getDateCreated());
    response.setLastUpdated(holiday.getLastUpdated());
    response.setVersion(holiday.getVersion());

    return response;
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

  /**
   * Update existing Holiday persistence entity with data from UpdateHolidayRequest.
   *
   * @deprecated Use DOP command pattern instead
   */
  @Deprecated
  public void updateEntity(Holiday holiday, UpdateHolidayRequest request) {
    if (request.getName() != null) {
      holiday.setName(request.getName());
    }
    if (request.getDate() != null) {
      holiday.setDate(request.getDate());
    }
    if (request.getObserved() != null) {
      holiday.setObserved(request.getObserved());
    }
    if (request.getCountry() != null) {
      holiday.setCountry(request.getCountry());
    }
    if (request.getState() != null) {
      holiday.setState(request.getState());
    }
    if (request.getCity() != null) {
      holiday.setCity(request.getCity());
    }
    if (request.getType() != null) {
      holiday.setType(request.getType());
    }
    holiday.setRecurring(request.isRecurring());
    if (request.getDescription() != null) {
      holiday.setDescription(request.getDescription());
    }
  }
}
