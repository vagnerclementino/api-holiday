package me.clementino.holiday.domain.dop;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for HolidayOperations covering all holiday types. Tests both calculateDate
 * and calculateObservedDate methods to ensure they return new instances with correctly calculated
 * dates.
 */
@DisplayName("HolidayOperations Tests")
class HolidayOperationsTest {

  private static final List<Locality> BRAZIL_LOCALITIES = List.of(Locality.country("BR", "Brazil"));
  private static final List<Locality> US_LOCALITIES =
      List.of(Locality.country("US", "United States"));

  private final HolidayOperations holidayOperations = new HolidayOperations();

  @Nested
  @DisplayName("FixedHoliday Tests")
  class FixedHolidayTests {

    @Test
    @DisplayName("calculateDate should return new FixedHoliday with calculated date")
    void testCalculateDateFixedHoliday() {
      // Given: Christmas 2023
      var originalChristmas =
          new FixedHoliday(
              "Christmas Day",
              "Christian celebration",
              LocalDate.of(2023, Month.DECEMBER, 25),
              25,
              Month.DECEMBER,
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS);

      // When: Calculate for 2025
      Holiday result = holidayOperations.calculateDate(originalChristmas, 2025);

      // Then: Should return new FixedHoliday with 2025 date
      assertInstanceOf(FixedHoliday.class, result);
      FixedHoliday calculatedChristmas = (FixedHoliday) result;

      assertEquals(LocalDate.of(2025, Month.DECEMBER, 25), calculatedChristmas.date());
      assertEquals("Christmas Day", calculatedChristmas.name());
      assertEquals("Christian celebration", calculatedChristmas.description());
      assertEquals(BRAZIL_LOCALITIES, calculatedChristmas.localities());
      assertEquals(HolidayType.RELIGIOUS, calculatedChristmas.type());

      // Should be a new instance
      assertNotSame(originalChristmas, result);
    }

    @Test
    @DisplayName("calculateObservedDate should return same as calculateDate for FixedHoliday")
    void testCalculateObservedDateFixedHoliday() {
      // Given: New Year 2023
      var originalNewYear =
          new FixedHoliday(
              "New Year's Day",
              "First day of the year",
              LocalDate.of(2023, Month.JANUARY, 1),
              1,
              Month.JANUARY,
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL);

      // When: Calculate observed date for 2025
      Holiday result = holidayOperations.calculateObservedDate(originalNewYear, 2025);

      // Then: Should return FixedHoliday (no observed date concept)
      assertInstanceOf(FixedHoliday.class, result);
      FixedHoliday calculatedNewYear = (FixedHoliday) result;

      assertEquals(LocalDate.of(2025, Month.JANUARY, 1), calculatedNewYear.date());
      assertNotSame(originalNewYear, result);
    }

    @Test
    @DisplayName("getDateOnly should return correct LocalDate")
    void testGetDateOnlyFixedHoliday() {
      // Given: Independence Day 2023
      var independenceDay =
          new FixedHoliday(
              "Independence Day",
              "Brazil's independence",
              LocalDate.of(2023, Month.SEPTEMBER, 7),
              7,
              Month.SEPTEMBER,
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL);

      // When: Get date for 2026
      LocalDate result = holidayOperations.getDateOnly(independenceDay, 2026);

      // Then: Should return correct date
      assertEquals(LocalDate.of(2026, Month.SEPTEMBER, 7), result);
    }
  }

  @Nested
  @DisplayName("ObservedHoliday Tests")
  class ObservedHolidayTests {

