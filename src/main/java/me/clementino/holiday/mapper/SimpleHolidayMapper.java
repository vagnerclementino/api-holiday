package me.clementino.holiday.mapper;

import java.time.LocalDateTime;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayData;
import me.clementino.holiday.domain.Location;
import me.clementino.holiday.dto.CreateHolidayRequest;
import me.clementino.holiday.dto.SimpleHolidayResponse;
import org.springframework.stereotype.Component;

/** Simple mapper for Holiday data using DOP principles. */
@Component
public class SimpleHolidayMapper {

  /** Convert HolidayData to SimpleHolidayResponse. */
  public SimpleHolidayResponse toResponse(HolidayData holidayData) {
    return new SimpleHolidayResponse(
        holidayData.id(),
        holidayData.name(),
        holidayData.date(),
        holidayData.observed().orElse(null),
        holidayData.location().country(),
        holidayData.location().state().orElse(null),
        holidayData.location().city().orElse(null),
        holidayData.type(),
        holidayData.recurring(),
        holidayData.description().orElse(null),
        holidayData.dateCreated().map(LocalDateTime::toString).orElse(null),
        holidayData.lastUpdated().map(LocalDateTime::toString).orElse(null),
        holidayData.version().orElse(null));
  }

  /** Convert CreateHolidayRequest to HolidayData. */
  public HolidayData fromCreateRequest(CreateHolidayRequest request) {
    return new HolidayData(
        null, // ID will be generated
        request.name(),
        request.date(),
        Optional.ofNullable(request.observed()),
        new Location(
            request.country(),
            Optional.ofNullable(request.state()),
            Optional.ofNullable(request.city())),
        request.type(),
        request.recurring() != null ? request.recurring() : false,
        Optional.ofNullable(request.description()),
        Optional.empty(), // dateCreated
        Optional.empty(), // lastUpdated
        Optional.empty() // version
        );
  }
}
