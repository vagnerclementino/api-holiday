package me.clementino.holiday.exception;

/** Exception thrown when a holiday is not found. */
public class HolidayNotFoundException extends RuntimeException {

  public HolidayNotFoundException(String message) {
    super(message);
  }

  public HolidayNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
