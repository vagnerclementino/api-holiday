package me.clementino.holiday.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Enhanced error response DTO using Java 24 record.
 *
 * <p>This record demonstrates DOP principles:
 *
 * <ul>
 *   <li>Model Data Immutably and Transparently - immutable record structure
 *   <li>Model the Data, the Whole Data, and Nothing but the Data - contains exactly what's needed
 *       for error responses
 * </ul>
 */
public record ErrorResponse(
    String error,
    String message,
    List<String> details,
    Map<String, String> fieldErrors,
    String path,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") OffsetDateTime timestamp,
    int status) {

  /** Creates a simple error response with just a message. */
  public static ErrorResponse of(String message) {
    return new ErrorResponse(
        "Bad Request", message, List.of(), Map.of(), null, OffsetDateTime.now(), 400);
  }

  /** Creates an error response with message and details. */
  public static ErrorResponse of(String message, List<String> details) {
    return new ErrorResponse(
        "Bad Request", message, details, Map.of(), null, OffsetDateTime.now(), 400);
  }

  /** Creates an error response with custom status. */
  public static ErrorResponse of(String error, String message, int status) {
    return new ErrorResponse(
        error, message, List.of(), Map.of(), null, OffsetDateTime.now(), status);
  }

  /** Creates an error response with field errors. */
  public static ErrorResponse withFieldErrors(String message, Map<String, String> fieldErrors) {
    return new ErrorResponse(
        "Validation Failed", message, List.of(), fieldErrors, null, OffsetDateTime.now(), 400);
  }

  /** Creates a not found error response. */
  public static ErrorResponse notFound(String resource, String id) {
    return new ErrorResponse(
        "Not Found",
        String.format("%s with id '%s' not found", resource, id),
        List.of(),
        Map.of(),
        null,
        OffsetDateTime.now(),
        404);
  }

  /** Creates an internal server error response. */
  public static ErrorResponse internalError(String message) {
    return new ErrorResponse(
        "Internal Server Error", message, List.of(), Map.of(), null, OffsetDateTime.now(), 500);
  }

  /** Adds path information to this error response. */
  public ErrorResponse withPath(String requestPath) {
    return new ErrorResponse(error, message, details, fieldErrors, requestPath, timestamp, status);
  }

  /** Checks if this error has field-specific validation errors. */
  public boolean hasFieldErrors() {
    return !fieldErrors.isEmpty();
  }

  /** Checks if this error has additional details. */
  public boolean hasDetails() {
    return !details.isEmpty();
  }
}
