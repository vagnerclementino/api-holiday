package me.clementino.holiday.domain.dop;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import me.clementino.holiday.domain.HolidayType;

/**
 * Moveable holiday derived from another holiday record. Examples: Good Friday (2 days before
 * Easter), Easter Monday (1 day after Easter).
 *
 * <p>This represents holidays that are calculated relative to other holidays, such as Good Friday
 * (Easter - 2 days) or Palm Sunday (Easter - 7 days).
 *
 * <p>DOP Principles Applied:
 *
 * <ol>
 *   <li><strong>Model Data Immutably and Transparently</strong> - Immutable record with public
 *       fields
 *   <li><strong>Model the Data, the Whole Data, and Nothing but the Data</strong> - Contains
 *       exactly what a derived holiday needs: base holiday and offset
 *   <li><strong>Make Illegal States Unrepresentable</strong> - Validation prevents invalid states,
 *       enum ensures valid holiday types
 *   <li><strong>Separate Operations from Data</strong> - No behavior methods, only data. Date
 *       calculations handled by HolidayOperations
 * </ol>
 */
public record MoveableFromBaseHoliday(
    KnownHoliday knownHoliday, // Standardized holiday identifier (must be derived)
    String description,
    LocalDate date, // Pre-calculated date for a specific year
    List<Locality> localities,
    HolidayType type,
    Holiday baseHoliday, // The holiday this is derived from (e.g., Easter for Good Friday)
    int dayOffset, // Days to add/subtract from base holiday (-2 for Good Friday, +1 for Easter
    // Monday)
    boolean mondayisation)
    implements Holiday {

  // Compact constructor for validation
  public MoveableFromBaseHoliday {
    Objects.requireNonNull(knownHoliday, "Known holiday cannot be null");
    Objects.requireNonNull(date, "Holiday date cannot be null");
    Objects.requireNonNull(localities, "Localities cannot be null");
    Objects.requireNonNull(type, "Holiday type cannot be null");
    Objects.requireNonNull(baseHoliday, "Base holiday cannot be null");
    description = Objects.requireNonNullElse(description, knownHoliday.getDescription());

    if (!knownHoliday.isDerived()) {
      throw new IllegalArgumentException(
          "Holiday "
              + knownHoliday
              + " is not derived from another holiday. Use MoveableHoliday instead.");
    }

    if (localities.isEmpty()) {
      throw new IllegalArgumentException("At least one locality must be specified");
    }

    // Validate that the base holiday matches the expected base for this known holiday
    var expectedBaseHoliday = knownHoliday.getBaseHoliday();
    if (baseHoliday instanceof MoveableHoliday moveableBase) {
      if (moveableBase.knownHoliday() != expectedBaseHoliday) {
        throw new IllegalArgumentException(
            "Base holiday mismatch. Expected "
                + expectedBaseHoliday
                + " but got "
                + moveableBase.knownHoliday());
      }
    } else if (baseHoliday instanceof FixedHoliday fixedBase) {
      // For now, we assume derived holidays are based on moveable holidays like Easter
      throw new IllegalArgumentException(
          "Derived holiday "
              + knownHoliday
              + " should be based on a moveable holiday, not a fixed holiday");
    }

    // Validate that the day offset matches the expected offset
    var expectedOffset = knownHoliday.getDayOffset();
    if (dayOffset != expectedOffset) {
      throw new IllegalArgumentException(
          "Day offset mismatch for "
              + knownHoliday
              + ". Expected "
              + expectedOffset
              + " but got "
              + dayOffset);
    }
  }

  // Convenience method to get the standardized name
  public String name() {
    return knownHoliday.getDisplayName();
  }

  // Transformation methods (instead of setters)
  public MoveableFromBaseHoliday withKnownHoliday(KnownHoliday newKnownHoliday) {
    return new MoveableFromBaseHoliday(
        newKnownHoliday,
        description,
        date,
        localities,
        type,
        baseHoliday,
        dayOffset,
        mondayisation);
  }

  public MoveableFromBaseHoliday withDescription(String newDescription) {
    return new MoveableFromBaseHoliday(
        knownHoliday,
        newDescription,
        date,
        localities,
        type,
        baseHoliday,
        dayOffset,
        mondayisation);
  }

  public MoveableFromBaseHoliday withDate(LocalDate newDate) {
    return new MoveableFromBaseHoliday(
        knownHoliday,
        description,
        newDate,
        localities,
        type,
        baseHoliday,
        dayOffset,
        mondayisation);
  }

  public MoveableFromBaseHoliday withLocalities(List<Locality> newLocalities) {
    return new MoveableFromBaseHoliday(
        knownHoliday,
        description,
        date,
        newLocalities,
        type,
        baseHoliday,
        dayOffset,
        mondayisation);
  }

  public MoveableFromBaseHoliday withType(HolidayType newType) {
    return new MoveableFromBaseHoliday(
        knownHoliday,
        description,
        date,
        localities,
        newType,
        baseHoliday,
        dayOffset,
        mondayisation);
  }

  public MoveableFromBaseHoliday withBaseHoliday(Holiday newBaseHoliday) {
    return new MoveableFromBaseHoliday(
        knownHoliday,
        description,
        date,
        localities,
        type,
        newBaseHoliday,
        dayOffset,
        mondayisation);
  }

  public MoveableFromBaseHoliday withDayOffset(int newDayOffset) {
    return new MoveableFromBaseHoliday(
        knownHoliday,
        description,
        date,
        localities,
        type,
        baseHoliday,
        newDayOffset,
        mondayisation);
  }

  public MoveableFromBaseHoliday withMondayisation(boolean newMondayisation) {
    return new MoveableFromBaseHoliday(
        knownHoliday,
        description,
        date,
        localities,
        type,
        baseHoliday,
        dayOffset,
        newMondayisation);
  }
}
