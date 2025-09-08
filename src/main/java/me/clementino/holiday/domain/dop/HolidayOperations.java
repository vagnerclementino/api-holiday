package me.clementino.holiday.domain.dop;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

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
 *   <li><strong>Immutable Operations</strong> - All operations return new instances, never modify
 *       input
 * </ol>
 */
@Component
public final class HolidayOperations {

  /**
   * Calculates the date for a holiday in a specific year and returns a new holiday instance with
   * the calculated date. This method follows DOP principles by returning new immutable instances
   * rather than modifying existing ones.
   */
  public Holiday calculateDate(Holiday holiday, int year) {
    Objects.requireNonNull(holiday, "Holiday cannot be null");
    validateYear(year);

    return switch (holiday) {
      case FixedHoliday fixed -> {
        LocalDate newDate = calculateFixedDate(fixed, year);
        yield fixed.withDate(newDate);
      }
      case ObservedHoliday observed -> {
        LocalDate newDate = calculateFixedDate(observed, year);
        LocalDate newObserved =
            observed.mondayisation() ? applyMondayisationRules(newDate) : newDate;
        yield observed.withDate(newDate).withObserved(newObserved);
      }
      case MoveableHoliday moveable -> {
        LocalDate newDate = calculateMoveableDate(moveable, year);
        yield moveable.withDate(newDate);
      }
      case MoveableFromBaseHoliday derived -> {
        LocalDate newDate = calculateDerivedDate(derived, year);
        yield derived.withDate(newDate);
      }
    };
  }

  /**
   * Calculates the observed date for a holiday in a given year and returns a new holiday instance
   * with both the original date and observed date calculated. Applies mondayisation rules if the
   * holiday supports it.
   */
  public Holiday calculateObservedDate(Holiday holiday, int year) {
    Objects.requireNonNull(holiday, "Holiday cannot be null");
    validateYear(year);

    return switch (holiday) {
      case FixedHoliday fixed -> {
        LocalDate newDate = calculateFixedDate(fixed, year);
        yield fixed.withDate(newDate);
      }
      case ObservedHoliday observed -> {
        LocalDate newDate = calculateFixedDate(observed, year);
        LocalDate newObserved =
            observed.mondayisation() ? applyMondayisationRules(newDate) : newDate;
        yield observed.withDate(newDate).withObserved(newObserved);
      }
      case MoveableHoliday moveable -> {
        LocalDate newDate = calculateMoveableDate(moveable, year);
        LocalDate observedDate =
            moveable.mondayisation() ? applyMondayisationRules(newDate) : newDate;

        if (moveable.mondayisation() && !newDate.equals(observedDate)) {
          yield new ObservedHoliday(
              moveable.name(),
              moveable.description(),
              newDate,
              moveable.localities(),
              moveable.type(),
              observedDate,
              true);
        } else {
          yield moveable.withDate(newDate);
        }
      }
      case MoveableFromBaseHoliday derived -> {
        LocalDate newDate = calculateDerivedDate(derived, year);
        LocalDate observedDate =
            derived.mondayisation() ? applyMondayisationRules(newDate) : newDate;

        if (derived.mondayisation() && !newDate.equals(observedDate)) {
          yield new ObservedHoliday(
              derived.name(),
              derived.description(),
              newDate,
              derived.localities(),
              derived.type(),
              observedDate,
              true);
        } else {
          yield derived.withDate(newDate);
        }
      }
    };
  }

  /**
   * Helper method to get just the LocalDate for a holiday in a specific year. Useful for
   * calculations that only need the date value.
   */
  public LocalDate getDateOnly(Holiday holiday, int year) {
    Objects.requireNonNull(holiday, "Holiday cannot be null");
    validateYear(year);

    return switch (holiday) {
      case FixedHoliday fixed -> calculateFixedDate(fixed, year);
      case ObservedHoliday observed -> calculateFixedDate(observed, year);
      case MoveableHoliday moveable -> calculateMoveableDate(moveable, year);
      case MoveableFromBaseHoliday derived -> calculateDerivedDate(derived, year);
    };
  }

  /**
   * Helper method to get just the observed LocalDate for a holiday in a specific year. Useful for
   * calculations that only need the observed date value.
   */
  public LocalDate getObservedDateOnly(Holiday holiday, int year) {
    Objects.requireNonNull(holiday, "Holiday cannot be null");
    validateYear(year);

    return switch (holiday) {
      case FixedHoliday fixed -> calculateFixedDate(fixed, year);
      case ObservedHoliday observed -> observed.observed();
      case MoveableHoliday moveable -> {
        LocalDate date = calculateMoveableDate(moveable, year);
        yield moveable.mondayisation() ? applyMondayisationRules(date) : date;
      }
      case MoveableFromBaseHoliday derived -> {
        LocalDate date = calculateDerivedDate(derived, year);
        yield derived.mondayisation() ? applyMondayisationRules(date) : date;
      }
    };
  }

