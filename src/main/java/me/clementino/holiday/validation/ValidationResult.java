package me.clementino.holiday.validation;

import module java.base;

/**
 * Enhanced sealed interface representing validation results using comprehensive
 * DOP principles.
 * Following DOP principle: Make illegal states unrepresentable. Can only be
 * Success, Warning, or
 * Failure - no other states possible.
 */
public sealed interface ValidationResult
    permits ValidationResult.Success, ValidationResult.Warning, ValidationResult.Failure {

  /** Successful validation result. */
  record Success(String message, Optional<Object> validatedData) implements ValidationResult {
    public Success {
      if (message == null || message.isBlank()) {
        throw new IllegalArgumentException("Success message cannot be null or blank");
      }
      validatedData = validatedData.isPresent() ? validatedData : Optional.empty();
    }

    /** Creates a success result with just a message. */
    public static Success of(String message) {
      return new Success(message, Optional.empty());
    }

    /** Creates a success result with validated data. */
    public static Success withData(String message, Object data) {
      return new Success(message, Optional.of(data));
    }
  }

  /** Warning validation result - validation passed but with warnings. */
  record Warning(String message, List<String> warnings, Optional<Object> validatedData)
      implements ValidationResult {
    public Warning {
      if (message == null || message.isBlank()) {
        throw new IllegalArgumentException("Warning message cannot be null or blank");
      }
      if (warnings == null || warnings.isEmpty()) {
        throw new IllegalArgumentException("Warning must have at least one warning message");
      }
      validatedData = validatedData != null ? validatedData : Optional.empty();
    }

    /** Creates a warning result with a single warning. */
    public static Warning of(String message, String warning) {
      return new Warning(message, List.of(warning), Optional.empty());
    }

    /** Creates a warning result with multiple warnings. */
    public static Warning of(String message, List<String> warnings) {
      return new Warning(message, warnings, Optional.empty());
    }

    /** Creates a warning result with validated data. */
    public static Warning withData(String message, List<String> warnings, Object data) {
      return new Warning(message, warnings, Optional.of(data));
    }
  }

  /** Failed validation result with detailed error information. */
  record Failure(
      String message,
      List<String> errors,
      Map<String, String> fieldErrors,
      Optional<String> errorCode,
      Optional<Exception> cause)
      implements ValidationResult {
    public Failure {
      if (message == null || message.isBlank()) {
        throw new IllegalArgumentException("Failure message cannot be null or blank");
      }
      if ((errors == null || errors.isEmpty()) && (fieldErrors == null || fieldErrors.isEmpty())) {
        throw new IllegalArgumentException("Failure must have at least one error or field error");
      }

      errors = errors != null ? errors : List.of();
      fieldErrors = fieldErrors != null ? fieldErrors : Map.of();
      errorCode = errorCode != null ? errorCode : Optional.empty();
      cause = cause != null ? cause : Optional.empty();
    }

    /** Creates a failure result with a single error. */
    public static Failure of(String message, String error) {
      return new Failure(message, List.of(error), Map.of(), Optional.empty(), Optional.empty());
    }

    /** Creates a failure result with multiple errors. */
    public static Failure of(String message, List<String> errors) {
      return new Failure(message, errors, Map.of(), Optional.empty(), Optional.empty());
    }

    /** Creates a failure result with field errors. */
    public static Failure withFieldErrors(String message, Map<String, String> fieldErrors) {
      return new Failure(message, List.of(), fieldErrors, Optional.empty(), Optional.empty());
    }

    /** Creates a failure result with an error code. */
    public static Failure withCode(String message, String error, String errorCode) {
      return new Failure(
          message, List.of(error), Map.of(), Optional.of(errorCode), Optional.empty());
    }

    /** Creates a failure result with a cause exception. */
    public static Failure withCause(String message, String error, Exception cause) {
      return new Failure(message, List.of(error), Map.of(), Optional.empty(), Optional.of(cause));
    }

    /** Gets all error messages as a flat list. */
    public List<String> getAllErrors() {
      List<String> allErrors = new java.util.ArrayList<>(errors);
      fieldErrors.values().forEach(allErrors::add);
      return allErrors;
    }

    /** Gets the total error count. */
    public int getErrorCount() {
      return errors.size() + fieldErrors.size();
    }

    /** Checks if this failure has field-specific errors. */
    public boolean hasFieldErrors() {
      return !fieldErrors.isEmpty();
    }

    /** Checks if this failure has general errors. */
    public boolean hasGeneralErrors() {
      return !errors.isEmpty();
    }
  }

  /** Returns true if validation was successful. */
  default boolean isSuccess() {
    return this instanceof Success;
  }

  /** Returns true if validation had warnings. */
  default boolean isWarning() {
    return this instanceof Warning;
  }

  /** Returns true if validation failed. */
  default boolean isFailure() {
    return this instanceof Failure;
  }

  /** Returns true if validation passed (success or warning). */
  default boolean isPassed() {
    return isSuccess() || isWarning();
  }

  /** Returns true if validation did not fail completely. */
  default boolean isNotFailure() {
    return !isFailure();
  }

  /** Gets the main message for this validation result. */
  default String getMessage() {
    return switch (this) {
      case Success success -> success.message();
      case Warning warning -> warning.message();
      case Failure failure -> failure.message();
    };
  }

  /** Gets validated data if available. */
  default Optional<Object> getValidatedData() {
    return switch (this) {
      case Success success -> success.validatedData();
      case Warning warning -> warning.validatedData();
      case Failure failure -> Optional.empty();
    };
  }

  /** Combines this validation result with another. */
  default ValidationResult combine(ValidationResult other) {
    if (this.isFailure() || other.isFailure()) {
      List<String> allErrors = new java.util.ArrayList<>();
      Map<String, String> allFieldErrors = new java.util.HashMap<>();

      if (this instanceof Failure thisFailure) {
        allErrors.addAll(thisFailure.errors());
        allFieldErrors.putAll(thisFailure.fieldErrors());
      }
      if (other instanceof Failure otherFailure) {
        allErrors.addAll(otherFailure.errors());
        allFieldErrors.putAll(otherFailure.fieldErrors());
      }

      return allFieldErrors.isEmpty()
          ? Failure.of("Combined validation failed", allErrors)
          : Failure.withFieldErrors("Combined validation failed", allFieldErrors);
    }

    if (this.isWarning() || other.isWarning()) {
      List<String> allWarnings = new java.util.ArrayList<>();

      if (this instanceof Warning thisWarning) {
        allWarnings.addAll(thisWarning.warnings());
      }
      if (other instanceof Warning otherWarning) {
        allWarnings.addAll(otherWarning.warnings());
      }

      return allWarnings.isEmpty()
          ? Success.of("Combined validation successful")
          : Warning.of("Combined validation with warnings", allWarnings);
    }

    return Success.of("Combined validation successful");
  }
}
