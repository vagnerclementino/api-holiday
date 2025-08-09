package me.clementino.holiday.domain.oop;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import me.clementino.holiday.domain.HolidayType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("MoveableHoliday Tests")
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
      // Given
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      // When & Then
      assertEquals(LocalDate.of(2024, 3, 31), easter.getDate(2024));
      assertEquals("Easter Sunday", easter.getName());
      assertEquals(HolidayType.RELIGIOUS, easter.getType());
      assertEquals(MoveableHolidayType.LUNAR_BASED, easter.getMoveableType());
    }

    @Test
    @DisplayName("Should calculate Easter Sunday for different years")
    void shouldCalculateEasterSundayForDifferentYears() {
      // Given
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      // When & Then
      assertEquals(LocalDate.of(2024, 3, 31), easter.getDate(2024));
      assertEquals(LocalDate.of(2025, 4, 20), easter.getDate(2025));
      assertEquals(LocalDate.of(2026, 4, 5), easter.getDate(2026));
    }

    @Test
    @DisplayName("Should apply mondayisation to Easter Sunday when enabled")
    void shouldApplyMondayisationToEasterSunday() {
      // Given - Easter with mondayisation enabled
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED,
              true);

      // When & Then
      LocalDate actualDate = easter.getDate(2025);
      LocalDate observedDate = easter.getObserved(2025);

      assertEquals(LocalDate.of(2025, 4, 20), actualDate); // Sunday
      // Easter 2025 is on Sunday, so with mondayisation it moves to Monday
      assertEquals(LocalDate.of(2025, 4, 21), observedDate); // Monday
      assertTrue(easter.isMondayisation());
    }

    @Test
    @DisplayName("Should not apply mondayisation when disabled")
    void shouldNotApplyMondayisationWhenDisabled() {
      // Given - Easter with mondayisation disabled (default)
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      // When & Then
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
      // Given
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

      // When & Then
      LocalDate easterDate = easter.getDate(2025);
      LocalDate goodFridayDate = goodFriday.getDate(2025);

      assertEquals(easterDate.minusDays(2), goodFridayDate);
      assertEquals(LocalDate.of(2025, 4, 18), goodFridayDate); // Good Friday 2025
      assertEquals(MoveableHolidayType.RELATIVE_TO_HOLIDAY, goodFriday.getMoveableType());
    }

    @Test
    @DisplayName("Should calculate Good Friday for different years")
    void shouldCalculateGoodFridayForDifferentYears() {
      // Given
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

      // When & Then
      assertEquals(LocalDate.of(2024, 3, 29), goodFriday.getDate(2024)); // Good Friday 2024
      assertEquals(LocalDate.of(2025, 4, 18), goodFriday.getDate(2025)); // Good Friday 2025
      assertEquals(LocalDate.of(2026, 4, 3), goodFriday.getDate(2026)); // Good Friday 2026
    }

    @Test
    @DisplayName("Should maintain relationship between Easter and Good Friday")
    void shouldMaintainRelationshipBetweenEasterAndGoodFriday() {
      // Given
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

      // When & Then - Test multiple years
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
      // Given
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

      // When & Then
      LocalDate actualDate = goodFriday.getDate(2025);
      LocalDate observedDate = goodFriday.getObserved(2025);

      assertEquals(LocalDate.of(2025, 4, 18), actualDate); // Friday
      assertEquals(actualDate, observedDate); // Friday is not moved
      assertTrue(goodFriday.isMondayisation());
    }
  }

  @Nested
  @DisplayName("Constructor Tests")
  class ConstructorTests {

    @Test
    @DisplayName("Should create lunar-based holiday correctly")
    void shouldCreateLunarBasedHolidayCorrectly() {
      // Given & When
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      // Then
      assertEquals("Easter Sunday", easter.getName());
      assertEquals("Resurrection of Christ", easter.getDescription());
      assertEquals(HolidayType.RELIGIOUS, easter.getType());
      assertFalse(easter.isMondayisation()); // Default is false
      assertEquals(MoveableHolidayType.LUNAR_BASED, easter.getMoveableType());
      assertNull(easter.getBaseHoliday());
      assertEquals(0, easter.getDayOffset());
    }

    @Test
    @DisplayName("Should create lunar-based holiday with mondayisation")
    void shouldCreateLunarBasedHolidayWithMondayisation() {
      // Given & When
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED,
              true);

      // Then
      assertEquals("Easter Sunday", easter.getName());
      assertEquals(MoveableHolidayType.LUNAR_BASED, easter.getMoveableType());
      assertTrue(easter.isMondayisation());
    }

    @Test
    @DisplayName("Should create relative holiday correctly")
    void shouldCreateRelativeHolidayCorrectly() {
      // Given
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      // When
      MoveableHoliday goodFriday =
          new MoveableHoliday(
              "Good Friday",
              "Crucifixion of Christ",
              localities,
              HolidayType.RELIGIOUS,
              easter,
              -2);

      // Then
      assertEquals("Good Friday", goodFriday.getName());
      assertEquals(MoveableHolidayType.RELATIVE_TO_HOLIDAY, goodFriday.getMoveableType());
      assertEquals(easter, goodFriday.getBaseHoliday());
      assertEquals(-2, goodFriday.getDayOffset());
      assertFalse(goodFriday.isMondayisation()); // Default is false
    }

    @Test
    @DisplayName("Should create relative holiday with mondayisation")
    void shouldCreateRelativeHolidayWithMondayisation() {
      // Given
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      // When
      MoveableHoliday goodFriday =
          new MoveableHoliday(
              "Good Friday",
              "Crucifixion of Christ",
              localities,
              HolidayType.RELIGIOUS,
              easter,
              -2,
              true);

      // Then
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
      // Given
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      // When
      String result = easter.toString();

      // Then
      assertTrue(result.contains("Easter Sunday"));
      assertTrue(result.contains("RELIGIOUS"));
    }

    @Test
    @DisplayName("Should return correct string representation for relative holiday")
    void shouldReturnCorrectStringRepresentationForRelativeHoliday() {
      // Given
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

      // When
      String result = goodFriday.toString();

      // Then
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
      // Given
      MoveableHoliday easter =
          new MoveableHoliday(
              "Easter Sunday",
              "Resurrection of Christ",
              localities,
              HolidayType.RELIGIOUS,
              MoveableHolidayType.LUNAR_BASED);

      // When & Then
      assertThrows(
          IllegalArgumentException.class,
          () -> {
            easter.getDate(1582); // Before Gregorian calendar
          });

      assertDoesNotThrow(
          () -> {
            easter.getDate(1583); // First valid Gregorian year
          });
    }

    @Test
    @DisplayName("Should handle null base holiday gracefully")
    void shouldHandleNullBaseHolidayGracefully() {
      // Given
      MoveableHoliday holiday =
          new MoveableHoliday(
              "Test Holiday", "Test description", localities, HolidayType.NATIONAL, null, 5);

      // When & Then
      LocalDate result = holiday.getDate(2025);
      assertEquals(LocalDate.of(2025, 1, 1), result); // Should fallback to default
      assertEquals(MoveableHolidayType.RELATIVE_TO_HOLIDAY, holiday.getMoveableType());
    }

    @Test
    @DisplayName("Should handle positive day offset")
    void shouldHandlePositiveDayOffset() {
      // Given
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

      // When & Then
      LocalDate easterDate = easter.getDate(2025);
      LocalDate easterMondayDate = easterMonday.getDate(2025);

      assertEquals(easterDate.plusDays(1), easterMondayDate);
      assertEquals(LocalDate.of(2025, 4, 21), easterMondayDate); // Easter Monday 2025
    }
  }

  @Nested
  @DisplayName("Weekday-Based Holiday Tests")
  class WeekdayBasedTests {

    @Test
    @DisplayName("Should handle weekday-based holidays")
    void shouldHandleWeekdayBasedHolidays() {
      // Given - This is a placeholder test since weekday-based calculation is not fully implemented
      MoveableHoliday laborDay =
          new MoveableHoliday(
              "Labor Day",
              "Workers' rights",
              localities,
              HolidayType.NATIONAL,
              MoveableHolidayType.WEEKDAY_BASED);

      // When & Then
      assertEquals("Labor Day", laborDay.getName());
      assertEquals(MoveableHolidayType.WEEKDAY_BASED, laborDay.getMoveableType());

      // Note: Actual weekday calculation is not fully implemented yet
      // This test only verifies the holiday can be created correctly
    }
  }
}
