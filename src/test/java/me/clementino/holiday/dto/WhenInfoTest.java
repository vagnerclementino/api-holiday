package me.clementino.holiday.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("WhenInfo Tests")
class WhenInfoTest {

  @Test
  @DisplayName("Should create WhenInfo from LocalDate")
  void shouldCreateWhenInfoFromLocalDate() {
    // Given
    LocalDate date = LocalDate.of(2024, 12, 25); // Christmas 2024 (Wednesday)

    // When
    WhenInfo whenInfo = WhenInfo.from(date);

    // Then
    assertThat(whenInfo.date()).isEqualTo(date);
    assertThat(whenInfo.weekday()).isEqualTo(DayOfWeek.WEDNESDAY);
  }

  @Test
  @DisplayName("Should handle different weekdays correctly")
  void shouldHandleDifferentWeekdaysCorrectly() {
    // Given
    LocalDate monday = LocalDate.of(2024, 1, 1); // New Year 2024 (Monday)
    LocalDate sunday = LocalDate.of(2024, 1, 7); // Sunday

    // When
    WhenInfo mondayInfo = WhenInfo.from(monday);
    WhenInfo sundayInfo = WhenInfo.from(sunday);

    // Then
    assertThat(mondayInfo.weekday()).isEqualTo(DayOfWeek.MONDAY);
    assertThat(sundayInfo.weekday()).isEqualTo(DayOfWeek.SUNDAY);
  }

  @Test
  @DisplayName("Should be immutable record")
  void shouldBeImmutableRecord() {
    // Given
    LocalDate date = LocalDate.of(2024, 6, 15);
    WhenInfo whenInfo = WhenInfo.from(date);

    // When & Then
    assertThat(whenInfo).isNotNull();
    assertThat(whenInfo.date()).isEqualTo(date);
    assertThat(whenInfo.weekday()).isEqualTo(DayOfWeek.SATURDAY);

    // Verify it's a proper record
    assertThat(whenInfo.toString()).contains("WhenInfo");
    assertThat(whenInfo.toString()).contains("2024-06-15");
    assertThat(whenInfo.toString()).contains("SATURDAY");
  }
}
