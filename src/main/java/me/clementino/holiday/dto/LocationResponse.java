package me.clementino.holiday.dto;

/** Immutable DTO for location information in API responses. */
public record LocationResponse(String country, String state, String city) {}
