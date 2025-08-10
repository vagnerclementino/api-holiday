package me.clementino.holiday.domain.dop;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Operations for working with holidays. This class demonstrates the DOP principle of separating
 * operations from data - all holiday behavior is implemented here as pure functions that work with
 * immutable holiday records.
 *
 * <p>DOP Principles Applied:
 *
 * <ol>
 *   <li><strong>Separate Operations from Data</strong> - All holiday operations are pure functions
 *   <li><strong>Pattern Matching</strong> - Uses switch expressions for type-safe operations
 *   <li><strong>Immutable Operations</strong> - All operations return new values, never modify
 *       input
 * </ol>
 */
public final class HolidayOperations {

  private HolidayOperations() {
    // Utility class - prevent instantiation
  }

  /** Calculates the date for a holiday in a specific year. */
  public static LocalDate calculateDate(Holiday holiday, int year) {
    Objects.requireNonNull(holiday, "Holiday cannot be null");
    validateYear(year);

    return switch (holiday) {
      case FixedHoliday fixed -> calculateFixedDate(fixed, year);
      case ObservedHoliday observed -> calculateObservedDate(observed, year);
      case MoveableHoliday moveable -> calculateMoveableDate(moveable, year);
      case MoveableFromBaseHoliday derived -> calculateDerivedDate(derived, year);
    };
  }

  /**
   * Calculates the observed date for a holiday in a given year. Applies mondayisation rules if the
   * holiday supports it.
   */
  public static LocalDate calculateObservedDate(Holiday holiday, int year) {
    Objects.requireNonNull(holiday, "Holiday cannot be null");
    validateYear(year);

    return switch (holiday) {
      case FixedHoliday fixed -> calculateFixedDate(fixed, year); // No mondayisation
      case ObservedHoliday observed -> {
        // For observed holidays, return the observed date directly
        yield observed.observed();
      }
      case MoveableHoliday moveable -> {
        LocalDate actualDate = calculateMoveableDate(moveable, year);
        if (moveable.mondayisation()) {
          yield applyMondayisationRules(actualDate);
        }
        yield actualDate;
      }
      case MoveableFromBaseHoliday derived -> {
        LocalDate actualDate = calculateDerivedDate(derived, year);
        if (derived.mondayisation()) {
          yield applyMondayisationRules(actualDate);
        }
        yield actualDate;
      }
    };
  }

  /** Checks if a holiday falls on a weekend for the specified year. */
  public static boolean isWeekend(Holiday holiday, int year) {
    LocalDate holidayDate = calculateDate(holiday, year);
    DayOfWeek dayOfWeek = holidayDate.getDayOfWeek();
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
  }

  /** Formats holiday information for display. Uses pattern matching and destructuring. */
  public static String formatHolidayInfo(Holiday holiday) {
    Objects.requireNonNull(holiday, "Holiday cannot be null");

    var localityInfo = formatLocalities(holiday.localities());
    var typeInfo =
        switch (holiday.type()) {
          case NATIONAL -> "National";
          case STATE -> "State";
          case MUNICIPAL -> "Municipal";
          case RELIGIOUS -> "Religious";
          case COMMERCIAL -> "Commercial";
        };

    return switch (holiday) {
      case FixedHoliday fixed ->
          "%s - %s holiday in %s on %s %d"
              .formatted(
                  fixed.name(),
                  typeInfo,
                  localityInfo,
                  fixed.date().getMonth(),
                  fixed.date().getDayOfMonth());

      case ObservedHoliday observed -> {
        var mondayisationInfo = observed.mondayisation() ? " (with mondayisation)" : "";
        var dateInfo =
            observed.date().equals(observed.observed())
                ? "on %s %d".formatted(observed.date().getMonth(), observed.date().getDayOfMonth())
                : "on %s %d (observed %s %d)"
                    .formatted(
                        observed.date().getMonth(), observed.date().getDayOfMonth(),
                        observed.observed().getMonth(), observed.observed().getDayOfMonth());
        yield "%s - %s holiday in %s %s%s"
            .formatted(observed.name(), typeInfo, localityInfo, dateInfo, mondayisationInfo);
      }

      case MoveableHoliday moveable -> {
        var mondayisationInfo = moveable.mondayisation() ? " (with mondayisation)" : "";
        yield "%s - %s holiday in %s (moveable)%s"
            .formatted(moveable.name(), typeInfo, localityInfo, mondayisationInfo);
      }

      case MoveableFromBaseHoliday derived -> {
        var mondayisationInfo = derived.mondayisation() ? " (with mondayisation)" : "";
        yield "%s - %s holiday in %s (derived from %s, %+d days)%s"
            .formatted(
                derived.name(),
                typeInfo,
                localityInfo,
                derived.baseHoliday().name(),
                derived.dayOffset(),
                mondayisationInfo);
      }
    };
  }

