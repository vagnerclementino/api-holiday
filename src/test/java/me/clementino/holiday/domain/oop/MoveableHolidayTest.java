package me.clementino.holiday.domain.oop;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import me.clementino.holiday.domain.dop.HolidayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@DisplayName("MoveableHoliday Tests")
@Tag("unit")
class MoveableHolidayTest {

  private List<Locality> localities;

  @BeforeEach
  void setUp() {
    localities = List.of(Locality.country("Brazil"));
  }

  @Nested
  @DisplayName("Easter Tests")
  class EasterTests {

    @Test
    @DisplayName("Should calculate Easter Sunday correctly for 2024")
    void shouldCalculateEasterSundayCorrectlyFor2024() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      assertEquals(LocalDate.of(2024, 3, 31), easter.getDate(2024));
      assertEquals("Easter Sunday", easter.getName());
      assertEquals(HolidayType.RELIGIOUS, easter.getType());
      assertEquals(MoveableHolidayType.LUNAR_BASED, easter.getMoveableType());
    }

    @Test
    @DisplayName("Should calculate Easter Sunday for different years")
    void shouldCalculateEasterSundayForDifferentYears() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      assertEquals(LocalDate.of(2024, 3, 31), easter.getDate(2024));
      assertEquals(LocalDate.of(2025, 4, 20), easter.getDate(2025));
      assertEquals(LocalDate.of(2026, 4, 5), easter.getDate(2026));
    }

    @Test
    @DisplayName("Should apply mondayisation to Easter Sunday when enabled")
    void shouldApplyMondayisationToEasterSunday() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED,
              true);

      LocalDate actualDate = easter.getDate(2025);
      LocalDate observedDate = easter.getObserved(2025);

      assertEquals(LocalDate.of(2025, 4, 20), actualDate);
      assertEquals(LocalDate.of(2025, 4, 21), observedDate);
      assertTrue(easter.isMondayisation());
    }

    @Test
    @DisplayName("Should not apply mondayisation when disabled")
    void shouldNotApplyMondayisationWhenDisabled() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      LocalDate actualDate = easter.getDate(2025);
      LocalDate observedDate = easter.getObserved(2025);

      assertEquals(actualDate, observedDate);
      assertFalse(easter.isMondayisation());
    }
  }

  @Nested
  @DisplayName("Good Friday Tests")
  class GoodFridayTests {

    @Test
    @DisplayName("Should calculate Good Friday relative to Easter")
    void shouldCalculateGoodFridayRelativeToEaster() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      MoveableHoliday goodFriday =
          new MoveableHoliday(
              "Good Friday",
              "Crucifixion of Christ",
              localities,
              HolidayType.RELIGIOUS,
              easter,
              -2);

      LocalDate easterDate = easter.getDate(2025);
      LocalDate goodFridayDate = goodFriday.getDate(2025);

      assertEquals(easterDate.minusDays(2), goodFridayDate);
      assertEquals(LocalDate.of(2025, 4, 18), goodFridayDate);
      assertEquals(MoveableHolidayType.RELATIVE_TO_HOLIDAY, goodFriday.getMoveableType());
    }

    @Test
    @DisplayName("Should calculate Good Friday for different years")
    void shouldCalculateGoodFridayForDifferentYears() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      MoveableHoliday goodFriday =
          new MoveableHoliday(
              "Good Friday",
              "Crucifixion of Christ",
              localities,
              HolidayType.RELIGIOUS,
              easter,
              -2);

      assertEquals(LocalDate.of(2024, 3, 29), goodFriday.getDate(2024));
      assertEquals(LocalDate.of(2025, 4, 18), goodFriday.getDate(2025));
      assertEquals(LocalDate.of(2026, 4, 3), goodFriday.getDate(2026));
    }

    @Test
    @DisplayName("Should maintain relationship between Easter and Good Friday")
    void shouldMaintainRelationshipBetweenEasterAndGoodFriday() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      MoveableHoliday goodFriday =
          new MoveableHoliday(
              "Good Friday",
              "Crucifixion of Christ",
              localities,
              HolidayType.RELIGIOUS,
              easter,
              -2);

      for (int year = 2024; year <= 2030; year++) {
        LocalDate easterDate = easter.getDate(year);
        LocalDate goodFridayDate = goodFriday.getDate(year);

        assertEquals(
            easterDate.minusDays(2),
            goodFridayDate,
            "Good Friday should be 2 days before Easter for year " + year);
      }
    }

    @Test
    @DisplayName("Should apply mondayisation to Good Friday when enabled")
    void shouldApplyMondayisationToGoodFridayWhenEnabled() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      MoveableHoliday goodFriday =
          new MoveableHoliday(
              "Good Friday",
              "Crucifixion of Christ",
              localities,
              HolidayType.RELIGIOUS,
              easter,
              -2,
              true);

      LocalDate actualDate = goodFriday.getDate(2025);
      LocalDate observedDate = goodFriday.getObserved(2025);

      assertEquals(LocalDate.of(2025, 4, 18), actualDate);
      assertEquals(actualDate, observedDate);
      assertTrue(goodFriday.isMondayisation());
    }
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Should create lunar-based holiday correctly")
    void shouldCreateLunarBasedHolidayCorrectly() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      assertEquals("Easter Sunday", easter.getName());
      assertEquals("Resurrection of Christ", easter.getDescription());
      assertEquals(HolidayType.RELIGIOUS, easter.getType());
      assertFalse(easter.isMondayisation());
      assertEquals(MoveableHolidayType.LUNAR_BASED, easter.getMoveableType());
      assertNull(easter.getBaseHoliday());
      assertEquals(0, easter.getDayOffset());
    }

    @Test
    @DisplayName("Should create lunar-based holiday with mondayisation")
    void shouldCreateLunarBasedHolidayWithMondayisation() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED,
              true);

      assertEquals("Easter Sunday", easter.getName());
      assertEquals(MoveableHolidayType.LUNAR_BASED, easter.getMoveableType());
      assertTrue(easter.isMondayisation());
    }

    @Test
    @DisplayName("Should create relative holiday correctly")
    void shouldCreateRelativeHolidayCorrectly() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      MoveableHoliday goodFriday =
          new MoveableHoliday(
              "Good Friday",
              "Crucifixion of Christ",
              localities,
              HolidayType.RELIGIOUS,
              easter,
              -2);

      assertEquals("Good Friday", goodFriday.getName());
      assertEquals(MoveableHolidayType.RELATIVE_TO_HOLIDAY, goodFriday.getMoveableType());
      assertEquals(easter, goodFriday.getBaseHoliday());
      assertEquals(-2, goodFriday.getDayOffset());
      assertFalse(goodFriday.isMondayisation());
    }

    @Test
    @DisplayName("Should create relative holiday with mondayisation")
    void shouldCreateRelativeHolidayWithMondayisation() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      MoveableHoliday goodFriday =
          new MoveableHoliday(
              "Good Friday",
              "Crucifixion of Christ",
              localities,
              HolidayType.RELIGIOUS,
              easter,
              -2,
              true);

      assertEquals("Good Friday", goodFriday.getName());
      assertEquals(MoveableHolidayType.RELATIVE_TO_HOLIDAY, goodFriday.getMoveableType());
      assertTrue(goodFriday.isMondayisation());
    }
  }

  @Nested
  @DisplayName("String Representation Tests")
  class StringRepresentationTests {

    @Test
    @DisplayName("Should return correct string representation for lunar-based holiday")
    void shouldReturnCorrectStringRepresentationForLunarBasedHoliday() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      String result = easter.toString();

      assertTrue(result.contains("Easter Sunday"));
      assertTrue(result.contains("RELIGIOUS"));
    }

    @Test
    @DisplayName("Should return correct string representation for relative holiday")
    void shouldReturnCorrectStringRepresentationForRelativeHoliday() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      MoveableHoliday goodFriday =
          new MoveableHoliday(
              "Good Friday",
              "Crucifixion of Christ",
              localities,
              HolidayType.RELIGIOUS,
              easter,
              -2);

      String result = goodFriday.toString();

      assertTrue(result.contains("Good Friday"));
      assertTrue(result.contains("RELIGIOUS"));
    }
  }

  @Nested
  @DisplayName("Edge Cases Tests")
  class EdgeCasesTests {

    @Test
    @DisplayName("Should handle early Gregorian calendar years")
    void shouldHandleEarlyGregorianCalendarYears() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      assertThrows(
          IllegalArgumentException.class,
          () -> {
            easter.getDate(1582);
          });

      assertDoesNotThrow(
          () -> {
            easter.getDate(1583);
          });
    }

    @Test
    @DisplayName("Should handle null base holiday gracefully")
    void shouldHandleNullBaseHolidayGracefully() {
      MoveableHoliday holiday =
          new MoveableHoliday(
              "Test Holiday", "Test description", localities, HolidayType.NATIONAL, null, 5);

      LocalDate result = holiday.getDate(2025);
      assertEquals(LocalDate.of(2025, 1, 1), result);
      assertEquals(MoveableHolidayType.RELATIVE_TO_HOLIDAY, holiday.getMoveableType());
    }

    @Test
    @DisplayName("Should handle positive day offset")
    void shouldHandlePositiveDayOffset() {
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      MoveableHoliday easterMonday =
          new MoveableHoliday(
              "Easter Monday", "Day after Easter", localities, HolidayType.RELIGIOUS, easter, 1);

      LocalDate easterDate = easter.getDate(2025);
      LocalDate easterMondayDate = easterMonday.getDate(2025);

      assertEquals(easterDate.plusDays(1), easterMondayDate);
      assertEquals(LocalDate.of(2025, 4, 21), easterMondayDate);
    }
  }

  @Nested
  @DisplayName("Weekday-Based Holiday Tests")
  class WeekdayBasedTests {

    @Test
    @DisplayName("Should handle weekday-based holidays")
    void shouldHandleWeekdayBasedHolidays() {
      MoveableHoliday laborDay =
          new MoveableHoliday(
              "Labor Day",
              "Workers' rights",
              localities,
              HolidayType.NATIONAL,
              MoveableHolidayType.WEEKDAY_BASED);

      assertEquals("Labor Day", laborDay.getName());
      assertEquals(MoveableHolidayType.WEEKDAY_BASED, laborDay.getMoveableType());
    }
  }
}
