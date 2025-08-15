package me.clementino.holiday.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

/**
 * Specialized validation error response DTO using Java 24 record.
 *
 * <p>This record demonstrates DOP principles:
 *
 * <ul>
 *   <li>Model Data Immutably and Transparently - immutable record structure
 *   <li>Model the Data, the Whole Data, and Nothing but the Data - contains exactly what's needed
 *       for validation errors
 * </ul>
 */
public record ValidationErrorResponse(
    String message,
    List<FieldError> fieldErrors,
    List<GlobalError> globalErrors,
    String path,
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") OffsetDateTime timestamp,
    int errorCount) {

  /** Record representing a field-specific validation error. */
  public record FieldError(String field, Object rejectedValue, String message, String code) {

    /** Creates a field error with null rejected value. */
    public static FieldError of(String field, String message) {
      return new FieldError(field, null, message, "validation.failed");
    }

    /** Creates a field error with rejected value. */
    public static FieldError of(String field, Object rejectedValue, String message) {
      return new FieldError(field, rejectedValue, message, "validation.failed");
    }

    /** Creates a field error with custom code. */
    public static FieldError of(String field, Object rejectedValue, String message, String code) {
      return new FieldError(field, rejectedValue, message, code);
    }
  }

  /** Record representing a global validation error (not tied to a specific field). */
  public record GlobalError(String message, String code, Object[] arguments) {

    /** Creates a global error with just a message. */
    public static GlobalError of(String message) {
      return new GlobalError(message, "validation.global", new Object[0]);
    }

    /** Creates a global error with code. */
    public static GlobalError of(String message, String code) {
      return new GlobalError(message, code, new Object[0]);
    }

    /** Creates a global error with arguments. */
    public static GlobalError of(String message, String code, Object... arguments) {
      return new GlobalError(message, code, arguments);
    }
  }

  /** Creates a validation error response from field errors. */
  public static ValidationErrorResponse fromFieldErrors(List<FieldError> fieldErrors) {
    return new ValidationErrorResponse(
        "Validation failed",
        fieldErrors,
        List.of(),
        null,
        OffsetDateTime.now(),
        fieldErrors.size());
  }

  /** Creates a validation error response from global errors. */
  public static ValidationErrorResponse fromGlobalErrors(List<GlobalError> globalErrors) {
    return new ValidationErrorResponse(
        "Validation failed",
        List.of(),
        globalErrors,
        null,
        OffsetDateTime.now(),
        globalErrors.size());
  }

  /** Creates a validation error response from both field and global errors. */
  public static ValidationErrorResponse of(
      List<FieldError> fieldErrors, List<GlobalError> globalErrors) {
    return new ValidationErrorResponse(
        "Validation failed",
        fieldErrors,
        globalErrors,
        null,
        OffsetDateTime.now(),
        fieldErrors.size() + globalErrors.size());
  }

  /** Creates a validation error response from a map of field errors. */
  public static ValidationErrorResponse fromFieldErrorMap(Map<String, String> fieldErrorMap) {
    List<FieldError> fieldErrors =
        fieldErrorMap.entrySet().stream()
            .map(entry -> FieldError.of(entry.getKey(), entry.getValue()))
            .toList();

    return fromFieldErrors(fieldErrors);
  }

  /** Adds path information to this validation error response. */
  public ValidationErrorResponse withPath(String requestPath) {
    return new ValidationErrorResponse(
        message, fieldErrors, globalErrors, requestPath, timestamp, errorCount);
  }

  /** Checks if this response has field-specific errors. */
  public boolean hasFieldErrors() {
    return !fieldErrors.isEmpty();
  }

  /** Checks if this response has global errors. */
  public boolean hasGlobalErrors() {
    return !globalErrors.isEmpty();
  }

  /** Gets all error messages as a flat list. */
  public List<String> getAllErrorMessages() {
    List<String> messages =
        fieldErrors.stream().map(FieldError::message).collect(java.util.stream.Collectors.toList());

    globalErrors.stream().map(GlobalError::message).forEach(messages::add);

    return messages;
  }

  /** Gets a summary of all errors. */
  public String getErrorSummary() {
    if (errorCount == 0) {
      return "No validation errors";
    }

    int fieldErrorCount = fieldErrors.size();
    int globalErrorCount = globalErrors.size();

    if (fieldErrorCount > 0 && globalErrorCount > 0) {
      return String.format(
          "%d field errors and %d global errors", fieldErrorCount, globalErrorCount);
    } else if (fieldErrorCount > 0) {
      return String.format("%d field error%s", fieldErrorCount, fieldErrorCount == 1 ? "" : "s");
    } else {
      return String.format("%d global error%s", globalErrorCount, globalErrorCount == 1 ? "" : "s");
    }
  }
}
