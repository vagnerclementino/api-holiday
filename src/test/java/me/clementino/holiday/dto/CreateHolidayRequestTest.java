package me.clementino.holiday.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.domain.dop.Locality;
import org.junit.jupiter.api.Test;

/** Test class demonstrating DOP principles with CreateHolidayRequest. */
class CreateHolidayRequestTest {

  @Test
  void shouldCreateFixedHolidayWithSpecificYear() {
    // Given: Christmas 2024
    var christmas2024 =
        new CreateHolidayRequest.Fixed(
            "Christmas Day",
            "Christian holiday celebrating the birth of Jesus Christ",
            25, // day
            Month.DECEMBER, // month using java.time.Month
            2024, // specific year
            List.of(new Locality.Country("US", "United States")),
            HolidayType.NATIONAL);

    // When: Get the date
    LocalDate date = christmas2024.date();

    // Then: Should be December 25, 2024
    assertEquals(LocalDate.of(2024, 12, 25), date);
    assertFalse(christmas2024.isRecurring());
    assertEquals("Christmas Day", christmas2024.name());
    assertEquals(25, christmas2024.day());
    assertEquals(Month.DECEMBER, christmas2024.month());
    assertEquals(2024, christmas2024.year());
  }

  @Test
  void shouldCreateRecurringFixedHoliday() {
    // Given: New Year (recurring every year)
    var newYear =
        new CreateHolidayRequest.Fixed(
            "New Year's Day",
            "First day of the year",
            1, // day
            Month.JANUARY, // month
            null, // no specific year - recurring
            List.of(new Locality.Country("BR", "Brazil")),
            HolidayType.NATIONAL);

    // When: Get the date
    LocalDate date = newYear.date();

    // Then: Should use current year
    assertEquals(1, date.getDayOfMonth());
    assertEquals(Month.JANUARY, date.getMonth());
    assertEquals(LocalDate.now().getYear(), date.getYear());
    assertTrue(newYear.isRecurring());
    assertNull(newYear.year());
  }

  @Test
  void shouldValidateValidDayMonthCombinations() {
    // Given/When/Then: Valid combinations should work
    assertDoesNotThrow(
        () ->
            new CreateHolidayRequest.Fixed(
                "Valentine's Day",
                "Day of love",
                14,
                Month.FEBRUARY,
                null,
                List.of(new Locality.Country("US", "United States")),
                HolidayType.COMMERCIAL));

    assertDoesNotThrow(
        () ->
            new CreateHolidayRequest.Fixed(
                "Leap Day",
                "Extra day in leap year",
                29,
                Month.FEBRUARY,
                null, // Valid - February can have 29 days in leap years
                List.of(new Locality.Country("US", "United States")),
                HolidayType.COMMERCIAL));

    assertDoesNotThrow(
        () ->
            new CreateHolidayRequest.Fixed(
                "Halloween",
                "Spooky day",
                31,
                Month.OCTOBER,
                null, // Valid - October has 31 days
                List.of(new Locality.Country("US", "United States")),
                HolidayType.COMMERCIAL));
  }

  @Test
  void shouldRejectInvalidDayMonthCombinations() {
    // Given/When/Then: Invalid combinations should throw exceptions

    // February 31st - invalid
    IllegalArgumentException feb31Exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new CreateHolidayRequest.Fixed(
                    "Invalid Holiday",
                    "This should fail",
                    31,
                    Month.FEBRUARY,
                    null,
                    List.of(new Locality.Country("US", "United States")),
                    HolidayType.COMMERCIAL));
    assertTrue(feb31Exception.getMessage().contains("Invalid day 31 for month FEBRUARY"));

