package me.clementino.holiday.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayData;
import me.clementino.holiday.domain.Location;
import me.clementino.holiday.dto.CreateHolidayRequest;
import me.clementino.holiday.dto.HolidayResponseDTO;
import me.clementino.holiday.dto.LocationInfo;
import me.clementino.holiday.dto.WhenInfo;
import org.springframework.stereotype.Component;

/** Simple mapper for Holiday data using DOP principles. */
@Component
public class SimpleHolidayMapper {

  /** Convert HolidayData to HolidayResponseDTO. */
  public HolidayResponseDTO toResponse(HolidayData holidayData) {
    // Create when info (required)
    WhenInfo when = WhenInfo.from(holidayData.date());

    // Create observed info (optional)
    WhenInfo observed = holidayData.observed().map(WhenInfo::from).orElse(null);

    // Create location info (at least one required)
    LocationInfo locationInfo = LocationInfo.from(holidayData.location());
    List<LocationInfo> where = List.of(locationInfo);

    return new HolidayResponseDTO(
        holidayData.id(),
        holidayData.name(),
        when,
        observed,
        where,
        holidayData.type(),
        holidayData.description().orElse(null),
        holidayData.dateCreated().map(LocalDateTime::toString).orElse(null),
        holidayData.lastUpdated().map(LocalDateTime::toString).orElse(null));
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
