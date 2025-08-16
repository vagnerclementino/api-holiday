package me.clementino.holiday.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayData;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.domain.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("YearCalculatorService Tests")
class YearCalculatorServiceTest {

  @Mock private HolidayService holidayService;

  private YearCalculatorService yearCalculatorService;

  private HolidayData baseHoliday;

  @BeforeEach
  void setUp() {
    yearCalculatorService = new YearCalculatorService(holidayService);

    // Create a base recurring holiday for testing
    baseHoliday =
        new HolidayData(
            "test-holiday-1",
            "New Year's Day",
            LocalDate.of(2024, 1, 1),
            Optional.empty(),
            new Location("Brazil", Optional.empty(), Optional.empty()),
            HolidayType.NATIONAL,
            true, // recurring
            Optional.of("New Year celebration"),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());
  }

  @Test
  @DisplayName("Should calculate holiday for specific year")
  void shouldCalculateHolidayForSpecificYear() {
    // Given
    int targetYear = 2025;

    // When
    Optional<HolidayData> result =
        yearCalculatorService.calculateHolidayForYear(baseHoliday, targetYear);

    // Then
    assertThat(result).isPresent();
    HolidayData calculatedHoliday = result.get();

    assertThat(calculatedHoliday.name()).isEqualTo("New Year's Day");
    assertThat(calculatedHoliday.date()).isEqualTo(LocalDate.of(2025, 1, 1));
    assertThat(calculatedHoliday.recurring()).isFalse(); // Year-specific holidays are not recurring
    assertThat(calculatedHoliday.description()).hasValue("New Year celebration (2025)");
    assertThat(calculatedHoliday.id()).isEqualTo("test-holiday-1_2025");
  }

