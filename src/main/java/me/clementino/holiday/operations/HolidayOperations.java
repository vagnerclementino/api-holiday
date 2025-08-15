package me.clementino.holiday.operations;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import me.clementino.holiday.domain.*;
import org.springframework.stereotype.Component;

/**
 * Pure operations on holiday data. Following DOP v1.1 Principle 4: Separate Operations From Data.
 *
 * <p>This class contains only pure functions that work with immutable data. No state is stored here
 * - all operations are stateless and side-effect free.
 */
@Component
public class HolidayOperations {

  /** Validates a holiday based on its type and location. */
  public ValidationResult validateHoliday(HolidayData holiday) {
    return switch (holiday.type()) {
      case NATIONAL -> validateNationalHoliday(holiday);
      case STATE -> validateStateHoliday(holiday);
      case MUNICIPAL -> validateMunicipalHoliday(holiday);
      case RELIGIOUS -> validateReligiousHoliday(holiday);
      case COMMERCIAL -> validateCommercialHoliday(holiday);
    };
  }

  /** Formats holiday information for display. */
  public String formatHolidayInfo(HolidayData holiday) {
    Location location = holiday.location();
    if (location.isNational()) {
      return "%s - National holiday in %s".formatted(holiday.name(), location.country());
    } else if (location.isState()) {
      return "%s - State holiday in %s, %s"
          .formatted(holiday.name(), location.state().get(), location.country());
    } else if (location.isCity()) {
      return "%s - Municipal holiday in %s, %s, %s"
          .formatted(
              holiday.name(), location.city().get(), location.state().get(), location.country());
    } else {
      return "%s - Holiday".formatted(holiday.name());
    }
  }

  /** Calculates the next occurrence of a recurring holiday. */
  public Optional<LocalDate> calculateNextOccurrence(HolidayData holiday, LocalDate fromDate) {
    if (!holiday.recurring()) {
      return Optional.empty();
    }

    LocalDate holidayDate = holiday.date();
    int currentYear = fromDate.getYear();

    // Try current year first
    LocalDate thisYear = holidayDate.withYear(currentYear);
    if (thisYear.isAfter(fromDate)) {
      return Optional.of(thisYear);
    }

    // Try next year
    return Optional.of(holidayDate.withYear(currentYear + 1));
  }

  /** Filters holidays based on query criteria. */
  public List<HolidayData> filterHolidays(List<HolidayData> holidays, HolidayQuery query) {
    return holidays.stream()
        .filter(holiday -> matchesCountry(holiday, query.countryCode()))
        .filter(holiday -> matchesState(holiday, query.subdivisionCode()))
        .filter(holiday -> matchesCity(holiday, query.cityName()))
        .filter(holiday -> matchesType(holiday, query.type()))
        .filter(holiday -> matchesDateRange(holiday, query.startDate(), query.endDate()))
        .filter(holiday -> matchesRecurring(holiday, Optional.empty()))
        .filter(holiday -> matchesNamePattern(holiday, query.namePattern()))
        .toList();
  }

  /** Applies a command to holiday data, returning the updated data. */
  public HolidayData applyCommand(HolidayData holiday, HolidayCommand.Update command) {
    // TODO: Update this method to work with new DOP Holiday structure
    throw new UnsupportedOperationException(
        "Implementation pending - needs DOP Holiday conversion");
  }

  /** Creates holiday data from a create command. */
  public HolidayData createFromCommand(HolidayCommand.Create command) {
    // TODO: Update this method to work with new DOP Holiday structure
    throw new UnsupportedOperationException(
        "Implementation pending - needs DOP Holiday conversion");
  }

  // Private validation methods

  private ValidationResult validateNationalHoliday(HolidayData holiday) {
    if (!holiday.location().isNational()) {
      return ValidationResult.Failure.of(
          "Validation failed", "National holidays must have country-only location");
    }
    return ValidationResult.Success.of("National holiday is valid");
  }

  private ValidationResult validateStateHoliday(HolidayData holiday) {
    if (!holiday.location().isState()) {
      return ValidationResult.Failure.of(
          "Validation failed", "State holidays must have country and state location");
    }
    return ValidationResult.Success.of("State holiday is valid");
  }

  private ValidationResult validateMunicipalHoliday(HolidayData holiday) {
    if (!holiday.location().isCity()) {
      return ValidationResult.Failure.of(
          "Validation failed", "Municipal holidays must have country, state, and city location");
    }
    return ValidationResult.Success.of("Municipal holiday is valid");
  }

  private ValidationResult validateReligiousHoliday(HolidayData holiday) {
    // Religious holidays can be at any level
    return ValidationResult.Success.of("Religious holiday is valid");
  }

  private ValidationResult validateCommercialHoliday(HolidayData holiday) {
    // Commercial holidays can be at any level
    return ValidationResult.Success.of("Commercial holiday is valid");
  }

  // Private filter methods

  private boolean matchesCountry(HolidayData holiday, Optional<String> country) {
    return country.isEmpty() || holiday.location().country().equalsIgnoreCase(country.get());
  }

  private boolean matchesState(HolidayData holiday, Optional<String> state) {
    return state.isEmpty()
        || holiday.location().state().map(s -> s.equalsIgnoreCase(state.get())).orElse(false);
  }

  private boolean matchesCity(HolidayData holiday, Optional<String> city) {
    return city.isEmpty()
        || holiday.location().city().map(c -> c.equalsIgnoreCase(city.get())).orElse(false);
  }

  private boolean matchesType(HolidayData holiday, Optional<HolidayType> type) {
    return type.isEmpty() || holiday.type() == type.get();
  }

  private boolean matchesDateRange(
      HolidayData holiday, Optional<LocalDate> startDate, Optional<LocalDate> endDate) {
    LocalDate holidayDate = holiday.date();

    if (startDate.isPresent() && holidayDate.isBefore(startDate.get())) {
      return false;
    }

    if (endDate.isPresent() && holidayDate.isAfter(endDate.get())) {
      return false;
    }

    return true;
  }

  private boolean matchesRecurring(HolidayData holiday, Optional<Boolean> recurring) {
    return recurring.isEmpty() || holiday.recurring() == recurring.get();
  }

  private boolean matchesNamePattern(HolidayData holiday, Optional<String> namePattern) {
    return namePattern.isEmpty()
        || holiday.name().toLowerCase().contains(namePattern.get().toLowerCase());
  }
}