    @Test
    @DisplayName("calculateDate should return new ObservedHoliday with calculated dates")
    void testCalculateDateObservedHoliday() {
      // Given: Christmas 2023 (Monday) with mondayisation
      var originalChristmas =
          new ObservedHoliday(
              "Christmas Day",
              "Christian celebration",
              LocalDate.of(2023, Month.DECEMBER, 25), // Original date
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              LocalDate.of(2023, Month.DECEMBER, 25), // Observed date (same)
              true // mondayisation enabled
              );

      // When: Calculate for 2022 (Christmas falls on Sunday)
      Holiday result = holidayOperations.calculateDate(originalChristmas, 2022);

      // Then: Should return new ObservedHoliday with mondayisation applied
      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday calculatedChristmas = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2022, Month.DECEMBER, 25), calculatedChristmas.date()); // Sunday
      assertEquals(
          LocalDate.of(2022, Month.DECEMBER, 26), calculatedChristmas.observed()); // Monday
      assertTrue(calculatedChristmas.mondayisation());
      assertNotSame(originalChristmas, result);
    }

    @Test
    @DisplayName("calculateDate should not apply mondayisation when disabled")
    void testCalculateDateObservedHolidayNoMondayisation() {
      // Given: Christmas with mondayisation disabled
      var originalChristmas =
          new ObservedHoliday(
              "Christmas Day",
              "Christian celebration",
              LocalDate.of(2023, Month.DECEMBER, 25),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              LocalDate.of(2023, Month.DECEMBER, 25),
              false // mondayisation disabled
              );

      // When: Calculate for 2022 (Christmas falls on Sunday)
      Holiday result = holidayOperations.calculateDate(originalChristmas, 2022);

      // Then: Should return ObservedHoliday without mondayisation
      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday calculatedChristmas = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2022, Month.DECEMBER, 25), calculatedChristmas.date());
      assertEquals(
          LocalDate.of(2022, Month.DECEMBER, 25), calculatedChristmas.observed()); // Same date
      assertFalse(calculatedChristmas.mondayisation());
    }

    @Test
    @DisplayName("calculateObservedDate should apply mondayisation rules")
    void testCalculateObservedDateObservedHoliday() {
      // Given: New Year with mondayisation (using a weekday as placeholder)
      var originalNewYear =
          new ObservedHoliday(
              "New Year's Day",
              "First day of the year",
              LocalDate.of(2023, Month.JANUARY, 1), // Sunday
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL,
              LocalDate.of(2023, Month.JANUARY, 2), // Monday (observed)
              true);

      // When: Calculate observed date for 2022 (New Year falls on Saturday)
      Holiday result = holidayOperations.calculateObservedDate(originalNewYear, 2022);

      // Then: Should apply Saturday -> Friday rule
      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday calculatedNewYear = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2022, Month.JANUARY, 1), calculatedNewYear.date()); // Saturday
      assertEquals(LocalDate.of(2021, Month.DECEMBER, 31), calculatedNewYear.observed()); // Friday
    }

    @Test
    @DisplayName("getObservedDateOnly should return observed date")
    void testGetObservedDateOnlyObservedHoliday() {
      // Given: Christmas with specific observed date
      var christmas =
          new ObservedHoliday(
              "Christmas Day",
              "Christian celebration",
              LocalDate.of(2022, Month.DECEMBER, 25), // Sunday
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              LocalDate.of(2022, Month.DECEMBER, 26), // Monday (observed)
              true);

      // When: Get observed date
      LocalDate result = holidayOperations.getObservedDateOnly(christmas, 2022);

      // Then: Should return the observed date
      assertEquals(LocalDate.of(2022, Month.DECEMBER, 26), result);
    }
  }

  @Nested
  @DisplayName("MoveableHoliday Tests")
  class MoveableHolidayTests {

    @Test
    @DisplayName("calculateDate should return new MoveableHoliday with calculated Easter date")
    void testCalculateDateMoveableHolidayEaster() {
      // Given: Easter with placeholder date
      var originalEaster =
          new MoveableHoliday(
              "Easter",
              "Christian celebration of resurrection",
              LocalDate.of(2023, Month.APRIL, 9), // Placeholder date
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER,
              false);

      // When: Calculate for 2025
      Holiday result = holidayOperations.calculateDate(originalEaster, 2025);

      // Then: Should return new MoveableHoliday with calculated Easter date
      assertInstanceOf(MoveableHoliday.class, result);
      MoveableHoliday calculatedEaster = (MoveableHoliday) result;

      // Easter 2025 is April 20th
      assertEquals(LocalDate.of(2025, Month.APRIL, 20), calculatedEaster.date());
      assertEquals(KnownHoliday.EASTER, calculatedEaster.knownHoliday());
      assertNotSame(originalEaster, result);
    }

    @Test
    @DisplayName(
        "calculateDate should return new MoveableHoliday with calculated Thanksgiving date")
    void testCalculateDateMoveableHolidayThanksgiving() {
      // Given: Thanksgiving with placeholder date
      var originalThanksgiving =
          new MoveableHoliday(
              "Thanksgiving",
              "US harvest celebration",
              LocalDate.of(2023, Month.NOVEMBER, 23), // Placeholder
              US_LOCALITIES,
              HolidayType.NATIONAL,
              KnownHoliday.THANKSGIVING_US,
              false);

      // When: Calculate for 2024
      Holiday result = holidayOperations.calculateDate(originalThanksgiving, 2024);

      // Then: Should return new MoveableHoliday with calculated Thanksgiving date
      assertInstanceOf(MoveableHoliday.class, result);
      MoveableHoliday calculatedThanksgiving = (MoveableHoliday) result;

      // Thanksgiving 2024 is November 28th (4th Thursday)
      assertEquals(LocalDate.of(2024, Month.NOVEMBER, 28), calculatedThanksgiving.date());
      assertEquals(KnownHoliday.THANKSGIVING_US, calculatedThanksgiving.knownHoliday());
    }

    @Test
    @DisplayName(
        "calculateObservedDate should handle mondayisation correctly for moveable holidays")
    void testCalculateObservedDateMoveableHolidayWithMondayisation() {
      // Given: Memorial Day with mondayisation enabled (Memorial Day is always Monday)
      var originalMemorialDay =
          new MoveableHoliday(
              "Memorial Day",
              "US day of remembrance",
              LocalDate.of(2023, Month.MAY, 29),
              US_LOCALITIES,
              HolidayType.NATIONAL,
              KnownHoliday.MEMORIAL_DAY_US,
              true // mondayisation enabled
              );

      // When: Calculate observed date for 2025
      Holiday result = holidayOperations.calculateObservedDate(originalMemorialDay, 2025);

      // Then: Memorial Day 2025 is May 26th (Monday), so no mondayisation adjustment needed
      // Should stay as MoveableHoliday since Memorial Day is always on Monday
      assertInstanceOf(MoveableHoliday.class, result);
      MoveableHoliday calculatedMemorialDay = (MoveableHoliday) result;
      assertEquals(LocalDate.of(2025, Month.MAY, 26), calculatedMemorialDay.date());
    }

    @Test
    @DisplayName("getDateOnly should return calculated date for moveable holiday")
    void testGetDateOnlyMoveableHoliday() {
      // Given: Memorial Day
      var memorialDay =
          new MoveableHoliday(
              "Memorial Day",
              "US day of remembrance",
              LocalDate.of(2023, Month.MAY, 29), // Placeholder
              US_LOCALITIES,
              HolidayType.NATIONAL,
              KnownHoliday.MEMORIAL_DAY_US,
              false);

      // When: Get date for 2025
      LocalDate result = holidayOperations.getDateOnly(memorialDay, 2025);

      // Then: Should return calculated Memorial Day 2025 (last Monday of May)
      assertEquals(LocalDate.of(2025, Month.MAY, 26), result);
    }
  }

  @Nested
  @DisplayName("MoveableFromBaseHoliday Tests")
  class MoveableFromBaseHolidayTests {

    @Test
    @DisplayName("calculateDate should return new MoveableFromBaseHoliday with calculated date")
    void testCalculateDateMoveableFromBaseHoliday() {
      // Given: Easter as base holiday
      var easter =
          new MoveableHoliday(
              "Easter",
              "Christian celebration",
              LocalDate.of(2023, Month.APRIL, 9),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER,
              false);

      // Given: Good Friday derived from Easter
      var originalGoodFriday =
          new MoveableFromBaseHoliday(
              "Good Friday",
              "Christian observance of crucifixion",
              LocalDate.of(2023, Month.APRIL, 7), // Placeholder
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.GOOD_FRIDAY,
              easter,
              -2, // 2 days before Easter
              false);

      // When: Calculate for 2025
      Holiday result = holidayOperations.calculateDate(originalGoodFriday, 2025);

      // Then: Should return new MoveableFromBaseHoliday with calculated date
      assertInstanceOf(MoveableFromBaseHoliday.class, result);
      MoveableFromBaseHoliday calculatedGoodFriday = (MoveableFromBaseHoliday) result;

      // Easter 2025 is April 20th, so Good Friday is April 18th
      assertEquals(LocalDate.of(2025, Month.APRIL, 18), calculatedGoodFriday.date());
      assertEquals(KnownHoliday.GOOD_FRIDAY, calculatedGoodFriday.knownHoliday());
      assertEquals(-2, calculatedGoodFriday.dayOffset());
      assertNotSame(originalGoodFriday, result);
    }

    @Test
    @DisplayName("calculateDate should work with Easter Monday")
    void testCalculateDateEasterMonday() {
      // Given: Easter as base holiday
      var easter =
          new MoveableHoliday(
              "Easter",
              "Christian celebration",
              LocalDate.of(2023, Month.APRIL, 9),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER,
              false);

      // Given: Easter Monday derived from Easter
      var originalEasterMonday =
          new MoveableFromBaseHoliday(
              "Easter Monday",
              "Christian holiday following Easter",
              LocalDate.of(2023, Month.APRIL, 10), // Placeholder
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER_MONDAY,
              easter,
              1, // 1 day after Easter
              false);

      // When: Calculate for 2024
      Holiday result = holidayOperations.calculateDate(originalEasterMonday, 2024);

      // Then: Should return new MoveableFromBaseHoliday with calculated date
      assertInstanceOf(MoveableFromBaseHoliday.class, result);
      MoveableFromBaseHoliday calculatedEasterMonday = (MoveableFromBaseHoliday) result;

      // Easter 2024 is March 31st, so Easter Monday is April 1st
      assertEquals(LocalDate.of(2024, Month.APRIL, 1), calculatedEasterMonday.date());
      assertEquals(1, calculatedEasterMonday.dayOffset());
    }

    @Test
    @DisplayName(
        "calculateObservedDate should convert to ObservedHoliday when mondayisation needed")
    void testCalculateObservedDateMoveableFromBaseHolidayWithMondayisation() {
      // Given: Easter as base holiday
      var easter =
          new MoveableHoliday(
              "Easter",
              "Christian celebration",
              LocalDate.of(2023, Month.APRIL, 9),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER,
              false);

      // Given: Good Friday with mondayisation enabled (hypothetical scenario)
      var originalGoodFriday =
          new MoveableFromBaseHoliday(
              "Good Friday",
              "Christian observance",
              LocalDate.of(2023, Month.APRIL, 7),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.GOOD_FRIDAY,
              easter,
              -2,
              true // mondayisation enabled
              );

      // When: Calculate observed date for 2025
      Holiday result = holidayOperations.calculateObservedDate(originalGoodFriday, 2025);

      // Then: Good Friday 2025 is April 18th (Friday), so no mondayisation needed
      assertInstanceOf(MoveableFromBaseHoliday.class, result);
      MoveableFromBaseHoliday calculatedGoodFriday = (MoveableFromBaseHoliday) result;
      assertEquals(LocalDate.of(2025, Month.APRIL, 18), calculatedGoodFriday.date());
    }

    @Test
    @DisplayName("getDateOnly should return calculated derived date")
    void testGetDateOnlyMoveableFromBaseHoliday() {
      // Given: Easter as base holiday
      var easter =
          new MoveableHoliday(
              "Easter",
              "Christian celebration",
              LocalDate.of(2023, Month.APRIL, 9),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER,
              false);

      // Given: Palm Sunday (7 days before Easter)
      var palmSunday =
          new MoveableFromBaseHoliday(
              "Palm Sunday",
              "Christian holiday commemorating Jesus' entry into Jerusalem",
              LocalDate.of(2023, Month.APRIL, 2), // Placeholder
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.PALM_SUNDAY,
              easter,
              -7, // 7 days before Easter
              false);

      // When: Get date for 2026
      LocalDate result = holidayOperations.getDateOnly(palmSunday, 2026);

      // Then: Should return calculated Palm Sunday date
      // Easter 2026 is April 5th, so Palm Sunday is March 29th
      assertEquals(LocalDate.of(2026, Month.MARCH, 29), result);
    }
  }

  @Nested
  @DisplayName("Edge Cases and Error Handling")
  class EdgeCasesAndErrorHandling {

    @Test
    @DisplayName("calculateDate should throw exception for null holiday")
    void testCalculateDateNullHoliday() {
      assertThrows(NullPointerException.class, () -> holidayOperations.calculateDate(null, 2025));
    }

    @Test
    @DisplayName("calculateDate should throw exception for invalid year")
    void testCalculateDateInvalidYear() {
      var holiday =
          new FixedHoliday(
              "Test Holiday",
              "Test description",
              LocalDate.of(2023, Month.JANUARY, 1),
              1,
              Month.JANUARY,
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL);

      assertThrows(
          IllegalArgumentException.class, () -> holidayOperations.calculateDate(holiday, 0));
      assertThrows(
          IllegalArgumentException.class, () -> holidayOperations.calculateDate(holiday, -1));
    }

    @Test
    @DisplayName("calculateObservedDate should throw exception for null holiday")
    void testCalculateObservedDateNullHoliday() {
      assertThrows(
          NullPointerException.class, () -> holidayOperations.calculateObservedDate(null, 2025));
    }

    @Test
    @DisplayName("isWeekend should correctly identify weekend dates")
    void testIsWeekend() {
      // Given: Christmas 2022 (Sunday)
      var christmas =
          new FixedHoliday(
              "Christmas Day",
              "Christian celebration",
              LocalDate.of(2023, Month.DECEMBER, 25),
              25,
              Month.DECEMBER,
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS);

      // When/Then: Check different years
      assertTrue(holidayOperations.isWeekend(christmas, 2022)); // Sunday
      assertFalse(holidayOperations.isWeekend(christmas, 2023)); // Monday
      assertTrue(holidayOperations.isWeekend(christmas, 2021)); // Saturday
    }
  }

  @Nested
  @DisplayName("Specific Date Calculations")
  class SpecificDateCalculations {

    @Test
    @DisplayName("Easter calculation should be accurate for known years")
    void testEasterCalculation() {
      // Test known Easter dates
      assertEquals(LocalDate.of(2024, Month.MARCH, 31), holidayOperations.calculateEaster(2024));
      assertEquals(LocalDate.of(2025, Month.APRIL, 20), holidayOperations.calculateEaster(2025));
      assertEquals(LocalDate.of(2026, Month.APRIL, 5), holidayOperations.calculateEaster(2026));
    }

    @Test
    @DisplayName("Thanksgiving calculation should be accurate for known years")
    void testThanksgivingCalculation() {
      // Test known Thanksgiving dates (4th Thursday of November)
      assertEquals(
          LocalDate.of(2024, Month.NOVEMBER, 28), holidayOperations.calculateThanksgiving(2024));
      assertEquals(
          LocalDate.of(2025, Month.NOVEMBER, 27), holidayOperations.calculateThanksgiving(2025));
      assertEquals(
          LocalDate.of(2026, Month.NOVEMBER, 26), holidayOperations.calculateThanksgiving(2026));
    }

    @Test
    @DisplayName("Memorial Day calculation should be accurate for known years")
    void testMemorialDayCalculation() {
      // Test known Memorial Day dates (last Monday of May)
      assertEquals(LocalDate.of(2024, Month.MAY, 27), holidayOperations.calculateMemorialDay(2024));
      assertEquals(LocalDate.of(2025, Month.MAY, 26), holidayOperations.calculateMemorialDay(2025));
      assertEquals(LocalDate.of(2026, Month.MAY, 25), holidayOperations.calculateMemorialDay(2026));
    }
  }

  @Nested
  @DisplayName("Mondayisation Rules")
  class MondayisationRules {

    @Test
    @DisplayName("Saturday holidays should be observed on Friday")
    void testSaturdayMondayisation() {
      // Given: Holiday that falls on Saturday (January 1, 2022)
      var newYear =
          new ObservedHoliday(
              "New Year's Day",
              "First day of the year",
              LocalDate.of(2021, Month.JANUARY, 1), // Placeholder
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL,
              LocalDate.of(2021, Month.JANUARY, 1), // Placeholder
              true);

      // When: Calculate observed date for 2022 (Saturday)
      Holiday result = holidayOperations.calculateObservedDate(newYear, 2022);

      // Then: Should be observed on Friday
      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday observedNewYear = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2022, Month.JANUARY, 1), observedNewYear.date()); // Saturday
      assertEquals(LocalDate.of(2021, Month.DECEMBER, 31), observedNewYear.observed()); // Friday
    }

    @Test
    @DisplayName("Sunday holidays should be observed on Monday")
    void testSundayMondayisation() {
      // Given: Holiday that falls on Sunday (January 1, 2023)
      var newYear =
          new ObservedHoliday(
              "New Year's Day",
              "First day of the year",
              LocalDate.of(2021, Month.JANUARY, 1), // Placeholder
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL,
              LocalDate.of(2021, Month.JANUARY, 1), // Placeholder
              true);

      // When: Calculate observed date for 2023 (Sunday)
      Holiday result = holidayOperations.calculateObservedDate(newYear, 2023);

      // Then: Should be observed on Monday
      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday observedNewYear = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2023, Month.JANUARY, 1), observedNewYear.date()); // Sunday
      assertEquals(LocalDate.of(2023, Month.JANUARY, 2), observedNewYear.observed()); // Monday
    }

    @Test
    @DisplayName("Weekday holidays should not be adjusted")
    void testWeekdayNoMondayisation() {
      // Given: Holiday that falls on weekday (January 1, 2024 - Monday)
      var newYear =
          new ObservedHoliday(
              "New Year's Day",
              "First day of the year",
              LocalDate.of(2021, Month.JANUARY, 1), // Placeholder
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL,
              LocalDate.of(2021, Month.JANUARY, 1), // Placeholder
              true);

      // When: Calculate observed date for 2024 (Monday)
      Holiday result = holidayOperations.calculateObservedDate(newYear, 2024);

      // Then: Should remain on the same date
      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday observedNewYear = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2024, Month.JANUARY, 1), observedNewYear.date()); // Monday
      assertEquals(LocalDate.of(2024, Month.JANUARY, 1), observedNewYear.observed()); // Same Monday
    }
  }
}
