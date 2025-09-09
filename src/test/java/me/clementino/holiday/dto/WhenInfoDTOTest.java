package me.clementino.holiday.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@DisplayName("WhenInfoDTO Tests")
@Tag("unit")
class WhenInfoDTOTest {

  @Test
  @DisplayName("Should create WhenInfoDTO from LocalDate")
  void shouldCreateWhenInfoFromLocalDate() {
    LocalDate date = LocalDate.of(2024, 12, 25);

    WhenInfoDTO whenInfoDTO = WhenInfoDTO.from(date);

    assertThat(whenInfoDTO.date()).isEqualTo(date);
    assertThat(whenInfoDTO.weekday()).isEqualTo(DayOfWeek.WEDNESDAY);
  }

  @Test
  @DisplayName("Should handle different weekdays correctly")
  void shouldHandleDifferentWeekdaysCorrectly() {
    LocalDate monday = LocalDate.of(2024, 1, 1);
    LocalDate sunday = LocalDate.of(2024, 1, 7);

    WhenInfoDTO mondayInfo = WhenInfoDTO.from(monday);
    WhenInfoDTO sundayInfo = WhenInfoDTO.from(sunday);

    assertThat(mondayInfo.weekday()).isEqualTo(DayOfWeek.MONDAY);
    assertThat(sundayInfo.weekday()).isEqualTo(DayOfWeek.SUNDAY);
  }

  @Test
  @DisplayName("Should be immutable record")
  void shouldBeImmutableRecord() {
    LocalDate date = LocalDate.of(2024, 6, 15);
    WhenInfoDTO whenInfoDTO = WhenInfoDTO.from(date);

    assertThat(whenInfoDTO).isNotNull();
    assertThat(whenInfoDTO.date()).isEqualTo(date);
    assertThat(whenInfoDTO.weekday()).isEqualTo(DayOfWeek.SATURDAY);

    assertThat(whenInfoDTO.toString()).contains("WhenInfoDTO");
    assertThat(whenInfoDTO.toString()).contains("2024-06-15");
    assertThat(whenInfoDTO.toString()).contains("SATURDAY");
  }
}
