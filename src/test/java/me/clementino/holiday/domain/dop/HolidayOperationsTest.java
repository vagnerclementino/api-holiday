package me.clementino.holiday.domain.dop;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for HolidayOperations covering all holiday types. Tests both calculateDate
 * and calculateObservedDate methods to ensure they return new instances with correctly calculated
 * dates.
 */
@DisplayName("HolidayOperations Tests")
@Tag("unit")
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
      var originalChristmas =
          new FixedHoliday(
              "Christmas Day",
              "Christian celebration",
              LocalDate.of(2023, Month.DECEMBER, 25),
              25,
              Month.DECEMBER,
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS);

      Holiday result = holidayOperations.calculateDate(originalChristmas, 2025);

      assertInstanceOf(FixedHoliday.class, result);
      FixedHoliday calculatedChristmas = (FixedHoliday) result;

      assertEquals(LocalDate.of(2025, Month.DECEMBER, 25), calculatedChristmas.date());
      assertEquals("Christmas Day", calculatedChristmas.name());
      assertEquals("Christian celebration", calculatedChristmas.description());
      assertEquals(BRAZIL_LOCALITIES, calculatedChristmas.localities());
      assertEquals(HolidayType.RELIGIOUS, calculatedChristmas.type());

      assertNotSame(originalChristmas, result);
    }

    @Test
    @DisplayName("calculateObservedDate should return same as calculateDate for FixedHoliday")
    void testCalculateObservedDateFixedHoliday() {
      var originalNewYear =
          new FixedHoliday(
              "New Year's Day",
              "First day of the year",
              LocalDate.of(2023, Month.JANUARY, 1),
              1,
              Month.JANUARY,
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL);

      Holiday result = holidayOperations.calculateObservedDate(originalNewYear, 2025);

      assertInstanceOf(FixedHoliday.class, result);
      FixedHoliday calculatedNewYear = (FixedHoliday) result;

      assertEquals(LocalDate.of(2025, Month.JANUARY, 1), calculatedNewYear.date());
      assertNotSame(originalNewYear, result);
    }

    @Test
    @DisplayName("getDateOnly should return correct LocalDate")
    void testGetDateOnlyFixedHoliday() {
      var independenceDay =
          new FixedHoliday(
              "Independence Day",
              "Brazil's independence",
              LocalDate.of(2023, Month.SEPTEMBER, 7),
              7,
              Month.SEPTEMBER,
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL);

      LocalDate result = holidayOperations.getDateOnly(independenceDay, 2026);

      assertEquals(LocalDate.of(2026, Month.SEPTEMBER, 7), result);
    }
  }

  @Nested
  @DisplayName("ObservedHoliday Tests")
  class ObservedHolidayTests {

    @Test
    @DisplayName("calculateDate should return new ObservedHoliday with calculated dates")
    void testCalculateDateObservedHoliday() {
      var originalChristmas =
          new ObservedHoliday(
              "Christmas Day",
              "Christian celebration",
              LocalDate.of(2023, Month.DECEMBER, 25),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              LocalDate.of(2023, Month.DECEMBER, 25),
              true);

      Holiday result = holidayOperations.calculateDate(originalChristmas, 2022);

      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday calculatedChristmas = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2022, Month.DECEMBER, 25), calculatedChristmas.date());
      assertEquals(LocalDate.of(2022, Month.DECEMBER, 26), calculatedChristmas.observed());
      assertTrue(calculatedChristmas.mondayisation());
      assertNotSame(originalChristmas, result);
    }

    @Test
    @DisplayName("calculateDate should not apply mondayisation when disabled")
    void testCalculateDateObservedHolidayNoMondayisation() {
      var originalChristmas =
          new ObservedHoliday(
              "Christmas Day",
              "Christian celebration",
              LocalDate.of(2023, Month.DECEMBER, 25),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              LocalDate.of(2023, Month.DECEMBER, 25),
              false);

      Holiday result = holidayOperations.calculateDate(originalChristmas, 2022);

      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday calculatedChristmas = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2022, Month.DECEMBER, 25), calculatedChristmas.date());
      assertEquals(LocalDate.of(2022, Month.DECEMBER, 25), calculatedChristmas.observed());
      assertFalse(calculatedChristmas.mondayisation());
    }

    @Test
    @DisplayName("calculateObservedDate should apply mondayisation rules")
    void testCalculateObservedDateObservedHoliday() {
      var originalNewYear =
          new ObservedHoliday(
              "New Year's Day",
              "First day of the year",
              LocalDate.of(2023, Month.JANUARY, 1),
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL,
              LocalDate.of(2023, Month.JANUARY, 2),
              true);

      Holiday result = holidayOperations.calculateObservedDate(originalNewYear, 2022);

      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday calculatedNewYear = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2022, Month.JANUARY, 1), calculatedNewYear.date());
      assertEquals(LocalDate.of(2021, Month.DECEMBER, 31), calculatedNewYear.observed());
    }

    @Test
    @DisplayName("getObservedDateOnly should return observed date")
    void testGetObservedDateOnlyObservedHoliday() {
      var christmas =
          new ObservedHoliday(
              "Christmas Day",
              "Christian celebration",
              LocalDate.of(2022, Month.DECEMBER, 25),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              LocalDate.of(2022, Month.DECEMBER, 26),
              true);

      LocalDate result = holidayOperations.getObservedDateOnly(christmas, 2022);

      assertEquals(LocalDate.of(2022, Month.DECEMBER, 26), result);
    }
  }

  @Nested
  @DisplayName("MoveableHoliday Tests")
  class MoveableHolidayTests {

    @Test
    @DisplayName("calculateDate should return new MoveableHoliday with calculated Easter date")
    void testCalculateDateMoveableHolidayEaster() {
      var originalEaster =
          new MoveableHoliday(
              "Easter",
              "Christian celebration of resurrection",
              LocalDate.of(2023, Month.APRIL, 9),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER,
              false);

      Holiday result = holidayOperations.calculateDate(originalEaster, 2025);

      assertInstanceOf(MoveableHoliday.class, result);
      MoveableHoliday calculatedEaster = (MoveableHoliday) result;

      assertEquals(LocalDate.of(2025, Month.APRIL, 20), calculatedEaster.date());
      assertEquals(KnownHoliday.EASTER, calculatedEaster.knownHoliday());
      assertNotSame(originalEaster, result);
    }

    @Test
    @DisplayName(
        "calculateDate should return new MoveableHoliday with calculated Thanksgiving date")
    void testCalculateDateMoveableHolidayThanksgiving() {
      var originalThanksgiving =
          new MoveableHoliday(
              "Thanksgiving",
              "US harvest celebration",
              LocalDate.of(2023, Month.NOVEMBER, 23),
              US_LOCALITIES,
              HolidayType.NATIONAL,
              KnownHoliday.THANKSGIVING_US,
              false);

      Holiday result = holidayOperations.calculateDate(originalThanksgiving, 2024);

      assertInstanceOf(MoveableHoliday.class, result);
      MoveableHoliday calculatedThanksgiving = (MoveableHoliday) result;

      assertEquals(LocalDate.of(2024, Month.NOVEMBER, 28), calculatedThanksgiving.date());
      assertEquals(KnownHoliday.THANKSGIVING_US, calculatedThanksgiving.knownHoliday());
    }

    @Test
    @DisplayName(
        "calculateObservedDate should handle mondayisation correctly for moveable holidays")
    void testCalculateObservedDateMoveableHolidayWithMondayisation() {
      var originalMemorialDay =
          new MoveableHoliday(
              "Memorial Day",
              "US day of remembrance",
              LocalDate.of(2023, Month.MAY, 29),
              US_LOCALITIES,
              HolidayType.NATIONAL,
              KnownHoliday.MEMORIAL_DAY_US,
              true);

      Holiday result = holidayOperations.calculateObservedDate(originalMemorialDay, 2025);

      assertInstanceOf(MoveableHoliday.class, result);
      MoveableHoliday calculatedMemorialDay = (MoveableHoliday) result;
      assertEquals(LocalDate.of(2025, Month.MAY, 26), calculatedMemorialDay.date());
    }

    @Test
    @DisplayName("getDateOnly should return calculated date for moveable holiday")
    void testGetDateOnlyMoveableHoliday() {
      var memorialDay =
          new MoveableHoliday(
              "Memorial Day",
              "US day of remembrance",
              LocalDate.of(2023, Month.MAY, 29),
              US_LOCALITIES,
              HolidayType.NATIONAL,
              KnownHoliday.MEMORIAL_DAY_US,
              false);

      LocalDate result = holidayOperations.getDateOnly(memorialDay, 2025);

      assertEquals(LocalDate.of(2025, Month.MAY, 26), result);
    }
  }

  @Nested
  @DisplayName("MoveableFromBaseHoliday Tests")
  class MoveableFromBaseHolidayTests {

    @Test
    @DisplayName("calculateDate should return new MoveableFromBaseHoliday with calculated date")
    void testCalculateDateMoveableFromBaseHoliday() {
      var easter =
          new MoveableHoliday(
              "Easter",
              "Christian celebration",
              LocalDate.of(2023, Month.APRIL, 9),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER,
              false);

      var originalGoodFriday =
          new MoveableFromBaseHoliday(
              "Good Friday",
              "Christian observance of crucifixion",
              LocalDate.of(2023, Month.APRIL, 7),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.GOOD_FRIDAY,
              easter,
              -2,
              false);

      Holiday result = holidayOperations.calculateDate(originalGoodFriday, 2025);

      assertInstanceOf(MoveableFromBaseHoliday.class, result);
      MoveableFromBaseHoliday calculatedGoodFriday = (MoveableFromBaseHoliday) result;

      assertEquals(LocalDate.of(2025, Month.APRIL, 18), calculatedGoodFriday.date());
      assertEquals(KnownHoliday.GOOD_FRIDAY, calculatedGoodFriday.knownHoliday());
      assertEquals(-2, calculatedGoodFriday.dayOffset());
      assertNotSame(originalGoodFriday, result);
    }

    @Test
    @DisplayName("calculateDate should work with Easter Monday")
    void testCalculateDateEasterMonday() {
      var easter =
          new MoveableHoliday(
              "Easter",
              "Christian celebration",
              LocalDate.of(2023, Month.APRIL, 9),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER,
              false);

      var originalEasterMonday =
          new MoveableFromBaseHoliday(
              "Easter Monday",
              "Christian holiday following Easter",
              LocalDate.of(2023, Month.APRIL, 10),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER_MONDAY,
              easter,
              1,
              false);

      Holiday result = holidayOperations.calculateDate(originalEasterMonday, 2024);

      assertInstanceOf(MoveableFromBaseHoliday.class, result);
      MoveableFromBaseHoliday calculatedEasterMonday = (MoveableFromBaseHoliday) result;

      assertEquals(LocalDate.of(2024, Month.APRIL, 1), calculatedEasterMonday.date());
      assertEquals(1, calculatedEasterMonday.dayOffset());
    }

    @Test
    @DisplayName(
        "calculateObservedDate should convert to ObservedHoliday when mondayisation needed")
    void testCalculateObservedDateMoveableFromBaseHolidayWithMondayisation() {
      var easter =
          new MoveableHoliday(
              "Easter",
              "Christian celebration",
              LocalDate.of(2023, Month.APRIL, 9),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER,
              false);

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
              true);

      Holiday result = holidayOperations.calculateObservedDate(originalGoodFriday, 2025);

      assertInstanceOf(MoveableFromBaseHoliday.class, result);
      MoveableFromBaseHoliday calculatedGoodFriday = (MoveableFromBaseHoliday) result;
      assertEquals(LocalDate.of(2025, Month.APRIL, 18), calculatedGoodFriday.date());
    }

    @Test
    @DisplayName("getDateOnly should return calculated derived date")
    void testGetDateOnlyMoveableFromBaseHoliday() {
      var easter =
          new MoveableHoliday(
              "Easter",
              "Christian celebration",
              LocalDate.of(2023, Month.APRIL, 9),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.EASTER,
              false);

      var palmSunday =
          new MoveableFromBaseHoliday(
              "Palm Sunday",
              "Christian holiday commemorating Jesus' entry into Jerusalem",
              LocalDate.of(2023, Month.APRIL, 2),
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS,
              KnownHoliday.PALM_SUNDAY,
              easter,
              -7,
              false);

      LocalDate result = holidayOperations.getDateOnly(palmSunday, 2026);

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
      var christmas =
          new FixedHoliday(
              "Christmas Day",
              "Christian celebration",
              LocalDate.of(2023, Month.DECEMBER, 25),
              25,
              Month.DECEMBER,
              BRAZIL_LOCALITIES,
              HolidayType.RELIGIOUS);

      assertTrue(holidayOperations.isWeekend(christmas, 2022));
      assertFalse(holidayOperations.isWeekend(christmas, 2023));
      assertTrue(holidayOperations.isWeekend(christmas, 2021));
    }
  }

  @Nested
  @DisplayName("Specific Date Calculations")
  class SpecificDateCalculations {

    @Test
    @DisplayName("Easter calculation should be accurate for known years")
    void testEasterCalculation() {
      assertEquals(LocalDate.of(2024, Month.MARCH, 31), holidayOperations.calculateEaster(2024));
      assertEquals(LocalDate.of(2025, Month.APRIL, 20), holidayOperations.calculateEaster(2025));
      assertEquals(LocalDate.of(2026, Month.APRIL, 5), holidayOperations.calculateEaster(2026));
    }

    @Test
    @DisplayName("Thanksgiving calculation should be accurate for known years")
    void testThanksgivingCalculation() {
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
      var newYear =
          new ObservedHoliday(
              "New Year's Day",
              "First day of the year",
              LocalDate.of(2021, Month.JANUARY, 1),
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL,
              LocalDate.of(2021, Month.JANUARY, 1),
              true);

      Holiday result = holidayOperations.calculateObservedDate(newYear, 2022);

      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday observedNewYear = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2022, Month.JANUARY, 1), observedNewYear.date());
      assertEquals(LocalDate.of(2021, Month.DECEMBER, 31), observedNewYear.observed());
    }

    @Test
    @DisplayName("Sunday holidays should be observed on Monday")
    void testSundayMondayisation() {
      var newYear =
          new ObservedHoliday(
              "New Year's Day",
              "First day of the year",
              LocalDate.of(2021, Month.JANUARY, 1),
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL,
              LocalDate.of(2021, Month.JANUARY, 1),
              true);

      Holiday result = holidayOperations.calculateObservedDate(newYear, 2023);

      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday observedNewYear = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2023, Month.JANUARY, 1), observedNewYear.date());
      assertEquals(LocalDate.of(2023, Month.JANUARY, 2), observedNewYear.observed());
    }

    @Test
    @DisplayName("Weekday holidays should not be adjusted")
    void testWeekdayNoMondayisation() {
      var newYear =
          new ObservedHoliday(
              "New Year's Day",
              "First day of the year",
              LocalDate.of(2021, Month.JANUARY, 1),
              BRAZIL_LOCALITIES,
              HolidayType.NATIONAL,
              LocalDate.of(2021, Month.JANUARY, 1),
              true);

      Holiday result = holidayOperations.calculateObservedDate(newYear, 2024);

      assertInstanceOf(ObservedHoliday.class, result);
      ObservedHoliday observedNewYear = (ObservedHoliday) result;

      assertEquals(LocalDate.of(2024, Month.JANUARY, 1), observedNewYear.date());
      assertEquals(LocalDate.of(2024, Month.JANUARY, 1), observedNewYear.observed());
    }
  }
}
