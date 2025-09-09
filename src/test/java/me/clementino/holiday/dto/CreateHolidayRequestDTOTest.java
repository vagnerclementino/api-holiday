package me.clementino.holiday.dto;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import me.clementino.holiday.domain.dop.HolidayType;
import me.clementino.holiday.domain.dop.Locality;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/** Test class demonstrating DOP principles with CreateHolidayRequestDTO. */
@Tag("unit")
class CreateHolidayRequestDTOTest {

  @Test
  void shouldCreateFixedHolidayWithSpecificYear() {
    var christmas2024 =
        new CreateHolidayRequestDTO.Fixed(
            "Christmas Day",
            "Christian holiday celebrating the birth of Jesus Christ",
            25,
            Month.DECEMBER,
            2024,
            List.of(new Locality.Country("US", "United States")),
            HolidayType.NATIONAL);

    int effectiveYear =
        christmas2024.year() != null ? christmas2024.year() : LocalDate.now().getYear();
    LocalDate date = LocalDate.of(effectiveYear, christmas2024.month(), christmas2024.day());

    assertEquals(LocalDate.of(2024, 12, 25), date);
    assertFalse(christmas2024.year() == null);
    assertEquals("Christmas Day", christmas2024.name());
    assertEquals(25, christmas2024.day());
    assertEquals(Month.DECEMBER, christmas2024.month());
    assertEquals(2024, christmas2024.year());
  }

  @Test
  void shouldCreateRecurringFixedHoliday() {
    var newYear =
        new CreateHolidayRequestDTO.Fixed(
            "New Year's Day",
            "First day of the year",
            1,
            Month.JANUARY,
            null,
            List.of(new Locality.Country("BR", "Brazil")),
            HolidayType.NATIONAL);

    int effectiveYear = newYear.year() != null ? newYear.year() : LocalDate.now().getYear();
    LocalDate date = LocalDate.of(effectiveYear, newYear.month(), newYear.day());

    assertEquals(1, date.getDayOfMonth());
    assertEquals(Month.JANUARY, date.getMonth());
    assertEquals(LocalDate.now().getYear(), date.getYear());
    assertTrue(newYear.year() == null);
    assertNull(newYear.year());
  }

  @Test
  void shouldValidateValidDayMonthCombinations() {
    assertDoesNotThrow(
        () ->
            new CreateHolidayRequestDTO.Fixed(
                "Valentine's Day",
                "Day of love",
                14,
                Month.FEBRUARY,
                null,
                List.of(new Locality.Country("US", "United States")),
                HolidayType.COMMERCIAL));

    assertDoesNotThrow(
        () ->
            new CreateHolidayRequestDTO.Fixed(
                "Leap Day",
                "Extra day in leap year",
                29,
                Month.FEBRUARY,
                null,
                List.of(new Locality.Country("US", "United States")),
                HolidayType.COMMERCIAL));

    assertDoesNotThrow(
        () ->
            new CreateHolidayRequestDTO.Fixed(
                "Halloween",
                "Spooky day",
                31,
                Month.OCTOBER,
                null,
                List.of(new Locality.Country("US", "United States")),
                HolidayType.COMMERCIAL));
  }

  @Test
  void shouldRejectInvalidDayMonthCombinations() {

    IllegalArgumentException feb31Exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new CreateHolidayRequestDTO.Fixed(
                    "Invalid Holiday",
                    "This should fail",
                    31,
                    Month.FEBRUARY,
                    null,
                    List.of(new Locality.Country("US", "United States")),
                    HolidayType.COMMERCIAL));
    assertTrue(feb31Exception.getMessage().contains("Invalid day 31 for month FEBRUARY"));

    IllegalArgumentException apr31Exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new CreateHolidayRequestDTO.Fixed(
                    "Invalid Holiday",
                    "This should fail",
                    31,
                    Month.APRIL,
                    null,
                    List.of(new Locality.Country("US", "United States")),
                    HolidayType.COMMERCIAL));
    assertTrue(apr31Exception.getMessage().contains("Invalid day 31 for month APRIL"));

    IllegalArgumentException jun31Exception =
        assertThrows(
            IllegalArgumentException.class,
            () ->
                new CreateHolidayRequestDTO.Fixed(
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
    var independenceDay =
        new CreateHolidayRequestDTO.Fixed(
            "Independence Day",
            "Brazilian Independence Day",
            7,
            Month.SEPTEMBER,
            null,
            List.of(new Locality.Country("BR", "Brazil")),
            HolidayType.NATIONAL);

    assertNotNull(independenceDay);
    assertEquals("Independence Day", independenceDay.name());
    assertEquals(7, independenceDay.day());
    assertEquals(Month.SEPTEMBER, independenceDay.month());
    assertTrue(independenceDay.year() == null);
  }

  @Test
  void shouldDemonstratePatternMatchingWithSealedInterface() {
    CreateHolidayRequestDTO christmas =
        new CreateHolidayRequestDTO.Fixed(
            "Christmas",
            "Christian holiday",
            25,
            Month.DECEMBER,
            2024,
            List.of(new Locality.Country("US", "United States")),
            HolidayType.NATIONAL);

    CreateHolidayRequestDTO observed =
        new CreateHolidayRequestDTO.Observed(
            "Christmas Observed",
            "Observed Christmas",
            LocalDate.of(2024, 12, 25),
            List.of(new Locality.Country("US", "United States")),
            HolidayType.NATIONAL,
            LocalDate.of(2024, 12, 26),
            true);

    String christmasInfo = getHolidayInfo(christmas);
    String observedInfo = getHolidayInfo(observed);

    assertTrue(christmasInfo.contains("Fixed holiday"));
    assertTrue(christmasInfo.contains("Christmas"));
    assertTrue(christmasInfo.contains("DECEMBER"));
    assertTrue(observedInfo.contains("Observed holiday"));
    assertTrue(observedInfo.contains("Christmas Observed"));
  }

  @Test
  void shouldTestMonthEnumUsage() {
    var holiday =
        new CreateHolidayRequestDTO.Fixed(
            "Test Holiday",
            "Testing Month enum",
            15,
            Month.MARCH,
            2024,
            List.of(new Locality.Country("BR", "Brazil")),
            HolidayType.NATIONAL);

    assertEquals(Month.MARCH, holiday.month());
    assertEquals(3, holiday.month().getValue());
    assertEquals("MARCH", holiday.month().name());
    assertEquals(31, holiday.month().length(false));

    int effectiveYear = holiday.year() != null ? holiday.year() : LocalDate.now().getYear();
    LocalDate calculatedDate = LocalDate.of(effectiveYear, holiday.month(), holiday.day());
    assertEquals(LocalDate.of(2024, 3, 15), calculatedDate);
  }

  /** Demonstrates pattern matching with sealed interfaces - a key DOP principle. */
  private String getHolidayInfo(CreateHolidayRequestDTO request) {
    return switch (request) {
      case CreateHolidayRequestDTO.Fixed fixed ->
          "Fixed holiday: "
              + fixed.name()
              + " on "
              + fixed.day()
              + "/"
              + fixed.month()
              + (fixed.year() != null ? "/" + fixed.year() : " (recurring)");
      case CreateHolidayRequestDTO.Observed observed ->
          "Observed holiday: " + observed.name() + " observed on " + observed.observed();
      case CreateHolidayRequestDTO.Moveable moveable -> "Moveable holiday: " + moveable.name();
      case CreateHolidayRequestDTO.MoveableFromBase moveableFromBase ->
          "Moveable from base holiday: " + moveableFromBase.name();
    };
  }
}
