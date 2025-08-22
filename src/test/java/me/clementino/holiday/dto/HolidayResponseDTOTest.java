package me.clementino.holiday.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import me.clementino.holiday.domain.dop.HolidayType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@DisplayName("HolidayResponseDTO Tests")
@Tag("unit")
class HolidayResponseDTOTest {

  @Test
  @DisplayName("Should create valid HolidayResponseDTO")
  void shouldCreateValidHolidayResponseDTO() {
    // Given
    WhenInfo when = WhenInfo.from(LocalDate.of(2024, 12, 25));
    WhenInfo observed = WhenInfo.from(LocalDate.of(2024, 12, 26));
    LocationInfo location = new LocationInfo("BR", "SP", "São Paulo", "São Paulo, SP, Brazil");
    List<LocationInfo> where = List.of(location);

    // When
    HolidayResponseDTO response =
        new HolidayResponseDTO(
            "holiday-123",
            "Christmas Day",
            when,
            observed,
            where,
            HolidayType.NATIONAL,
            "Christian holiday",
            "2024-01-15T10:30:00",
            "2024-01-15T10:30:00");

    // Then
    assertThat(response.id()).isEqualTo("holiday-123");
    assertThat(response.name()).isEqualTo("Christmas Day");
    assertThat(response.when()).isEqualTo(when);
    assertThat(response.observed()).isEqualTo(observed);
    assertThat(response.where()).hasSize(1);
    assertThat(response.where().get(0)).isEqualTo(location);
    assertThat(response.type()).isEqualTo(HolidayType.NATIONAL);
    assertThat(response.description()).isEqualTo("Christian holiday");
  }

  @Test
  @DisplayName("Should create HolidayResponseDTO without observed date")
  void shouldCreateHolidayResponseDTOWithoutObservedDate() {
    // Given
    WhenInfo when = WhenInfo.from(LocalDate.of(2024, 1, 1));
    LocationInfo location = new LocationInfo("BR", null, null, "Brazil");
    List<LocationInfo> where = List.of(location);

    // When
    HolidayResponseDTO response =
        new HolidayResponseDTO(
            "holiday-456",
            "New Year's Day",
            when,
            null, // no observed date
            where,
            HolidayType.NATIONAL,
            "New Year celebration",
            "2024-01-15T10:30:00",
            "2024-01-15T10:30:00");

    // Then
    assertThat(response.observed()).isNull();
    assertThat(response.when()).isEqualTo(when);
  }

  @Test
  @DisplayName("Should create HolidayResponseDTO with multiple locations")
  void shouldCreateHolidayResponseDTOWithMultipleLocations() {
    // Given
    WhenInfo when = WhenInfo.from(LocalDate.of(2024, 9, 7));
    LocationInfo location1 = new LocationInfo("BR", "SP", "São Paulo", "São Paulo, SP, Brazil");
    LocationInfo location2 =
        new LocationInfo("BR", "RJ", "Rio de Janeiro", "Rio de Janeiro, RJ, Brazil");
    List<LocationInfo> where = List.of(location1, location2);

    // When
    HolidayResponseDTO response =
        new HolidayResponseDTO(
            "holiday-789",
            "Independence Day",
            when,
            null,
            where,
            HolidayType.NATIONAL,
            "Brazil Independence Day",
            "2024-01-15T10:30:00",
            "2024-01-15T10:30:00");

    // Then
    assertThat(response.where()).hasSize(2);
    assertThat(response.where()).contains(location1, location2);
  }

  @Test
  @DisplayName("Should create HolidayResponseDTO with minimal fields")
  void shouldCreateHolidayResponseDTOWithMinimalFields() {
    // Given
    WhenInfo when = WhenInfo.from(LocalDate.of(2024, 6, 15));
    LocationInfo location = new LocationInfo("US", null, null, "United States");
    List<LocationInfo> where = List.of(location);

    // When
    HolidayResponseDTO response =
        new HolidayResponseDTO(
            "holiday-minimal",
            "Simple Holiday",
            when,
            null, // no observed
            where,
            HolidayType.COMMERCIAL,
            null, // no description
            null, // no dateCreated
            null // no lastUpdated
            );

    // Then
    assertThat(response.id()).isEqualTo("holiday-minimal");
    assertThat(response.name()).isEqualTo("Simple Holiday");
    assertThat(response.when()).isEqualTo(when);
    assertThat(response.observed()).isNull();
    assertThat(response.where()).hasSize(1);
    assertThat(response.type()).isEqualTo(HolidayType.COMMERCIAL);
    assertThat(response.description()).isNull();
    assertThat(response.created()).isNull();
    assertThat(response.updated()).isNull();
  }

