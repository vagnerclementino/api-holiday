package me.clementino.holiday.dto;

import java.time.LocalDate;
import me.clementino.holiday.domain.HolidayType;

/** Simple response DTO for holidays that matches the current API structure. */
public record SimpleHolidayResponse(
    String id,
    String name,
    LocalDate date,
    LocalDate observed,
    String country,
    String state,
    String city,
    HolidayType type,
    Boolean recurring,
    String description,
    String dateCreated,
    String lastUpdated,
    Integer version) {}
