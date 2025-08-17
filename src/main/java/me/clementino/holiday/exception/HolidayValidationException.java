package me.clementino.holiday.exception;

import java.util.List;

/** Exception thrown when holiday validation fails. */
public class HolidayValidationException extends RuntimeException {

  private final List<String> errors;

  public HolidayValidationException(List<String> errors) {
    super("Holiday validation failed: " + String.join(", ", errors));
    this.errors = List.copyOf(errors);
  }

  public HolidayValidationException(String error) {
    this(List.of(error));
  }

  public List<String> getErrors() {
    return errors;
  }
}
