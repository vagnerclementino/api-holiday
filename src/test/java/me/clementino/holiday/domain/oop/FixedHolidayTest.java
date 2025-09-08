package me.clementino.holiday.domain.oop;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import me.clementino.holiday.domain.dop.HolidayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@DisplayName("FixedHoliday Tests")
@Tag("unit")
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
      FixedHoliday newYear =
          new FixedHoliday(
              "New Year",
              "Start of year",
              1,
              Month.JANUARY,
              localities,
              HolidayType.NATIONAL,
              true);

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
      FixedHoliday christmas =
          new FixedHoliday(
              "Christmas",
              "Birth of Christ",
              25,
              Month.DECEMBER,
              localities,
              HolidayType.RELIGIOUS,
              true);

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
      FixedHoliday christmas =
          new FixedHoliday(
              "Christmas",
              "Birth of Christ",
              25,
              Month.DECEMBER,
              localities,
              HolidayType.RELIGIOUS,
              true);

      LocalDate actualDate = christmas.getDate(2021);
      LocalDate observedDate = christmas.getObserved(2021);

      assertEquals(LocalDate.of(2021, 12, 25), actualDate);
      assertEquals(LocalDate.of(2021, 12, 24), observedDate);
    }

    @Test
    @DisplayName("Should apply mondayisation for Sunday holiday")
    void shouldApplyMondayisationForSundayHoliday() {
      FixedHoliday christmas =
          new FixedHoliday(
              "Christmas",
              "Birth of Christ",
              25,
              Month.DECEMBER,
              localities,
              HolidayType.RELIGIOUS,
              true);

      LocalDate actualDate = christmas.getDate(2022);
      LocalDate observedDate = christmas.getObserved(2022);

      assertEquals(LocalDate.of(2022, 12, 25), actualDate);
      assertEquals(LocalDate.of(2022, 12, 26), observedDate);
    }

    @Test
    @DisplayName("Should not apply mondayisation when disabled")
    void shouldNotApplyMondayisationWhenDisabled() {
      FixedHoliday christmas =
          new FixedHoliday(
              "Christmas",
              "Birth of Christ",
              25,
              Month.DECEMBER,
              localities,
              HolidayType.RELIGIOUS,
              false);

      LocalDate actualDate = christmas.getDate(2021);
      LocalDate observedDate = christmas.getObserved(2021);

      assertEquals(actualDate, observedDate);
      assertFalse(christmas.isMondayisation());
    }

    @Test
    @DisplayName("Should not change weekday holidays")
    void shouldNotChangeWeekdayHolidays() {
      FixedHoliday christmas =
          new FixedHoliday(
              "Christmas",
              "Birth of Christ",
              25,
              Month.DECEMBER,
              localities,
              HolidayType.RELIGIOUS,
              true);

      LocalDate actualDate = christmas.getDate(2024);
      LocalDate observedDate = christmas.getObserved(2024);

      assertEquals(actualDate, observedDate);
    }
  }

  @Nested
  @DisplayName("Validation Tests")
  class ValidationTests {

    @Test
    @DisplayName("Should validate day for month")
    void shouldValidateDayForMonth() {
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
      FixedHoliday leapDay =
          new FixedHoliday(
              "Leap Day", "Extra day", 29, Month.FEBRUARY, localities, HolidayType.NATIONAL, true);

      assertDoesNotThrow(() -> leapDay.getDate(2024));
      assertThrows(IllegalArgumentException.class, () -> leapDay.getDate(2023));
    }
  }

  @Nested
  @DisplayName("String Representation Tests")
  class StringRepresentationTests {

    @Test
    @DisplayName("Should return correct string representation")
    void shouldReturnCorrectStringRepresentation() {
      FixedHoliday newYear =
          new FixedHoliday(
              "New Year",
              "Start of year",
              1,
              Month.JANUARY,
              localities,
              HolidayType.NATIONAL,
              true);

      String result = newYear.toString();

      assertTrue(result.contains("New Year"));
      assertTrue(result.contains("JANUARY"));
      assertTrue(result.contains("NATIONAL"));
    }
  }
}
