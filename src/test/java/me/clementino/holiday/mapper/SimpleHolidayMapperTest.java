package me.clementino.holiday.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayData;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.domain.Location;
import me.clementino.holiday.dto.HolidayResponseDTO;
import me.clementino.holiday.dto.LocationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("SimpleHolidayMapper Tests")
class SimpleHolidayMapperTest {

  private SimpleHolidayMapper mapper;

  @BeforeEach
  void setUp() {
    mapper = new SimpleHolidayMapper();
  }

  @Test
  @DisplayName("Should map HolidayData to HolidayResponseDTO with all fields")
  void shouldMapHolidayDataToHolidayResponseDTOWithAllFields() {
    // Given
    LocalDate holidayDate = LocalDate.of(2024, 12, 25); // Wednesday
    LocalDate observedDate = LocalDate.of(2024, 12, 26); // Thursday
    LocalDateTime createdDate = LocalDateTime.of(2024, 1, 15, 10, 30);
    LocalDateTime updatedDate = LocalDateTime.of(2024, 1, 16, 14, 45);

    HolidayData holidayData =
        new HolidayData(
            "holiday-123",
            "Christmas Day",
            holidayDate,
            Optional.of(observedDate),
            new Location("Brazil", Optional.of("São Paulo"), Optional.of("São Paulo")),
            HolidayType.NATIONAL,
            true,
            Optional.of("Christian holiday celebrating the birth of Jesus Christ"),
            Optional.of(createdDate),
            Optional.of(updatedDate),
            Optional.of(1));

    // When
    HolidayResponseDTO response = mapper.toResponse(holidayData);

    // Then
    assertThat(response.id()).isEqualTo("holiday-123");
    assertThat(response.name()).isEqualTo("Christmas Day");

    // Verify 'when' information
    assertThat(response.when()).isNotNull();
    assertThat(response.when().date()).isEqualTo(holidayDate);
    assertThat(response.when().weekday()).isEqualTo(DayOfWeek.WEDNESDAY);

    // Verify 'observed' information
    assertThat(response.observed()).isNotNull();
    assertThat(response.observed().date()).isEqualTo(observedDate);
    assertThat(response.observed().weekday()).isEqualTo(DayOfWeek.THURSDAY);

    // Verify 'where' information
    assertThat(response.where()).hasSize(1);
    LocationInfo location = response.where().get(0);
    assertThat(location.country()).isEqualTo("BR"); // Now returns ISO code
    assertThat(location.subdivision()).isEqualTo("São Paulo");
    assertThat(location.city()).isEqualTo("São Paulo");

    // Verify other fields
    assertThat(response.type()).isEqualTo(HolidayType.NATIONAL);
    assertThat(response.description())
        .isEqualTo("Christian holiday celebrating the birth of Jesus Christ");
    assertThat(response.created()).isEqualTo("2024-01-15T10:30");
    assertThat(response.updated()).isEqualTo("2024-01-16T14:45");
  }

