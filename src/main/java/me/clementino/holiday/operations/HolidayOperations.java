package me.clementino.holiday.operations;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
        .filter(holiday -> matchesCountry(holiday, query.country()))
        .filter(holiday -> matchesState(holiday, query.state()))
        .filter(holiday -> matchesCity(holiday, query.city()))
        .filter(holiday -> matchesType(holiday, query.type()))
        .filter(holiday -> matchesDateRange(holiday, query.startDate(), query.endDate()))
        .filter(holiday -> matchesRecurring(holiday, query.recurring()))
        .filter(holiday -> matchesNamePattern(holiday, query.namePattern()))
        .toList();
  }

  /** Applies a command to holiday data, returning the updated data. */
  public HolidayData applyCommand(HolidayData holiday, HolidayCommand.Update command) {
    HolidayData updated = holiday;

    if (command.name().isPresent()) {
      updated = updated.withName(command.name().get());
    }
    if (command.date().isPresent()) {
      updated = updated.withDate(command.date().get());
    }
    if (command.location().isPresent()) {
      updated = updated.withLocation(command.location().get());
    }
    if (command.type().isPresent()) {
      updated = updated.withType(command.type().get());
    }
    if (command.recurring().isPresent()) {
      updated = updated.withRecurring(command.recurring().get());
    }
    if (command.description().isPresent()) {
      updated = updated.withDescription(command.description().get());
    }
    if (command.observed().isPresent()) {
      updated = updated.withObserved(command.observed().get());
    }

    return updated.withMetadata(
        holiday.dateCreated().orElse(null),
        LocalDateTime.now(),
        holiday.version().map(v -> v + 1).orElse(1));
  }

  /** Creates holiday data from a create command. */
  public HolidayData createFromCommand(HolidayCommand.Create command) {
    LocalDateTime now = LocalDateTime.now();
    return new HolidayData(
            command.name(),
            command.date(),
            command.location(),
            command.type(),
            command.recurring(),
            command.description().orElse(null))
        .withMetadata(now, now, 0); // Start with version 0
  }

  // Private validation methods

  private ValidationResult validateNationalHoliday(HolidayData holiday) {
    if (!holiday.location().isNational()) {
      return new ValidationResult.Failure("National holidays must have country-only location");
    }
    return new ValidationResult.Success("National holiday is valid");
  }

  private ValidationResult validateStateHoliday(HolidayData holiday) {
    if (!holiday.location().isState()) {
      return new ValidationResult.Failure("State holidays must have country and state location");
    }
    return new ValidationResult.Success("State holiday is valid");
  }

  private ValidationResult validateMunicipalHoliday(HolidayData holiday) {
    if (!holiday.location().isCity()) {
      return new ValidationResult.Failure(
          "Municipal holidays must have country, state, and city location");
    }
    return new ValidationResult.Success("Municipal holiday is valid");
  }

  private ValidationResult validateReligiousHoliday(HolidayData holiday) {
    // Religious holidays can be at any level
    return new ValidationResult.Success("Religious holiday is valid");
  }

  private ValidationResult validateCommercialHoliday(HolidayData holiday) {
    // Commercial holidays can be at any level
    return new ValidationResult.Success("Commercial holiday is valid");
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
