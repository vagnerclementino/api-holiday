package me.clementino.holiday.domain.dop;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Objects;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.domain.ValidationResult;

/**
 * Operations for working with Holiday data following DOP principle: Separate Operations from Data.
 *
 * <p>Based on the OOP Holiday classes but with operations separated from data. This class contains
 * all the behavior/operations while keeping the data structures pure.
 */
public final class HolidayOperations {

  /** Minimum year for accurate Gregorian calendar Easter calculation. */
  private static final int MIN_GREGORIAN_YEAR = 1583;

  /** Length of the Metonic cycle in years (lunar phases repeat every 19 solar years). */
  private static final int METONIC_CYCLE_YEARS = 19;

  /** Number of years in a century. */
  private static final int YEARS_PER_CENTURY = 100;

  /** Number of years in a leap year cycle within a century. */
  private static final int LEAP_YEAR_CYCLE = 4;

  /** Gregorian correction factor for lunar orbit calculation. */
  private static final int GREGORIAN_LUNAR_CORRECTION = 8;

  /** Base value for Gregorian calendar lunar correction. */
  private static final int GREGORIAN_CORRECTION_BASE = 25;

  /** Adjustment factor for precessional correction. */
  private static final int PRECESSIONAL_ADJUSTMENT = 1;

  /** Divisor for precessional correction calculation. */
  private static final int PRECESSIONAL_DIVISOR = 3;

  /** Base epact calculation constant. */
  private static final int EPACT_BASE = 15;

  /** Modulus for epact calculation (lunar month length approximation). */
  private static final int EPACT_MODULUS = 30;

  /** Base value for days to full moon calculation. */
  private static final int DAYS_TO_FULL_MOON_BASE = 32;

  /** Modulus for days to full moon calculation (days in a week). */
  private static final int DAYS_TO_FULL_MOON_MODULUS = 7;

  /** Multiplier for golden number in April correction. */
  private static final int APRIL_CORRECTION_GOLDEN_MULTIPLIER = 11;

  /** Multiplier for days to full moon in April correction. */
  private static final int APRIL_CORRECTION_DAYS_MULTIPLIER = 22;

  /** Divisor for April correction calculation. */
  private static final int APRIL_CORRECTION_DIVISOR = 451;

  /** Base value for final date calculation. */
  private static final int FINAL_DATE_BASE = 114;

  /** Divisor to extract month from final calculation. */
  private static final int MONTH_DIVISOR = 31;

  /** Adjustment to convert from 0-based to 1-based day numbering. */
  private static final int DAY_ADJUSTMENT = 1;

  private HolidayOperations() {
    // Utility class - prevent instantiation
  }

  /**
   * Calculates the actual date for a holiday in a given year. Uses pattern matching to handle
   * different holiday types.
   */
  public static LocalDate calculateDate(Holiday holiday, int year) {
    Objects.requireNonNull(holiday, "Holiday cannot be null");
    validateYear(year);

    return switch (holiday) {
      case FixedHoliday fixed -> calculateFixedDate(fixed, year);
      case ObservedHoliday observed -> calculateObservedDate(observed, year);
      case MoveableHoliday moveable -> calculateMoveableDate(moveable, year);
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
    };
  }

  /** Checks if a holiday falls on a weekend for the specified year. */
  public static boolean isWeekend(Holiday holiday, int year) {
    LocalDate holidayDate = calculateDate(holiday, year);
    DayOfWeek dayOfWeek = holidayDate.getDayOfWeek();
    return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
  }

  /**
   * Validates a holiday according to business rules. Uses pattern matching for type-safe
   * validation.
   */
  public static ValidationResult validateHoliday(Holiday holiday) {
    Objects.requireNonNull(holiday, "Holiday cannot be null");

    return switch (holiday) {
      case FixedHoliday fixed -> validateFixedHoliday(fixed);
      case ObservedHoliday observed -> validateObservedHoliday(observed);
      case MoveableHoliday moveable -> validateMoveableHoliday(moveable);
    };
  }

  // Private helper methods for date calculations

  private static LocalDate calculateFixedDate(FixedHoliday fixed, int year) {
    // For fixed holidays, extract day and month from the stored date and apply to the given year
    return LocalDate.of(year, fixed.date().getMonth(), fixed.date().getDayOfMonth());
  }

