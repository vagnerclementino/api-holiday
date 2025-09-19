package me.clementino.holiday.exception;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Immutable set of funny 404 error messages following DOP principles. Each message includes
   * emojis and humor to make 404 errors less frustrating.
   */
  private static final Set<String> FUNNY_404_MESSAGES =
      Set.of(
          "🔍 Oops! The resource '%s' seems to have gone on vacation! 🏖️ It's nowhere to be found.",
          "🕵️ Detective mode activated! But even Sherlock couldn't find '%s' 🔍",
          "🚀 Houston, we have a problem! The resource '%s' has left orbit! 🌌",
          "🎭 Plot twist! The resource '%s' is playing hide and seek... and winning! 🙈",
          "🧙‍♂️ *Waves magic wand* ✨ Nope, '%s' is still missing. Magic isn't real! 😅",
          "🐕 Even the best search dogs couldn't sniff out '%s'! Woof! 🐾",
          "🗺️ X marks the spot... but '%s' isn't here! Maybe try a different treasure map? 🏴‍☠️",
          "🎪 Ladies and gentlemen, for my next trick, I'll make '%s' appear! *poof* ...Still working on it! 🎩",
          "🍕 This resource '%s' is like pineapple on pizza - some say it exists, but we can't find it! 🍍",
          "🦄 You're looking for '%s'? That's as rare as finding a unicorn! 🌈",
          "🎮 Game Over! The resource '%s' respawned in a different server! 👾",
          "☕ Error 404: Resource '%s' not found. Coffee break needed! ☕");

  /**
   * Selects a random funny message from the immutable set. Uses ThreadLocalRandom for better
   * performance in concurrent environments.
   */
  private static String getRandomFunnyMessage(String resourcePath) {
    var messages = FUNNY_404_MESSAGES.toArray(String[]::new);
    var randomIndex = ThreadLocalRandom.current().nextInt(messages.length);
    return String.format(messages[randomIndex], resourcePath);
  }

  @ExceptionHandler(HolidayNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleHolidayNotFound(HolidayNotFoundException ex) {
    ErrorResponse error =
        new ErrorResponse(
            OffsetDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Holiday Not Found",
            ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ValidationErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            (error) -> {
              String fieldName = ((FieldError) error).getField();
              String errorMessage = error.getDefaultMessage();
              errors.put(fieldName, errorMessage);
            });

    ValidationErrorResponse errorResponse =
        new ValidationErrorResponse(
            OffsetDateTime.now(), HttpStatus.BAD_REQUEST.value(), "Validation Failed", errors);

    return ResponseEntity.badRequest().body(errorResponse);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
    ErrorResponse error =
        new ErrorResponse(
            OffsetDateTime.now(),
            HttpStatus.NOT_FOUND.value(),
            "Resource Not Found",
            getRandomFunnyMessage(ex.getResourcePath()));
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    ErrorResponse error =
        new ErrorResponse(
            OffsetDateTime.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            ex.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  public record ErrorResponse(OffsetDateTime timestamp, int status, String error, String message) {}

  public record ValidationErrorResponse(
      OffsetDateTime timestamp, int status, String error, Map<String, String> validationErrors) {}
}
