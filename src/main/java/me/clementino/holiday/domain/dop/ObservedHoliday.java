package me.clementino.holiday.domain.dop;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import me.clementino.holiday.domain.HolidayType;

/**
 * Observed holiday record - a holiday that can be "observed" on a different date due to
 * mondayisation rules (weekend adjustments). Examples: Christmas when it falls on weekend might be
 * observed on Monday, New Year when it falls on Sunday might be observed on Monday.
 *
 * <p>This represents holidays that have official "observed" dates that differ from their actual
 * calendar date when they fall on weekends.
 *
 * <p>DOP Principles Applied:
 *
 * <ol>
 *   <li><strong>Model Data Immutably and Transparently</strong> - Immutable record with public
 *       fields
 *   <li><strong>Model the Data, the Whole Data, and Nothing but the Data</strong> - Contains
 *       exactly what an observed holiday needs
 *   <li><strong>Make Illegal States Unrepresentable</strong> - Validation prevents invalid states
 *   <li><strong>Separate Operations from Data</strong> - No behavior methods, only data
 * </ol>
 */
public record ObservedHoliday(
    String name,
    String description,
    LocalDate date, // Original/scheduled date of the holiday
    LocalDate observed, // Date when the holiday is actually observed
    List<Locality> localities,
    HolidayType type,
    boolean mondayisation)
    implements Holiday {

  // Compact constructor for validation
  public ObservedHoliday {
    Objects.requireNonNull(name, "Holiday name cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(observed, "Holiday observed date cannot be null");
    Objects.requireNonNull(localities, "Localities cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");
    description = Objects.requireNonNullElse(description, "");

    if (name.isBlank()) {
      throw new IllegalArgumentException("Holiday name cannot be blank");
    }

    if (localities.isEmpty()) {
      throw new IllegalArgumentException("At least one locality must be specified");
    }

    // Validate that observed date makes sense relative to original date
    if (mondayisation && observed.equals(date)) {
      // If mondayisation is enabled but observed equals original date,
      // it means the original date was already on a weekday
      var dayOfWeek = date.getDayOfWeek();
      if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
        throw new IllegalArgumentException(
            "Mondayisation is enabled but observed date equals original weekend date. "
                + "Expected observed date to be adjusted for weekend.");
      }
    }
  }

  // Transformation methods (instead of setters)
  public ObservedHoliday withName(String newName) {
    return new ObservedHoliday(
        newName, description, date, observed, localities, type, mondayisation);
  }

  public ObservedHoliday withDescription(String newDescription) {
    return new ObservedHoliday(
        name, newDescription, date, observed, localities, type, mondayisation);
  }

  public ObservedHoliday withDate(LocalDate newDate) {
    return new ObservedHoliday(
        name, description, newDate, observed, localities, type, mondayisation);
  }

  public ObservedHoliday withObserved(LocalDate newObserved) {
    return new ObservedHoliday(
        name, description, date, newObserved, localities, type, mondayisation);
  }

  public ObservedHoliday withLocalities(List<Locality> newLocalities) {
    return new ObservedHoliday(
        name, description, date, observed, newLocalities, type, mondayisation);
  }

  public ObservedHoliday withType(HolidayType newType) {
    return new ObservedHoliday(
        name, description, date, observed, localities, newType, mondayisation);
  }

  public ObservedHoliday withMondayisation(boolean newMondayisation) {
    return new ObservedHoliday(
        name, description, date, observed, localities, type, newMondayisation);
  }
}