  private static LocalDate calculateObservedDate(ObservedHoliday observed, int year) {
    // For observed holidays, extract day and month from the stored date and apply to the given year
    return LocalDate.of(year, observed.date().getMonth(), observed.date().getDayOfMonth());
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
        yield "%s - %s holiday in %s (%s)%s"
            .formatted(
                moveable.name(),
                typeInfo,
                localityInfo,
                moveable.moveableType(),
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

  private static LocalDate calculateMoveableDate(MoveableHoliday moveable, int year) {
    // For moveable holidays, we need to recalculate based on the type
    return switch (moveable.moveableType()) {
      case LUNAR_BASED -> calculateLunarBasedDate(moveable, year);
      case RELATIVE_TO_HOLIDAY -> calculateRelativeDate(moveable, year);
      case WEEKDAY_BASED -> calculateWeekdayBasedDate(moveable, year);
    };
  }

  private static LocalDate calculateLunarBasedDate(MoveableHoliday moveable, int year) {
    return switch (moveable.name()) {
      case "Easter Sunday" -> calculateEasterSunday(year);
      default -> LocalDate.of(year, Month.JANUARY, 1); // Default fallback
    };
  }

  private static LocalDate calculateRelativeDate(MoveableHoliday moveable, int year) {
    if (moveable.baseHoliday().isPresent()) {
      LocalDate baseDate = calculateDate(moveable.baseHoliday().get(), year);
      return baseDate.plusDays(moveable.dayOffset());
    }
    return LocalDate.of(year, Month.JANUARY, 1); // Default fallback
  }

  private static LocalDate calculateWeekdayBasedDate(MoveableHoliday moveable, int year) {
    // This is a simplified implementation. In a real system, you would need
    // more sophisticated logic to handle different weekday patterns
    // For now, just return the stored date adjusted to the requested year
    return moveable.date().withYear(year);
  }

  /**
   * Calculates Easter Sunday using the Western Christian algorithm (Gregorian calendar). Based on
   * the algorithm by Jean Meeus from "Astronomical Algorithms" (1991).
   */
  private static LocalDate calculateEasterSunday(int year) {
    if (year < MIN_GREGORIAN_YEAR) {
      throw new IllegalArgumentException(
          "Easter calculation is only accurate for Gregorian calendar years ("
              + MIN_GREGORIAN_YEAR
              + " onwards). Got: "
              + year);
    }

    int goldenNumber = year % METONIC_CYCLE_YEARS;
    int century = year / YEARS_PER_CENTURY;
    int totalDays = getTotalDays(year, century, goldenNumber);
    int easterMonth = totalDays / MONTH_DIVISOR; // Will be 3 (March) or 4 (April)
    int easterDay = (totalDays % MONTH_DIVISOR) + DAY_ADJUSTMENT; // Day of the month (1-31)

    return LocalDate.of(year, easterMonth, easterDay);
  }

  private static int getTotalDays(int year, int century, int goldenNumber) {
    int yearInCentury = year % YEARS_PER_CENTURY;
    int centuryLeapCorrection = century / LEAP_YEAR_CYCLE;
    int centuryRemainder = century % LEAP_YEAR_CYCLE;
    int epact = getEpact(century, goldenNumber, centuryLeapCorrection);
    int daysToFullMoon = getDaysToFullMoon(yearInCentury, centuryRemainder, epact);
    return getDays(goldenNumber, epact, daysToFullMoon);
  }

  private static int getDays(int goldenNumber, int epact, int daysToFullMoon) {
    int aprilCorrection =
        (goldenNumber
                + APRIL_CORRECTION_GOLDEN_MULTIPLIER * epact
                + APRIL_CORRECTION_DAYS_MULTIPLIER * daysToFullMoon)
            / APRIL_CORRECTION_DIVISOR;

    return epact + daysToFullMoon - DAYS_TO_FULL_MOON_MODULUS * aprilCorrection + FINAL_DATE_BASE;
  }

  private static int getDaysToFullMoon(int yearInCentury, int centuryRemainder, int epact) {
    int yearLeapCorrection = yearInCentury / LEAP_YEAR_CYCLE;
    int yearRemainder = yearInCentury % LEAP_YEAR_CYCLE;
    return (DAYS_TO_FULL_MOON_BASE
            + 2 * centuryRemainder
            + 2 * yearLeapCorrection
            - epact
            - yearRemainder)
        % DAYS_TO_FULL_MOON_MODULUS;
  }

  private static int getEpact(int century, int goldenNumber, int centuryLeapCorrection) {
    int gregorianCorrection = (century + GREGORIAN_LUNAR_CORRECTION) / GREGORIAN_CORRECTION_BASE;
    int precessionalCorrection =
        (century - gregorianCorrection + PRECESSIONAL_ADJUSTMENT) / PRECESSIONAL_DIVISOR;

    return (METONIC_CYCLE_YEARS * goldenNumber
            + century
            - centuryLeapCorrection
            - precessionalCorrection
            + EPACT_BASE)
        % EPACT_MODULUS;
  }

  private static LocalDate applyMondayisationRules(LocalDate date) {
    return switch (date.getDayOfWeek()) {
      case SATURDAY -> date.minusDays(1); // Move to Friday
      case SUNDAY -> date.plusDays(1); // Move to Monday
      default -> date; // No change for weekdays
    };
  }

  private static ValidationResult validateFixedHoliday(FixedHoliday holiday) {
    var errors = new java.util.ArrayList<String>();

    // Validate date is not null (already handled by record constructor)
    // Additional date validations could be added here if needed

    // Validate localities are not empty
    if (holiday.localities().isEmpty()) {
      errors.add("At least one locality must be specified");
    }

    // Validate locality hierarchy for holiday type
    if (holiday.type() == HolidayType.NATIONAL) {
      boolean hasNationalLocality =
          holiday.localities().stream().anyMatch(locality -> locality instanceof Locality.Country);
      if (!hasNationalLocality) {
        errors.add("National holidays should have at least one country-level locality");
      }
    }

    return errors.isEmpty()
        ? new ValidationResult.Success("Fixed holiday is valid")
        : new ValidationResult.Failure(List.copyOf(errors));
  }

  private static ValidationResult validateObservedHoliday(ObservedHoliday holiday) {
    var errors = new java.util.ArrayList<String>();

    // Validate date is not null (already handled by record constructor)
    // Additional date validations could be added here if needed

    // Validate localities are not empty
    if (holiday.localities().isEmpty()) {
      errors.add("At least one locality must be specified");
    }

    // Validate locality hierarchy for holiday type
    if (holiday.type() == HolidayType.NATIONAL) {
      boolean hasNationalLocality =
          holiday.localities().stream().anyMatch(locality -> locality instanceof Locality.Country);
      if (!hasNationalLocality) {
        errors.add("National holidays should have at least one country-level locality");
      }
    }

    return errors.isEmpty()
        ? new ValidationResult.Success("Observed holiday is valid")
        : new ValidationResult.Failure(List.copyOf(errors));
  }

  private static ValidationResult validateMoveableHoliday(MoveableHoliday holiday) {
    var errors = new java.util.ArrayList<String>();

    // Validate localities are not empty
    if (holiday.localities().isEmpty()) {
      errors.add("At least one locality must be specified");
    }

    // Validate relative holidays have base holiday
    if (holiday.moveableType() == MoveableHolidayType.RELATIVE_TO_HOLIDAY
        && holiday.baseHoliday().isEmpty()) {
      errors.add("Relative holidays must have a base holiday specified");
    }

    // Validate locality hierarchy for holiday type
    if (holiday.type() == HolidayType.NATIONAL) {
      boolean hasNationalLocality =
          holiday.localities().stream().anyMatch(locality -> locality instanceof Locality.Country);
      if (!hasNationalLocality) {
        errors.add("National holidays should have at least one country-level locality");
      }
    }

    return errors.isEmpty()
        ? new ValidationResult.Success("Moveable holiday is valid")
        : new ValidationResult.Failure(List.copyOf(errors));
  }

  private static String formatLocalities(List<Locality> localities) {
    if (localities.isEmpty()) {
      return "Unknown location";
    }

    if (localities.size() == 1) {
      return getLocalityDisplayName(localities.get(0));
    }

    return localities.stream()
        .map(HolidayOperations::getLocalityDisplayName)
        .reduce((first, second) -> first + ", " + second)
        .orElse("Multiple locations");
  }

  private static String getLocalityDisplayName(Locality locality) {
    return switch (locality) {
      case Locality.Country country -> country.name();
      case Locality.Subdivision subdivision ->
          subdivision.name() + ", " + subdivision.country().name();
      case Locality.City city ->
          city.name() + ", " + city.subdivision().name() + ", " + city.country().name();
    };
  }

  private static boolean localityMatches(Locality holidayLocality, Locality targetLocality) {
    // Use pattern matching with sealed interface for type-safe locality matching
    return switch (holidayLocality) {
        // National holidays (country-level) apply everywhere in the country
      case Locality.Country holidayCountry ->
          switch (targetLocality) {
            case Locality.Country targetCountry -> holidayCountry.equals(targetCountry);
            case Locality.Subdivision targetSub -> holidayCountry.equals(targetSub.country());
            case Locality.City targetCity -> holidayCountry.equals(targetCity.country());
          };

        // State/subdivision holidays apply to that subdivision and its cities
      case Locality.Subdivision holidaySubdivision ->
          switch (targetLocality) {
            case Locality.Country ignored ->
                false; // Subdivision holiday doesn't apply to whole country
            case Locality.Subdivision targetSub -> holidaySubdivision.equals(targetSub);
            case Locality.City targetCity -> holidaySubdivision.equals(targetCity.subdivision());
          };

        // City holidays apply only to that specific city
      case Locality.City holidayCity ->
          switch (targetLocality) {
            case Locality.Country ignored -> false; // City holiday doesn't apply to whole country
            case Locality.Subdivision ignored ->
                false; // City holiday doesn't apply to whole subdivision
            case Locality.City targetCity -> holidayCity.equals(targetCity);
          };
    };
  }

  private static void validateYear(int year) {
    if (year <= 0) {
      throw new IllegalArgumentException("Year must be positive, got: " + year);
    }
  }
}