  /** Checks if a holiday falls on a weekend for the specified year. */
  public boolean isWeekend(Holiday holiday, int year) {
    LocalDate holidayDate = getDateOnly(holiday, year);
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

  private static LocalDate calculateFixedDate(Holiday holiday, int year) {
    return holiday.date().withYear(year);
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
  private LocalDate calculateDerivedDate(MoveableFromBaseHoliday derived, int year) {
    LocalDate baseDate = getDateOnly(derived.baseHoliday(), year);
    return baseDate.plusDays(derived.dayOffset());
  }

  /** Applies mondayisation rules to a date. */
  private static LocalDate applyMondayisationRules(LocalDate date) {
    return switch (date.getDayOfWeek()) {
      case SATURDAY -> date.minusDays(1);
      case SUNDAY -> date.plusDays(1);
      default -> date;
    };
  }

  /** Formats a list of localities for display. */
  private static String formatLocalities(List<Locality> localities) {
    return localities.stream()
        .map(HolidayOperations::formatLocality)
        .collect(Collectors.joining(", "));
  }

  /** Formats a single locality for display using correct pattern matching. */
  private static String formatLocality(Locality locality) {
    return switch (locality) {
      case Locality.Country country -> country.name();
      case Locality.Subdivision subdivision ->
          "%s, %s".formatted(subdivision.name(), subdivision.country().name());
      case Locality.City city ->
          "%s, %s, %s"
              .formatted(
                  city.name(), city.subdivision().name(), city.subdivision().country().name());
    };
  }

  /** Checks if a holiday locality matches a target locality using hierarchical matching. */
  private static boolean localityMatches(Locality holidayLocality, Locality targetLocality) {
    return switch (holidayLocality) {
      case Locality.Country holidayCountry ->
          switch (targetLocality) {
            case Locality.Country targetCountry -> holidayCountry.equals(targetCountry);
            case Locality.Subdivision targetSubdivision ->
                holidayCountry.equals(targetSubdivision.country());
            case Locality.City targetCity ->
                holidayCountry.equals(targetCity.subdivision().country());
          };
      case Locality.Subdivision holidaySubdivision ->
          switch (targetLocality) {
            case Locality.Country targetCountry -> false;
            case Locality.Subdivision targetSubdivision ->
                holidaySubdivision.equals(targetSubdivision);
            case Locality.City targetCity -> holidaySubdivision.equals(targetCity.subdivision());
          };
      case Locality.City holidayCity ->
          switch (targetLocality) {
            case Locality.Country targetCountry -> false;
            case Locality.Subdivision targetSubdivision -> false;
            case Locality.City targetCity -> holidayCity.equals(targetCity);
          };
    };
  }

  /** Calculates Easter Sunday for a given year using the algorithm from Jean Meeus. */
  public static LocalDate calculateEaster(int year) {
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

    int daysToFirstThursday = (DayOfWeek.THURSDAY.getValue() - firstDayOfWeek.getValue() + 7) % 7;
    LocalDate firstThursday = firstOfNovember.plusDays(daysToFirstThursday);

    return firstThursday.plusWeeks(3);
  }

  /** Calculates Memorial Day (last Monday of May) for a given year. */
  public static LocalDate calculateMemorialDay(int year) {
    LocalDate lastOfMay = LocalDate.of(year, Month.MAY, 31);
    DayOfWeek lastDayOfWeek = lastOfMay.getDayOfWeek();

    int daysToLastMonday = (lastDayOfWeek.getValue() - DayOfWeek.MONDAY.getValue() + 7) % 7;
    return lastOfMay.minusDays(daysToLastMonday);
  }

  /** Calculates Labor Day (1st Monday of September) for a given year. */
  public static LocalDate calculateLaborDay(int year) {
    LocalDate firstOfSeptember = LocalDate.of(year, Month.SEPTEMBER, 1);
    DayOfWeek firstDayOfWeek = firstOfSeptember.getDayOfWeek();

    int daysToFirstMonday = (DayOfWeek.MONDAY.getValue() - firstDayOfWeek.getValue() + 7) % 7;
    return firstOfSeptember.plusDays(daysToFirstMonday);
  }

  /** Calculates Mother's Day (2nd Sunday of May) for a given year. */
  public static LocalDate calculateMothersDay(int year) {
    LocalDate firstOfMay = LocalDate.of(year, Month.MAY, 1);
    DayOfWeek firstDayOfWeek = firstOfMay.getDayOfWeek();

    int daysToFirstSunday = (DayOfWeek.SUNDAY.getValue() - firstDayOfWeek.getValue() + 7) % 7;
    LocalDate firstSunday = firstOfMay.plusDays(daysToFirstSunday);

    return firstSunday.plusWeeks(1);
  }

  /** Calculates Father's Day (3rd Sunday of June) for a given year. */
  public static LocalDate calculateFathersDay(int year) {
    LocalDate firstOfJune = LocalDate.of(year, Month.JUNE, 1);
    DayOfWeek firstDayOfWeek = firstOfJune.getDayOfWeek();

    int daysToFirstSunday = (DayOfWeek.SUNDAY.getValue() - firstDayOfWeek.getValue() + 7) % 7;
    LocalDate firstSunday = firstOfJune.plusDays(daysToFirstSunday);

    return firstSunday.plusWeeks(2);
  }

  private static void validateYear(int year) {
    if (year <= 0) {
      throw new IllegalArgumentException("Year must be positive, got: " + year);
    }
  }
}