  /** Checks if a holiday applies to a specific locality. Uses hierarchical locality matching. */
  public static boolean appliesTo(Holiday holiday, Locality targetLocality) {
    Objects.requireNonNull(holiday, "Holiday cannot be null");
    Objects.requireNonNull(targetLocality, "Target locality cannot be null");

    return holiday.localities().stream()
        .anyMatch(holidayLocality -> localityMatches(holidayLocality, targetLocality));
  }

  // Private helper methods

  private static LocalDate calculateFixedDate(FixedHoliday fixed, int year) {
    // For fixed holidays, adjust the year but keep the same month and day
    return fixed.date().withYear(year);
  }

  /** Calculates the date for a moveable holiday in a specific year. */
  private static LocalDate calculateMoveableDate(MoveableHoliday moveable, int year) {
    return switch (moveable.knownHoliday()) {
      case EASTER -> calculateEaster(year);
      case THANKSGIVING_US -> calculateThanksgiving(year);
      case MEMORIAL_DAY_US -> calculateMemorialDay(year);
      case LABOR_DAY_US -> calculateLaborDay(year);
      case MOTHERS_DAY -> calculateMothersDay(year);
      case FATHERS_DAY -> calculateFathersDay(year);
      default ->
          throw new IllegalArgumentException(
              "Unsupported moveable holiday: " + moveable.knownHoliday());
    };
  }

  /** Calculates the date for a holiday derived from another holiday. */
  private static LocalDate calculateDerivedDate(MoveableFromBaseHoliday derived, int year) {
    LocalDate baseDate = calculateDate(derived.baseHoliday(), year);
    return baseDate.plusDays(derived.dayOffset());
  }

  /** Applies mondayisation rules to a date. */
  private static LocalDate applyMondayisationRules(LocalDate date) {
    return switch (date.getDayOfWeek()) {
      case SATURDAY -> date.minusDays(1); // Saturday -> Friday
      case SUNDAY -> date.plusDays(1); // Sunday -> Monday
      default -> date; // Weekdays remain unchanged
    };
  }

  /** Formats a list of localities for display. */
  private static String formatLocalities(List<Locality> localities) {
    return localities.stream()
        .map(HolidayOperations::formatLocality)
        .collect(Collectors.joining(", "));
  }

  /** Formats a single locality for display. */
  private static String formatLocality(Locality locality) {
    if (locality.city().isPresent() && locality.subdivision().isPresent()) {
      return "%s, %s, %s"
          .formatted(
              locality.city().get(),
              locality.subdivision().get().name(),
              locality.country().name());
    } else if (locality.subdivision().isPresent()) {
      return "%s, %s".formatted(locality.subdivision().get().name(), locality.country().name());
    } else {
      return locality.country().name();
    }
  }

  /** Checks if a holiday locality matches a target locality using hierarchical matching. */
  private static boolean localityMatches(Locality holidayLocality, Locality targetLocality) {
    // Country must always match
    if (!holidayLocality.country().equals(targetLocality.country())) {
      return false;
    }

    // If holiday is national (no subdivision), it applies everywhere in the country
    if (holidayLocality.subdivision().isEmpty()) {
      return true;
    }

    // If holiday has subdivision but target doesn't, no match
    if (targetLocality.subdivision().isEmpty()) {
      return false;
    }

    // Subdivision must match
    if (!holidayLocality.subdivision().equals(targetLocality.subdivision())) {
      return false;
    }

    // If holiday is state-wide (no city), it applies everywhere in the state
    if (holidayLocality.city().isEmpty()) {
      return true;
    }

    // Both must have cities and they must match
    return holidayLocality.city().equals(targetLocality.city());
  }

