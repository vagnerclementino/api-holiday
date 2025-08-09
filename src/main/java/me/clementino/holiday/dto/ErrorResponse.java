package me.clementino.holiday.dto;

import java.util.List;

/** Immutable DTO for error responses. */
public record ErrorResponse(String message, List<String> errors) {

  public static ErrorResponse of(String message) {
    return new ErrorResponse(message, List.of());
  }

  public static ErrorResponse of(String message, List<String> errors) {
    return new ErrorResponse(message, errors);
  }
}