  @Test
  @DisplayName("Should map HolidayData to HolidayResponseDTO without observed date")
  void shouldMapHolidayDataToHolidayResponseDTOWithoutObservedDate() {
    // Given
    LocalDate holidayDate = LocalDate.of(2024, 1, 1); // Monday

    HolidayData holidayData =
        new HolidayData(
            "holiday-456",
            "New Year's Day",
            holidayDate,
            Optional.empty(), // no observed date
            new Location("Brazil", Optional.empty(), Optional.empty()),
            HolidayType.NATIONAL,
            true,
            Optional.of("New Year celebration"),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    // When
    HolidayResponseDTO response = mapper.toResponse(holidayData);

    // Then
    assertThat(response.id()).isEqualTo("holiday-456");
    assertThat(response.name()).isEqualTo("New Year's Day");

    // Verify 'when' information
    assertThat(response.when()).isNotNull();
    assertThat(response.when().date()).isEqualTo(holidayDate);
    assertThat(response.when().weekday()).isEqualTo(DayOfWeek.MONDAY);

    // Verify 'observed' is null
    assertThat(response.observed()).isNull();

    // Verify 'where' information (country only)
    assertThat(response.where()).hasSize(1);
    LocationInfo location = response.where().get(0);
    assertThat(location.country()).isEqualTo("BR"); // Now returns ISO code
    assertThat(location.subdivision()).isNull();
    assertThat(location.city()).isNull();
  }

  @Test
  @DisplayName("Should map HolidayData with minimal fields")
  void shouldMapHolidayDataWithMinimalFields() {
    // Given
    LocalDate holidayDate = LocalDate.of(2024, 6, 15); // Saturday

    HolidayData holidayData =
        new HolidayData(
            "holiday-789",
            "Simple Holiday",
            holidayDate,
            Optional.empty(),
            new Location("USA", Optional.empty(), Optional.empty()),
            HolidayType.COMMERCIAL,
            false,
            Optional.empty(), // no description
            Optional.empty(), // no creation date
            Optional.empty(), // no update date
            Optional.empty() // no version
            );

    // When
    HolidayResponseDTO response = mapper.toResponse(holidayData);

    // Then
    assertThat(response.id()).isEqualTo("holiday-789");
    assertThat(response.name()).isEqualTo("Simple Holiday");
    assertThat(response.when().date()).isEqualTo(holidayDate);
    assertThat(response.when().weekday()).isEqualTo(DayOfWeek.SATURDAY);
    assertThat(response.observed()).isNull();
    assertThat(response.where()).hasSize(1);
    assertThat(response.where().get(0).country()).isEqualTo("USA");
    assertThat(response.type()).isEqualTo(HolidayType.COMMERCIAL);
    assertThat(response.description()).isNull();
    assertThat(response.created()).isNull();
    assertThat(response.updated()).isNull();
  }

  @Test
  @DisplayName("Should map HolidayData with state but no city")
  void shouldMapHolidayDataWithStateButNoCity() {
    // Given
    LocalDate holidayDate = LocalDate.of(2024, 4, 21); // Sunday

    HolidayData holidayData =
        new HolidayData(
            "holiday-state",
            "State Holiday",
            holidayDate,
            Optional.empty(),
            new Location("Brazil", Optional.of("Rio de Janeiro"), Optional.empty()),
            HolidayType.STATE,
            true,
            Optional.of("State-specific holiday"),
            Optional.empty(),
            Optional.empty(),
            Optional.empty());

    // When
    HolidayResponseDTO response = mapper.toResponse(holidayData);

    // Then
    assertThat(response.where()).hasSize(1);
    LocationInfo location = response.where().get(0);
    assertThat(location.country()).isEqualTo("BR"); // Now returns ISO code
    assertThat(location.subdivision()).isEqualTo("Rio de Janeiro");
    assertThat(location.city()).isNull();
    assertThat(response.type()).isEqualTo(HolidayType.STATE);
  }

  @Test
  @DisplayName("Should handle different weekdays correctly")
  void shouldHandleDifferentWeekdaysCorrectly() {
    // Test different days of the week
    LocalDate[] dates = {
      LocalDate.of(2024, 1, 1), // Monday
      LocalDate.of(2024, 1, 2), // Tuesday
      LocalDate.of(2024, 1, 3), // Wednesday
      LocalDate.of(2024, 1, 4), // Thursday
      LocalDate.of(2024, 1, 5), // Friday
      LocalDate.of(2024, 1, 6), // Saturday
      LocalDate.of(2024, 1, 7) // Sunday
    };

    DayOfWeek[] expectedWeekdays = {
      DayOfWeek.MONDAY,
      DayOfWeek.TUESDAY,
      DayOfWeek.WEDNESDAY,
      DayOfWeek.THURSDAY,
      DayOfWeek.FRIDAY,
      DayOfWeek.SATURDAY,
      DayOfWeek.SUNDAY
    };

    for (int i = 0; i < dates.length; i++) {
      // Given
      HolidayData holidayData =
          new HolidayData(
              "holiday-" + i,
              "Test Holiday " + i,
              dates[i],
              Optional.empty(),
              new Location("Test", Optional.empty(), Optional.empty()),
              HolidayType.NATIONAL,
              true,
              Optional.empty(),
              Optional.empty(),
              Optional.empty(),
              Optional.empty());

      // When
      HolidayResponseDTO response = mapper.toResponse(holidayData);

      // Then
      assertThat(response.when().weekday()).isEqualTo(expectedWeekdays[i]);
    }
  }
}
