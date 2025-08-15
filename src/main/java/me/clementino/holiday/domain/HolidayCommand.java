package me.clementino.holiday.domain;

import java.util.Objects;
import java.util.Optional;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.Locality;

/**
 * Sealed interface representing holiday commands using enhanced DOP principles. Following DOP v1.1
 * Principle 3: Make Illegal States Unrepresentable.
 *
 * <p>Commands are the only way to modify holiday data. Each command represents a specific operation
 * with its required data, now enhanced to work with the comprehensive DOP domain model.
 */
public sealed interface HolidayCommand
    permits HolidayCommand.Create,
        HolidayCommand.Update,
        HolidayCommand.Delete,
        HolidayCommand.CalculateForYear,
        HolidayCommand.BulkCalculate {

  /** Command to create a new holiday using the DOP Holiday sealed interface. */
  record Create(Holiday holiday) implements HolidayCommand {
    public Create {
      Objects.requireNonNull(holiday, "Holiday cannot be null");
    }

    /** Gets the holiday name for logging/debugging. */
    public String getHolidayName() {
      return holiday.name();
    }

    /** Gets the holiday type for validation. */
    public HolidayType getHolidayType() {
      return holiday.type();
    }

    /** Gets the primary locality for this holiday. */
    public Optional<Locality> getPrimaryLocality() {
      return holiday.localities().isEmpty()
          ? Optional.empty()
          : Optional.of(holiday.localities().getFirst());
    }
  }

  /** Command to update an existing holiday with a new Holiday instance. */
  record Update(String id, Holiday holiday) implements HolidayCommand {
    public Update {
      Objects.requireNonNull(id, "ID cannot be null");
      if (id.isBlank()) {
        throw new IllegalArgumentException("ID cannot be blank");
      }
      Objects.requireNonNull(holiday, "Holiday cannot be null");
    }

    /** Gets the holiday name for logging/debugging. */
    public String getHolidayName() {
      return holiday.name();
    }
  }

  /** Command to delete a holiday. */
  record Delete(String id, Optional<String> reason) implements HolidayCommand {
    public Delete {
      Objects.requireNonNull(id, "ID cannot be null");
      if (id.isBlank()) {
        throw new IllegalArgumentException("ID cannot be blank");
      }
      reason = Objects.requireNonNullElse(reason, Optional.empty());
    }

    /** Creates a delete command with just an ID. */
    public static Delete of(String id) {
      return new Delete(id, Optional.empty());
    }

    /** Creates a delete command with a reason. */
    public static Delete withReason(String id, String reason) {
      return new Delete(id, Optional.of(reason));
    }

    /** Checks if this delete command has a reason. */
    public boolean hasReason() {
      return reason.isPresent();
    }
  }

  /** Command to calculate a holiday for a specific year. */
  record CalculateForYear(String holidayId, int year, boolean persistResult)
      implements HolidayCommand {
    public CalculateForYear {
      Objects.requireNonNull(holidayId, "Holiday ID cannot be null");
      if (holidayId.isBlank()) {
        throw new IllegalArgumentException("Holiday ID cannot be blank");
      }
      if (year < 1900 || year > 2200) {
        throw new IllegalArgumentException("Year must be between 1900 and 2200");
      }
    }

    /** Creates a calculate command that persists the result. */
    public static CalculateForYear withPersistence(String holidayId, int year) {
      return new CalculateForYear(holidayId, year, true);
    }

    /** Creates a calculate command that doesn't persist the result. */
    public static CalculateForYear withoutPersistence(String holidayId, int year) {
      return new CalculateForYear(holidayId, year, false);
    }
  }

  /** Command to bulk calculate holidays for multiple years or localities. */
  record BulkCalculate(
      Optional<String> holidayId,
      Optional<Locality> locality,
      int startYear,
      int endYear,
      boolean persistResults)
      implements HolidayCommand {
    public BulkCalculate {
      holidayId = Objects.requireNonNullElse(holidayId, Optional.empty());
      locality = Objects.requireNonNullElse(locality, Optional.empty());

      if (startYear < 1900 || startYear > 2200) {
        throw new IllegalArgumentException("Start year must be between 1900 and 2200");
      }
      if (endYear < 1900 || endYear > 2200) {
        throw new IllegalArgumentException("End year must be between 1900 and 2200");
      }
      if (startYear > endYear) {
        throw new IllegalArgumentException("Start year cannot be after end year");
      }
      if (endYear - startYear > 50) {
        throw new IllegalArgumentException("Year range cannot exceed 50 years");
      }

      // Must have either holidayId or locality
      if (holidayId.isEmpty() && locality.isEmpty()) {
        throw new IllegalArgumentException("Must specify either holiday ID or locality");
      }
    }

    /** Creates a bulk calculate command for a specific holiday across years. */
    public static BulkCalculate forHoliday(String holidayId, int startYear, int endYear) {
      return new BulkCalculate(Optional.of(holidayId), Optional.empty(), startYear, endYear, true);
    }

    /** Creates a bulk calculate command for all holidays in a locality across years. */
    public static BulkCalculate forLocality(Locality locality, int startYear, int endYear) {
      return new BulkCalculate(Optional.empty(), Optional.of(locality), startYear, endYear, true);
    }

    /** Gets the number of years to calculate. */
    public int getYearCount() {
      return endYear - startYear + 1;
    }

    /** Checks if this is a single holiday calculation. */
    public boolean isSingleHoliday() {
      return holidayId.isPresent();
    }

    /** Checks if this is a locality-wide calculation. */
    public boolean isLocalityWide() {
      return locality.isPresent();
    }
  }
}
