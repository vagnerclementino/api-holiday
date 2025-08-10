package me.clementino.holiday.domain.dop;

import java.time.DayOfWeek;
import java.time.Month;
import java.util.Objects;

/**
 * Sealed interface representing different ways to calculate moveable holiday dates.
 *
 * <p>DOP Principles Applied: 1. Model Data Immutably and Transparently - All implementations are
 * immutable records 2. Model the Data, the Whole Data, and Nothing but the Data - Each rule
 * contains exactly what's needed 3. Make Illegal States Unrepresentable - Sealed interface prevents
 * invalid rule types 4. Separate Operations from Data - Calculation logic is separate from data
 */
public sealed interface DateCalculationRule
    permits DateCalculationRule.NthWeekdayOfMonth,
        DateCalculationRule.LastWeekdayOfMonth,
        DateCalculationRule.EasterBased,
        DateCalculationRule.RelativeToDate {

  /**
   * Nth weekday of a specific month. Examples: - Thanksgiving: 4th Thursday of November - Labor
   * Day: 1st Monday of September - Memorial Day: Last Monday of May (use LastWeekdayOfMonth
   * instead)
   */
  record NthWeekdayOfMonth(
      int occurrence, // 1st, 2nd, 3rd, 4th, etc.
      DayOfWeek dayOfWeek, // Monday, Tuesday, etc.
      Month month // January, February, etc.
      ) implements DateCalculationRule {

    public NthWeekdayOfMonth {
      Objects.requireNonNull(dayOfWeek, "Day of week cannot be null");
      Objects.requireNonNull(month, "Month cannot be null");

      if (occurrence < 1 || occurrence > 5) {
        throw new IllegalArgumentException(
            "Occurrence must be between 1 and 5, got: " + occurrence);
      }
    }
  }

  /**
   * Last occurrence of a weekday in a specific month. Examples: - Memorial Day: Last Monday of May
   * - Last Friday of the month
   */
  record LastWeekdayOfMonth(
      DayOfWeek dayOfWeek, // Monday, Tuesday, etc.
      Month month // January, February, etc.
      ) implements DateCalculationRule {

    public LastWeekdayOfMonth {
      Objects.requireNonNull(dayOfWeek, "Day of week cannot be null");
      Objects.requireNonNull(month, "Month cannot be null");
    }
  }

  /**
   * Date calculated relative to Easter Sunday. Examples: - Good Friday: Easter - 2 days - Easter
   * Monday: Easter + 1 day - Ash Wednesday: Easter - 46 days
   */
  record EasterBased(int daysOffset // Positive for after Easter, negative for before
      ) implements DateCalculationRule {
    // No validation needed - any integer offset is valid
  }

  /**
   * Date calculated relative to another fixed date. Examples: - Boxing Day: December 26 (Christmas
   * + 1 day) - Day after Thanksgiving: Thanksgiving + 1 day
   */
  record RelativeToDate(Month baseMonth, int baseDay, int daysOffset)
      implements DateCalculationRule {

    public RelativeToDate {
      Objects.requireNonNull(baseMonth, "Base month cannot be null");

      if (baseDay < 1 || baseDay > 31) {
        throw new IllegalArgumentException("Base day must be between 1 and 31, got: " + baseDay);
      }
    }
  }
}
