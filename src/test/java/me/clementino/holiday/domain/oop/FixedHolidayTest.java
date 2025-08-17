package me.clementino.holiday.domain.oop;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import me.clementino.holiday.domain.dop.HolidayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("FixedHoliday Tests")
class FixedHolidayTest {

  private List<Locality> localities;

  @BeforeEach
  void setUp() {
    localities = List.of(Locality.country("Brazil"));
  }

  @Nested
  @DisplayName("Basic Functionality Tests")
  class BasicFunctionalityTests {

    @Test
    @DisplayName("Should create New Year's Day correctly")
    void shouldCreateNewYearsDayCorrectly() {
      // Given
      FixedHoliday newYear =
          new FixedHoliday(
              "New Year",
              "Start of year",
              1,
              Month.JANUARY,
              localities,
              HolidayType.NATIONAL,
              true);

      // When & Then
      assertEquals("New Year", newYear.getName());
      assertEquals("Start of year", newYear.getDescription());
      assertEquals(LocalDate.of(2024, 1, 1), newYear.getDate(2024));
      assertEquals(LocalDate.of(2025, 1, 1), newYear.getDate(2025));
      assertEquals(HolidayType.NATIONAL, newYear.getType());
      assertTrue(newYear.isMondayisation());
    }

    @Test
    @DisplayName("Should calculate dates for different years")
    void shouldCalculateDatesForDifferentYears() {
      // Given
      FixedHoliday christmas =
          new FixedHoliday(
              "Christmas",
              "Birth of Christ",
              25,
              Month.DECEMBER,
              localities,
              HolidayType.RELIGIOUS,
              true);

      // When & Then
      assertEquals(LocalDate.of(2024, 12, 25), christmas.getDate(2024));
      assertEquals(LocalDate.of(2025, 12, 25), christmas.getDate(2025));
      assertEquals(LocalDate.of(2026, 12, 25), christmas.getDate(2026));
    }
  }

  @Nested
  @DisplayName("Mondayisation Tests")
  class MondayisationTests {

    @Test
    @DisplayName("Should apply mondayisation for Saturday holiday")
    void shouldApplyMondayisationForSaturdayHoliday() {
      // Given - Christmas 2021 falls on Saturday
      FixedHoliday christmas =
          new FixedHoliday(
              "Christmas",
              "Birth of Christ",
              25,
              Month.DECEMBER,
              localities,
              HolidayType.RELIGIOUS,
              true);

      // When & Then
      LocalDate actualDate = christmas.getDate(2021);
      LocalDate observedDate = christmas.getObserved(2021);

      assertEquals(LocalDate.of(2021, 12, 25), actualDate); // Saturday
      assertEquals(LocalDate.of(2021, 12, 24), observedDate); // Friday
    }

    @Test
    @DisplayName("Should apply mondayisation for Sunday holiday")
    void shouldApplyMondayisationForSundayHoliday() {
      // Given - Christmas 2022 falls on Sunday
      FixedHoliday christmas =
          new FixedHoliday(
              "Christmas",
              "Birth of Christ",
              25,
              Month.DECEMBER,
              localities,
              HolidayType.RELIGIOUS,
              true);

      // When & Then
      LocalDate actualDate = christmas.getDate(2022);
      LocalDate observedDate = christmas.getObserved(2022);

      assertEquals(LocalDate.of(2022, 12, 25), actualDate); // Sunday
      assertEquals(LocalDate.of(2022, 12, 26), observedDate); // Monday
    }

    @Test
    @DisplayName("Should not apply mondayisation when disabled")
    void shouldNotApplyMondayisationWhenDisabled() {
      // Given - Christmas with mondayisation disabled
      FixedHoliday christmas =
          new FixedHoliday(
              "Christmas",
              "Birth of Christ",
              25,
              Month.DECEMBER,
              localities,
              HolidayType.RELIGIOUS,
              false);

      // When & Then
      LocalDate actualDate = christmas.getDate(2021); // Saturday
      LocalDate observedDate = christmas.getObserved(2021);

      assertEquals(actualDate, observedDate); // Should be the same
      assertFalse(christmas.isMondayisation());
    }

    @Test
    @DisplayName("Should not change weekday holidays")
    void shouldNotChangeWeekdayHolidays() {
      // Given - Christmas 2024 falls on Wednesday
      FixedHoliday christmas =
          new FixedHoliday(
              "Christmas",
              "Birth of Christ",
              25,
              Month.DECEMBER,
              localities,
              HolidayType.RELIGIOUS,
              true);

      // When & Then
      LocalDate actualDate = christmas.getDate(2024);
      LocalDate observedDate = christmas.getObserved(2024);

      assertEquals(actualDate, observedDate); // Should be the same for weekdays
    }
  }

  @Nested
  @DisplayName("Validation Tests")
  class ValidationTests {

    @Test
    @DisplayName("Should validate day for month")
    void shouldValidateDayForMonth() {
      // When & Then
      assertThrows(
          IllegalArgumentException.class,
          () -> {
            new FixedHoliday(
                "Invalid",
                "Invalid date",
                32,
                Month.JANUARY,
                localities,
                HolidayType.NATIONAL,
                true);
          });
    }

    @Test
    @DisplayName("Should validate February 29 in non-leap year")
    void shouldValidateFebruary29InNonLeapYear() {
      // Given
      FixedHoliday leapDay =
          new FixedHoliday(
              "Leap Day", "Extra day", 29, Month.FEBRUARY, localities, HolidayType.NATIONAL, true);

      // When & Then
      assertDoesNotThrow(() -> leapDay.getDate(2024)); // Leap year - should work
      assertThrows(
          IllegalArgumentException.class,
          () -> leapDay.getDate(2023)); // Non-leap year - should fail
    }
  }

  @Nested
  @DisplayName("String Representation Tests")
  class StringRepresentationTests {

    @Test
    @DisplayName("Should return correct string representation")
    void shouldReturnCorrectStringRepresentation() {
      // Given
      FixedHoliday newYear =
          new FixedHoliday(
              "New Year",
              "Start of year",
              1,
              Month.JANUARY,
              localities,
              HolidayType.NATIONAL,
              true);

      // When
      String result = newYear.toString();

      // Then
      assertTrue(result.contains("New Year"));
      assertTrue(result.contains("JANUARY"));
      assertTrue(result.contains("NATIONAL"));
    }
  }
}