    // April 31st - invalid (April has only 30 days)
    IllegalArgumentException apr31Exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new CreateHolidayRequest.Fixed(
                    "Invalid Holiday",
                    "This should fail",
                    31,
                    Month.APRIL,
                    null,
                    List.of(new Locality.Country("US", "United States")),
                    HolidayType.COMMERCIAL));
    assertTrue(apr31Exception.getMessage().contains("Invalid day 31 for month APRIL"));

    // June 31st - invalid (June has only 30 days)
    IllegalArgumentException jun31Exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new CreateHolidayRequest.Fixed(
                    "Invalid Holiday",
                    "This should fail",
                    31,
                    Month.JUNE,
                    null,
                    List.of(new Locality.Country("US", "United States")),
                    HolidayType.COMMERCIAL));
    assertTrue(jun31Exception.getMessage().contains("Invalid day 31 for month JUNE"));
  }

  @Test
  void shouldCreateFixedHolidayWithValidation() {
    // Given: Valid holiday data
    var independenceDay =
        new CreateHolidayRequest.Fixed(
            "Independence Day",
            "Brazilian Independence Day",
            7, // September 7th
            Month.SEPTEMBER,
            null, // recurring
            List.of(new Locality.Country("BR", "Brazil")),
            HolidayType.NATIONAL);

    // When/Then: Should create successfully
    assertNotNull(independenceDay);
    assertEquals("Independence Day", independenceDay.name());
    assertEquals(7, independenceDay.day());
    assertEquals(Month.SEPTEMBER, independenceDay.month());
    assertTrue(independenceDay.isRecurring());
  }

  @Test
  void shouldDemonstratePatternMatchingWithSealedInterface() {
    // Given: Different types of holiday requests
    CreateHolidayRequest christmas =
        new CreateHolidayRequest.Fixed(
            "Christmas",
            "Christian holiday",
            25,
            Month.DECEMBER,
            2024,
            List.of(new Locality.Country("US", "United States")),
            HolidayType.NATIONAL);

    CreateHolidayRequest observed =
        new CreateHolidayRequest.Observed(
            "Christmas Observed",
            "Observed Christmas",
            LocalDate.of(2024, 12, 25),
            List.of(new Locality.Country("US", "United States")),
            HolidayType.NATIONAL,
            LocalDate.of(2024, 12, 26), // observed on 26th
            true);

    // When: Use pattern matching to handle different types
    String christmasInfo = getHolidayInfo(christmas);
    String observedInfo = getHolidayInfo(observed);

    // Then: Should handle each type appropriately
    assertTrue(christmasInfo.contains("Fixed holiday"));
    assertTrue(christmasInfo.contains("Christmas"));
    assertTrue(christmasInfo.contains("DECEMBER"));
    assertTrue(observedInfo.contains("Observed holiday"));
    assertTrue(observedInfo.contains("Christmas Observed"));
  }

  @Test
  void shouldTestMonthEnumUsage() {
    // Given: Holiday using Month enum
    var holiday =
        new CreateHolidayRequest.Fixed(
            "Test Holiday",
            "Testing Month enum",
            15,
            Month.MARCH,
            2024,
            List.of(new Locality.Country("BR", "Brazil")),
            HolidayType.NATIONAL);

    // When/Then: Should work with Month enum methods
    assertEquals(Month.MARCH, holiday.month());
    assertEquals(3, holiday.month().getValue());
    assertEquals("MARCH", holiday.month().name());
    assertEquals(31, holiday.month().length(false)); // March has 31 days
    assertEquals(LocalDate.of(2024, 3, 15), holiday.date());
  }

  /** Demonstrates pattern matching with sealed interfaces - a key DOP principle. */
  private String getHolidayInfo(CreateHolidayRequest request) {
    return switch (request) {
      case CreateHolidayRequest.Fixed fixed ->
          "Fixed holiday: "
              + fixed.name()
              + " on "
              + fixed.day()
              + "/"
              + fixed.month()
              + (fixed.year() != null ? "/" + fixed.year() : " (recurring)");
      case CreateHolidayRequest.Observed observed ->
          "Observed holiday: " + observed.name() + " observed on " + observed.observed();
      case CreateHolidayRequest.Moveable moveable -> "Moveable holiday: " + moveable.name();
      case CreateHolidayRequest.MoveableFromBase moveableFromBase ->
          "Moveable from base holiday: " + moveableFromBase.name();
    };
  }
}