  @Test
  @DisplayName("Should throw exception when 'when' is null")
  void shouldThrowExceptionWhenWhenIsNull() {
    // Given
    LocationInfo location = new LocationInfo("BR", null, null, "Brazil");
    List<LocationInfo> where = List.of(location);

    // When & Then
    assertThatThrownBy(
            () ->
                new HolidayResponseDTO(
                    "holiday-123",
                    "Test Holiday",
                    null, // when is null
                    null,
                    where,
                    HolidayType.NATIONAL,
                    "Test description",
                    "2024-01-15T10:30:00",
                    "2024-01-15T10:30:00"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("'when' information is required");
  }

  @Test
  @DisplayName("Should throw exception when 'where' is null")
  void shouldThrowExceptionWhenWhereIsNull() {
    // Given
    WhenInfo when = WhenInfo.from(LocalDate.of(2024, 12, 25));

    // When & Then
    assertThatThrownBy(
            () ->
                new HolidayResponseDTO(
                    "holiday-123",
                    "Test Holiday",
                    when,
                    null,
                    null, // where is null
                    HolidayType.NATIONAL,
                    "Test description",
                    "2024-01-15T10:30:00",
                    "2024-01-15T10:30:00"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("At least one location must be specified");
  }

  @Test
  @DisplayName("Should throw exception when 'where' is empty")
  void shouldThrowExceptionWhenWhereIsEmpty() {
    // Given
    WhenInfo when = WhenInfo.from(LocalDate.of(2024, 12, 25));
    List<LocationInfo> where = List.of(); // empty list

    // When & Then
    assertThatThrownBy(
            () ->
                new HolidayResponseDTO(
                    "holiday-123",
                    "Test Holiday",
                    when,
                    null,
                    where,
                    HolidayType.NATIONAL,
                    "Test description",
                    "2024-01-15T10:30:00",
                    "2024-01-15T10:30:00"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("At least one location must be specified");
  }

  @Test
  @DisplayName("Should throw exception when location has no country")
  void shouldThrowExceptionWhenLocationHasNoCountry() {
    // Given
    WhenInfo when = WhenInfo.from(LocalDate.of(2024, 12, 25));
    LocationInfo invalidLocation =
        new LocationInfo(null, "SP", "São Paulo", "São Paulo, SP, null"); // no country
    List<LocationInfo> where = List.of(invalidLocation);

    // When & Then
    assertThatThrownBy(
            () ->
                new HolidayResponseDTO(
                    "holiday-123",
                    "Test Holiday",
                    when,
                    null,
                    where,
                    HolidayType.NATIONAL,
                    "Test description",
                    "2024-01-15T10:30:00",
                    "2024-01-15T10:30:00"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Each location must have a country");
  }

  @Test
  @DisplayName("Should throw exception when location has blank country")
  void shouldThrowExceptionWhenLocationHasBlankCountry() {
    // Given
    WhenInfo when = WhenInfo.from(LocalDate.of(2024, 12, 25));
    LocationInfo invalidLocation =
        new LocationInfo("  ", "SP", "São Paulo", "São Paulo, SP,   "); // blank country
    List<LocationInfo> where = List.of(invalidLocation);

    // When & Then
    assertThatThrownBy(
            () ->
                new HolidayResponseDTO(
                    "holiday-123",
                    "Test Holiday",
                    when,
                    null,
                    where,
                    HolidayType.NATIONAL,
                    "Test description",
                    "2024-01-15T10:30:00",
                    "2024-01-15T10:30:00"))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Each location must have a country");
  }
}
