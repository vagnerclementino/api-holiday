package me.clementino.holiday.domain.dop;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayType;

/**
 * Moveable holiday record - date changes based on calculation rules. Examples: Easter, Thanksgiving
 * (4th Thursday of November), Labor Day (1st Monday of September)
 *
 * <p>Based on the OOP MoveableHoliday class but as an immutable record following DOP principles.
 *
 * <p>DOP Principles Applied: 1. Model Data Immutably and Transparently - Immutable record with
 * public fields 2. Model the Data, the Whole Data, and Nothing but the Data - Contains exactly what
 * a moveable holiday needs 3. Make Illegal States Unrepresentable - Validation prevents invalid
 * states 4. Separate Operations from Data - No behavior methods, only data
 */
public record MoveableHoliday(
    String name,
    String description,
    LocalDate date,
    List<Locality> localities,
    HolidayType type,
    boolean mondayisation,
    MoveableHolidayType moveableType,
    Optional<Holiday> baseHoliday,
    int dayOffset)
    implements Holiday {

  // Compact constructor for validation
  public MoveableHoliday {
    Objects.requireNonNull(name, "Holiday name cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(localities, "Localities cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");
    Objects.requireNonNull(moveableType, "Moveable type cannot be null");
    description = Objects.requireNonNullElse(description, "");
    baseHoliday = Objects.requireNonNullElse(baseHoliday, Optional.empty());

    if (name.isBlank()) {
      throw new IllegalArgumentException("Holiday name cannot be blank");
    }

    if (localities.isEmpty()) {
      throw new IllegalArgumentException("At least one locality must be specified");
    }

    // Validate that relative holidays have a base holiday
    if (moveableType == MoveableHolidayType.RELATIVE_TO_HOLIDAY && baseHoliday.isEmpty()) {
      throw new IllegalArgumentException("Base holiday is required for relative holidays");
    }
  }

  // Transformation methods (instead of setters)
  public MoveableHoliday withName(String newName) {
    return new MoveableHoliday(
        newName,
        description,
        date,
        localities,
        type,
        mondayisation,
        moveableType,
        baseHoliday,
        dayOffset);
  }

  public MoveableHoliday withDescription(String newDescription) {
    return new MoveableHoliday(
        name,
        newDescription,
        date,
        localities,
        type,
        mondayisation,
        moveableType,
        baseHoliday,
        dayOffset);
  }

  public MoveableHoliday withDate(LocalDate newDate) {
    return new MoveableHoliday(
        name,
        description,
        newDate,
        localities,
        type,
        mondayisation,
        moveableType,
        baseHoliday,
        dayOffset);
  }

  public MoveableHoliday withLocalities(List<Locality> newLocalities) {
    return new MoveableHoliday(
        name,
        description,
        date,
        newLocalities,
        type,
        mondayisation,
        moveableType,
        baseHoliday,
        dayOffset);
  }

  public MoveableHoliday withType(HolidayType newType) {
    return new MoveableHoliday(
        name,
        description,
        date,
        localities,
        newType,
        mondayisation,
        moveableType,
        baseHoliday,
        dayOffset);
  }

  public MoveableHoliday withMondayisation(boolean newMondayisation) {
    return new MoveableHoliday(
        name,
        description,
        date,
        localities,
        type,
        newMondayisation,
        moveableType,
        baseHoliday,
        dayOffset);
  }

  public MoveableHoliday withMoveableType(MoveableHolidayType newMoveableType) {
    return new MoveableHoliday(
        name,
        description,
        date,
        localities,
        type,
        mondayisation,
        newMoveableType,
        baseHoliday,
        dayOffset);
  }

  public MoveableHoliday withBaseHoliday(Optional<Holiday> newBaseHoliday) {
    return new MoveableHoliday(
        name,
        description,
        date,
        localities,
        type,
        mondayisation,
        moveableType,
        newBaseHoliday,
        dayOffset);
  }

  public MoveableHoliday withDayOffset(int newDayOffset) {
    return new MoveableHoliday(
        name,
        description,
        date,
        localities,
        type,
        mondayisation,
        moveableType,
        baseHoliday,
        newDayOffset);
  }
}
