package me.clementino.holiday.domain.dop;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import me.clementino.holiday.domain.HolidayType;

/**
 * Fixed holiday record - occurs on the same date every year. Examples: Christmas (December 25), New
 * Year (January 1)
 *
 * <p>Based on the OOP FixedHoliday class but as an immutable record following DOP principles.
 *
 * <p>DOP Principles Applied:
 *
 * <ol>
 *   <li><strong>Model Data Immutably and Transparently</strong> - Immutable record with public
 *       fields
 *   <li><strong>Model the Data, the Whole Data, and Nothing but the Data</strong> - Contains
 *       exactly what a fixed holiday needs: date, localities, etc.
 *   <li><strong>Make Illegal States Unrepresentable</strong> - Validation prevents invalid states
 *   <li><strong>Separate Operations from Data</strong> - No behavior methods, only data. Date
 *       calculations extract day/month from the stored date.
 * </ol>
 */
public record FixedHoliday(
    String name, String description, LocalDate date, List<Locality> localities, HolidayType type)
    implements Holiday {

  // Compact constructor for validation
  public FixedHoliday {
    Objects.requireNonNull(name, "Holiday name cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(localities, "Localities cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");
    description = Objects.requireNonNullElse(description, "");

    if (name.isBlank()) {
      throw new IllegalArgumentException("Holiday name cannot be blank");
    }

    if (localities.isEmpty()) {
      throw new IllegalArgumentException("At least one locality must be specified");
    }
  }

  // Transformation methods (instead of setters)
  public FixedHoliday withName(String newName) {
    return new FixedHoliday(newName, description, date, localities, type);
  }

  public FixedHoliday withDescription(String newDescription) {
    return new FixedHoliday(name, newDescription, date, localities, type);
  }

  public FixedHoliday withDate(LocalDate newDate) {
    return new FixedHoliday(name, description, newDate, localities, type);
  }

  public FixedHoliday withLocalities(List<Locality> newLocalities) {
    return new FixedHoliday(name, description, date, newLocalities, type);
  }

  public FixedHoliday withType(HolidayType newType) {
    return new FixedHoliday(name, description, date, localities, newType);
  }
}
