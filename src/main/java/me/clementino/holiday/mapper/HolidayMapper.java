package me.clementino.holiday.mapper;

import me.clementino.holiday.domain.Holiday;
import me.clementino.holiday.dto.CreateHolidayRequest;
import me.clementino.holiday.dto.HolidayResponse;
import me.clementino.holiday.dto.UpdateHolidayRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Mapper for converting between domain objects and DTOs.
 * Following DOP principles - pure transformation functions.
 */
@Component
public class HolidayMapper {

    /**
     * Convert CreateHolidayRequest to Holiday domain object.
     */
    public Holiday toEntity(CreateHolidayRequest request) {
        Holiday holiday = new Holiday(
            request.getName(),
            request.getDate(),
            request.getCountry(),
            request.getType()
        );
        
        holiday.setObserved(request.getObserved());
        holiday.setState(request.getState());
        holiday.setCity(request.getCity());
        holiday.setRecurring(request.isRecurring());
        holiday.setDescription(request.getDescription());
        
        return holiday;
    }

    /**
     * Convert Holiday domain object to HolidayResponse.
     */
    public HolidayResponse toResponse(Holiday holiday) {
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
     * Convert list of Holiday domain objects to list of HolidayResponse.
     */
    public List<HolidayResponse> toResponseList(List<Holiday> holidays) {
        return holidays.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Update existing Holiday with data from UpdateHolidayRequest.
     */
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
