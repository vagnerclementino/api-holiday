package me.clementino.holiday.domain.dop;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import me.clementino.holiday.domain.HolidayType;

/**
 * Moveable holiday record - holidays with dates calculated based on complex rules. Examples: Easter
 * (lunar-based), Thanksgiving (weekday-based).
 *
 * <p>This represents holidays that have their dates calculated using algorithms, such as Easter
 * (based on lunar calendar) or Thanksgiving (4th Thursday of November).
 *
 * <p>Based on the OOP MoveableHoliday class but simplified to focus on self-calculating holidays.
 * For holidays derived from other holidays, use {@link MoveableFromBaseHoliday}.
 *
 * <p>DOP Principles Applied:
 *
 * <ol>
 *   <li><strong>Model Data Immutably and Transparently</strong> - Immutable record with public
 *       fields
 *   <li><strong>Model the Data, the Whole Data, and Nothing but the Data</strong> - Contains
 *       exactly what a moveable holiday needs
 *   <li><strong>Make Illegal States Unrepresentable</strong> - Validation prevents invalid states,
 *       enum ensures valid holiday types
 *   <li><strong>Separate Operations from Data</strong> - No behavior methods, only data. Date
 *       calculations handled by HolidayOperations
 * </ol>
 */
public record MoveableHoliday(
    KnownHoliday knownHoliday, // Standardized holiday identifier
    String description,
    LocalDate date, // Pre-calculated date for a specific year
    List<Locality> localities,
    HolidayType type,
    boolean mondayisation)
    implements Holiday {

  // Compact constructor for validation
  public MoveableHoliday {
    Objects.requireNonNull(knownHoliday, "Known holiday cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(localities, "Localities cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");
    description = Objects.requireNonNullElse(description, knownHoliday.getDescription());

    if (!knownHoliday.isMoveable()) {
      throw new IllegalArgumentException("Holiday " + knownHoliday + " is not a moveable holiday");
    }

    if (knownHoliday.isDerived()) {
      throw new IllegalArgumentException(
          "Holiday "
              + knownHoliday
              + " is derived from another holiday. Use MoveableFromBaseHoliday instead.");
    }

    if (localities.isEmpty()) {
      throw new IllegalArgumentException("At least one locality must be specified");
    }
  }

  // Convenience method to get the standardized name
  public String name() {
    return knownHoliday.getDisplayName();
  }

  // Transformation methods (instead of setters)
  public MoveableHoliday withKnownHoliday(KnownHoliday newKnownHoliday) {
    return new MoveableHoliday(newKnownHoliday, description, date, localities, type, mondayisation);
  }

  public MoveableHoliday withDescription(String newDescription) {
    return new MoveableHoliday(knownHoliday, newDescription, date, localities, type, mondayisation);
  }

  public MoveableHoliday withDate(LocalDate newDate) {
    return new MoveableHoliday(knownHoliday, description, newDate, localities, type, mondayisation);
  }

  public MoveableHoliday withLocalities(List<Locality> newLocalities) {
    return new MoveableHoliday(knownHoliday, description, date, newLocalities, type, mondayisation);
  }

  public MoveableHoliday withType(HolidayType newType) {
    return new MoveableHoliday(knownHoliday, description, date, localities, newType, mondayisation);
  }

  public MoveableHoliday withMondayisation(boolean newMondayisation) {
    return new MoveableHoliday(knownHoliday, description, date, localities, type, newMondayisation);
  }
}