  @Test
  @DisplayName("Should not calculate non-recurring holiday for different year")
  void shouldNotCalculateNonRecurringHolidayForDifferentYear() {
    // Given
    HolidayData nonRecurringHoliday =
        new HolidayData(
            "non-recurring-1",
            "Special Event 2024",
            LocalDate.of(2024, 6, 15),
            Optional.empty(),
            new Location("Brazil", Optional.empty(), Optional.empty()),
            HolidayType.NATIONAL,
            false, // not recurring
            Optional.of("One-time special event"),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    int targetYear = 2025;

    // When
    Optional<HolidayData> result =
        yearCalculatorService.calculateHolidayForYear(nonRecurringHoliday, targetYear);

    // Then
    assertThat(result).isEmpty();
  }

  @Test
  @DisplayName("Should return non-recurring holiday for same year")
  void shouldReturnNonRecurringHolidayForSameYear() {
    // Given
    HolidayData nonRecurringHoliday =
        new HolidayData(
            "non-recurring-1",
            "Special Event 2024",
            LocalDate.of(2024, 6, 15),
            Optional.empty(),
            new Location("Brazil", Optional.empty(), Optional.empty()),
            HolidayType.NATIONAL,
            false, // not recurring
            Optional.of("One-time special event"),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    int targetYear = 2024;

    // When
    Optional<HolidayData> result =
        yearCalculatorService.calculateHolidayForYear(nonRecurringHoliday, targetYear);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get().name()).isEqualTo("Special Event 2024");
    assertThat(result.get().date().getYear()).isEqualTo(2024);
  }

  @Test
  @DisplayName("Should calculate holidays for year")
  void shouldCalculateHolidaysForYear() {
    // Given
    int targetYear = 2025;
    List<HolidayData> baseHolidays = List.of(baseHoliday);

    when(holidayService.findAll()).thenReturn(baseHolidays);

    // When
    List<HolidayData> result = yearCalculatorService.calculateHolidaysForYear(targetYear);

    // Then
    assertThat(result).hasSize(1);
    HolidayData calculatedHoliday = result.get(0);
    assertThat(calculatedHoliday.date().getYear()).isEqualTo(targetYear);
    assertThat(calculatedHoliday.name()).isEqualTo("New Year's Day");
  }

  @Test
  @DisplayName("Should calculate holidays for year and location")
  void shouldCalculateHolidaysForYearAndLocation() {
    // Given
    int targetYear = 2025;
    String country = "Brazil";
    String state = "SP";
    String city = "São Paulo";

    List<HolidayData> locationHolidays = List.of(baseHoliday);
    when(holidayService.findAllWithFilters(country, state, city, null, null, null, null, null))
        .thenReturn(locationHolidays);

    // When
    List<HolidayData> result =
        yearCalculatorService.calculateHolidaysForYearAndLocation(targetYear, country, state, city);

    // Then
    assertThat(result).hasSize(1);
    HolidayData calculatedHoliday = result.get(0);
    assertThat(calculatedHoliday.date().getYear()).isEqualTo(targetYear);
    assertThat(calculatedHoliday.location().country()).isEqualTo("Brazil");
  }

  @Test
  @DisplayName("Should calculate holidays for year and type")
  void shouldCalculateHolidaysForYearAndType() {
    // Given
    int targetYear = 2025;
    HolidayType type = HolidayType.NATIONAL;

    List<HolidayData> typeHolidays = List.of(baseHoliday);
    when(holidayService.findAllWithFilters(null, null, null, type, null, null, null, null))
        .thenReturn(typeHolidays);

    // When
    List<HolidayData> result =
        yearCalculatorService.calculateHolidaysForYearAndType(targetYear, type);

    // Then
    assertThat(result).hasSize(1);
    HolidayData calculatedHoliday = result.get(0);
    assertThat(calculatedHoliday.date().getYear()).isEqualTo(targetYear);
    assertThat(calculatedHoliday.type()).isEqualTo(HolidayType.NATIONAL);
  }

  @Test
  @DisplayName("Should check if holiday exists for year")
  void shouldCheckIfHolidayExistsForYear() {
    // Given
    String baseHolidayId = "test-holiday-1";
    int year = 2025;
    String yearSpecificId = "test-holiday-1_2025";

    when(holidayService.findById(yearSpecificId)).thenReturn(Optional.of(baseHoliday));

    // When
    boolean exists = yearCalculatorService.holidayExistsForYear(baseHolidayId, year);

    // Then
    assertThat(exists).isTrue();
  }

  @Test
  @DisplayName("Should calculate and persist holiday for year")
  void shouldCalculateAndPersistHolidayForYear() {
    // Given
    String baseHolidayId = "test-holiday-1";
    int year = 2025;
    String yearSpecificId = "test-holiday-1_2025";

    // Holiday doesn't exist yet
    when(holidayService.findById(yearSpecificId)).thenReturn(Optional.empty());
    // Base holiday exists
    when(holidayService.findById(baseHolidayId)).thenReturn(Optional.of(baseHoliday));
    // Persist succeeds
    when(holidayService.create(any(HolidayData.class))).thenReturn(baseHoliday);

    // When
    Optional<HolidayData> result =
        yearCalculatorService.calculateAndPersistHolidayForYear(baseHolidayId, year);

    // Then
    assertThat(result).isPresent();
  }

  @Test
  @DisplayName("Should validate year range")
  void shouldValidateYearRange() {
    // Given & When & Then
    assertThat(yearCalculatorService.isValidYear(2024)).isTrue();
    assertThat(yearCalculatorService.isValidYear(2050)).isTrue();
    assertThat(yearCalculatorService.isValidYear(1950)).isTrue();
    assertThat(yearCalculatorService.isValidYear(1900)).isFalse(); // Too far in past
    assertThat(yearCalculatorService.isValidYear(2100)).isFalse(); // Too far in future
  }

  @Test
  @DisplayName("Should handle leap year dates correctly")
  void shouldHandleLeapYearDatesCorrectly() {
    // Given - Holiday on Feb 29 (leap year)
    HolidayData leapYearHoliday =
        new HolidayData(
            "leap-day",
            "Leap Day",
            LocalDate.of(2024, 2, 29), // 2024 is a leap year
            Optional.empty(),
            new Location("Brazil", Optional.empty(), Optional.empty()),
            HolidayType.NATIONAL,
            true,
            Optional.of("Leap day celebration"),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    int nonLeapYear = 2025;

    // When
    Optional<HolidayData> result =
        yearCalculatorService.calculateHolidayForYear(leapYearHoliday, nonLeapYear);

    // Then
    assertThat(result).isPresent();
    HolidayData calculatedHoliday = result.get();
    // Should be moved to Feb 28 in non-leap year
    assertThat(calculatedHoliday.date()).isEqualTo(LocalDate.of(2025, 2, 28));
  }
}
