package me.clementino.holiday.domain.dop;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * Holiday operations following DOP principles.
 *
 * <p>This class contains pure functions that operate on Holiday data without modifying the original
 * data. All operations return new instances following immutability principles.
 */
@Component
public class HolidayOperations {

  /**
   * Calculate the observed date for a holiday.
   *
   * <p>Business rules: - If holiday falls on Saturday, observe on Friday - If holiday falls on
   * Sunday, observe on Monday - Otherwise, observe on the actual date
   *
   * @param holiday the holiday to calculate observed date for
   * @return new Holiday instance with calculated observed date
   */
  public Holiday calculateObservedDate(Holiday holiday) {
    LocalDate actualDate = holiday.date();
    DayOfWeek dayOfWeek = actualDate.getDayOfWeek();

    LocalDate observedDate =
        switch (dayOfWeek) {
          case SATURDAY -> actualDate.minusDays(1); // Friday
          case SUNDAY -> actualDate.plusDays(1); // Monday
          default -> actualDate; // Same day
        };

    // Only set observed date if it's different from actual date
    Optional<LocalDate> newObservedDate =
        observedDate.equals(actualDate) ? Optional.empty() : Optional.of(observedDate);

    return holiday.withObservedDate(newObservedDate);
  }

  /**
   * Calculate the next occurrence of a recurring holiday.
   *
   * @param holiday the recurring holiday
   * @param year the target year
   * @return new Holiday instance for the specified year
   */
  public Holiday calculateNextOccurrence(Holiday holiday, int year) {
    if (!holiday.recurring()) {
      throw new IllegalArgumentException("Holiday must be recurring to calculate next occurrence");
    }

    LocalDate originalDate = holiday.date();
    LocalDate nextDate = originalDate.withYear(year);

    Holiday nextHoliday = holiday.withDate(nextDate);
    return calculateObservedDate(nextHoliday);
  }

  /**
   * Validate if a holiday date is valid for its type.
   *
   * @param holiday the holiday to validate
   * @return validation result
   */
  public ValidationResult validateHoliday(Holiday holiday) {
    return switch (holiday.type()) {
      case NATIONAL -> validateNationalHoliday(holiday);
      case STATE -> validateStateHoliday(holiday);
      case MUNICIPAL -> validateMunicipalHoliday(holiday);
      case RELIGIOUS -> validateReligiousHoliday(holiday);
      case COMMERCIAL -> validateCommercialHoliday(holiday);
    };
  }

  /**
   * Check if a holiday falls on a weekend.
   *
   * @param holiday the holiday to check
   * @return true if holiday falls on weekend
   */
  public boolean isWeekendHoliday(Holiday holiday) {
    DayOfWeek dayOfWeek = holiday.date().getDayOfWeek();
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
  }

  /**
   * Get the day of week for a holiday.
   *
   * @param holiday the holiday
   * @return the day of week
   */
  public DayOfWeek getDayOfWeek(Holiday holiday) {
    return holiday.date().getDayOfWeek();
  }

  /**
   * Check if two holidays conflict (same date and overlapping location).
   *
   * @param holiday1 first holiday
   * @param holiday2 second holiday
   * @return true if holidays conflict
   */
  public boolean hasConflict(Holiday holiday1, Holiday holiday2) {
    // Same date check
    if (!holiday1.date().equals(holiday2.date())) {
      return false;
    }

    // Location overlap check
    return hasLocationOverlap(holiday1.location(), holiday2.location());
  }

  private ValidationResult validateNationalHoliday(Holiday holiday) {
    // National holidays should only have country specified
    Location location = holiday.location();
    if (location.state().isPresent() || location.city().isPresent()) {
      return new ValidationResult.Failure("National holidays should not specify state or city");
    }
    return new ValidationResult.Success("Valid national holiday");
  }

  private ValidationResult validateStateHoliday(Holiday holiday) {
    // State holidays should have country and state, but not city
    Location location = holiday.location();
    if (location.state().isEmpty()) {
      return new ValidationResult.Failure("State holidays must specify a state");
    }
    if (location.city().isPresent()) {
      return new ValidationResult.Failure("State holidays should not specify city");
    }
    return new ValidationResult.Success("Valid state holiday");
  }

  private ValidationResult validateMunicipalHoliday(Holiday holiday) {
    // Municipal holidays should have country, state, and city
    Location location = holiday.location();
    if (location.state().isEmpty() || location.city().isEmpty()) {
      return new ValidationResult.Failure("Municipal holidays must specify state and city");
    }
    return new ValidationResult.Success("Valid municipal holiday");
  }

  private ValidationResult validateReligiousHoliday(Holiday holiday) {
    // Religious holidays can be at any level
    return new ValidationResult.Success("Valid religious holiday");
  }

  private ValidationResult validateCommercialHoliday(Holiday holiday) {
    // Commercial holidays can be at any level
    return new ValidationResult.Success("Valid commercial holiday");
  }

  private boolean hasLocationOverlap(Location loc1, Location loc2) {
    // Check country overlap
    if (!loc1.country().equals(loc2.country())) {
      return false;
    }

    // If both have states, check state overlap
    if (loc1.state().isPresent() && loc2.state().isPresent()) {
      if (!loc1.state().get().equals(loc2.state().get())) {
        return false;
      }
    }

    // If both have cities, check city overlap
    if (loc1.city().isPresent() && loc2.city().isPresent()) {
      return loc1.city().get().equals(loc2.city().get());
    }

    // If one is more general than the other, they overlap
    return true;
  }

  /** Sealed interface for validation results. */
  public sealed interface ValidationResult
      permits ValidationResult.Success, ValidationResult.Failure {

    record Success(String message) implements ValidationResult {}

    record Failure(String message) implements ValidationResult {}
  }
}
