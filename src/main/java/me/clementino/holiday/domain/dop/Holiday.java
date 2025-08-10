package me.clementino.holiday.domain.dop;

import java.time.LocalDate;
import java.util.List;
import me.clementino.holiday.domain.HolidayType;

/**
 * Sealed interface representing a holiday following Data-Oriented Programming v1.1 principles.
 * Based on the OOP Holiday hierarchy but redesigned with DOP principles.
 *
 * <p>DOP Principles Applied:
 *
 * <ol>
 *   <li><strong>Model Data Immutably and Transparently</strong> - All implementations are immutable
 *       records
 *   <li><strong>Model the Data, the Whole Data, and Nothing but the Data</strong> - Represents
 *       exactly what a holiday is
 *   <li><strong>Make Illegal States Unrepresentable</strong> - Sealed interface prevents invalid
 *       holiday types
 *   <li><strong>Separate Operations from Data</strong> - No behavior methods, only data. Date
 *       calculations are in HolidayOperations
 * </ol>
 *
 * <p><strong>Note:</strong> The {@code date()} method was removed from this interface because:
 *
 * <ul>
 *   <li>{@link FixedHoliday} now stores {@code day} and {@code month} separately, not a full date
 *   <li>{@link MoveableHoliday} doesn't have a fixed date - it's calculated based on rules
 *   <li>Date calculations are handled by {@link HolidayOperations#calculateDate(Holiday, int)}
 * </ul>
 */
public sealed interface Holiday
    permits FixedHoliday, ObservedHoliday, MoveableHoliday, MoveableFromBaseHoliday {

  // Common data accessible to all holiday types
  String name();

  String description();

  LocalDate date();

  List<Locality> localities();

  HolidayType type();
}