  /** Calculates Easter Sunday for a given year using the algorithm from Jean Meeus. */
  public static LocalDate calculateEaster(int year) {
    // Algorithm from "Astronomical Algorithms" by Jean Meeus
    int a = year % 19;
    int b = year / 100;
    int c = year % 100;
    int d = b / 4;
    int e = b % 4;
    int f = (b + 8) / 25;
    int g = (b - f + 1) / 3;
    int h = (19 * a + b - d - g + 15) % 30;
    int i = c / 4;
    int k = c % 4;
    int l = (32 + 2 * e + 2 * i - h - k) % 7;
    int m = (a + 11 * h + 22 * l) / 451;
    int month = (h + l - 7 * m + 114) / 31;
    int day = ((h + l - 7 * m + 114) % 31) + 1;

    return LocalDate.of(year, month, day);
  }

  /** Calculates Thanksgiving (4th Thursday of November) for a given year. */
  public static LocalDate calculateThanksgiving(int year) {
    LocalDate firstOfNovember = LocalDate.of(year, Month.NOVEMBER, 1);
    DayOfWeek firstDayOfWeek = firstOfNovember.getDayOfWeek();

    // Find the first Thursday
    int daysToFirstThursday = (DayOfWeek.THURSDAY.getValue() - firstDayOfWeek.getValue() + 7) % 7;
    LocalDate firstThursday = firstOfNovember.plusDays(daysToFirstThursday);

    // Add 3 weeks to get the 4th Thursday
    return firstThursday.plusWeeks(3);
  }

  /** Calculates Memorial Day (last Monday of May) for a given year. */
  public static LocalDate calculateMemorialDay(int year) {
    LocalDate lastOfMay = LocalDate.of(year, Month.MAY, 31);
    DayOfWeek lastDayOfWeek = lastOfMay.getDayOfWeek();

    // Find the last Monday
    int daysToLastMonday = (lastDayOfWeek.getValue() - DayOfWeek.MONDAY.getValue() + 7) % 7;
    return lastOfMay.minusDays(daysToLastMonday);
  }

  /** Calculates Labor Day (1st Monday of September) for a given year. */
  public static LocalDate calculateLaborDay(int year) {
    LocalDate firstOfSeptember = LocalDate.of(year, Month.SEPTEMBER, 1);
    DayOfWeek firstDayOfWeek = firstOfSeptember.getDayOfWeek();

    // Find the first Monday
    int daysToFirstMonday = (DayOfWeek.MONDAY.getValue() - firstDayOfWeek.getValue() + 7) % 7;
    return firstOfSeptember.plusDays(daysToFirstMonday);
  }

  /** Calculates Mother's Day (2nd Sunday of May) for a given year. */
  public static LocalDate calculateMothersDay(int year) {
    LocalDate firstOfMay = LocalDate.of(year, Month.MAY, 1);
    DayOfWeek firstDayOfWeek = firstOfMay.getDayOfWeek();

    // Find the first Sunday
    int daysToFirstSunday = (DayOfWeek.SUNDAY.getValue() - firstDayOfWeek.getValue() + 7) % 7;
    LocalDate firstSunday = firstOfMay.plusDays(daysToFirstSunday);

    // Add 1 week to get the 2nd Sunday
    return firstSunday.plusWeeks(1);
  }

  /** Calculates Father's Day (3rd Sunday of June) for a given year. */
  public static LocalDate calculateFathersDay(int year) {
    LocalDate firstOfJune = LocalDate.of(year, Month.JUNE, 1);
    DayOfWeek firstDayOfWeek = firstOfJune.getDayOfWeek();

    // Find the first Sunday
    int daysToFirstSunday = (DayOfWeek.SUNDAY.getValue() - firstDayOfWeek.getValue() + 7) % 7;
    LocalDate firstSunday = firstOfJune.plusDays(daysToFirstSunday);

    // Add 2 weeks to get the 3rd Sunday
    return firstSunday.plusWeeks(2);
  }

  private static void validateYear(int year) {
    if (year <= 0) {
      throw new IllegalArgumentException("Year must be positive, got: " + year);
    }
  }
}
