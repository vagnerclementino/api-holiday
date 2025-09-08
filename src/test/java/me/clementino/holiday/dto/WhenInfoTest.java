package me.clementino.holiday.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@DisplayName("WhenInfo Tests")
@Tag("unit")
class WhenInfoTest {

  @Test
  @DisplayName("Should create WhenInfo from LocalDate")
  void shouldCreateWhenInfoFromLocalDate() {
    LocalDate date = LocalDate.of(2024, 12, 25);

    WhenInfo whenInfo = WhenInfo.from(date);

    assertThat(whenInfo.date()).isEqualTo(date);
    assertThat(whenInfo.weekday()).isEqualTo(DayOfWeek.WEDNESDAY);
  }

  @Test
  @DisplayName("Should handle different weekdays correctly")
  void shouldHandleDifferentWeekdaysCorrectly() {
    LocalDate monday = LocalDate.of(2024, 1, 1);
    LocalDate sunday = LocalDate.of(2024, 1, 7);

    WhenInfo mondayInfo = WhenInfo.from(monday);
    WhenInfo sundayInfo = WhenInfo.from(sunday);

    assertThat(mondayInfo.weekday()).isEqualTo(DayOfWeek.MONDAY);
    assertThat(sundayInfo.weekday()).isEqualTo(DayOfWeek.SUNDAY);
  }

  @Test
  @DisplayName("Should be immutable record")
  void shouldBeImmutableRecord() {
    LocalDate date = LocalDate.of(2024, 6, 15);
    WhenInfo whenInfo = WhenInfo.from(date);

    assertThat(whenInfo).isNotNull();
    assertThat(whenInfo.date()).isEqualTo(date);
    assertThat(whenInfo.weekday()).isEqualTo(DayOfWeek.SATURDAY);

    assertThat(whenInfo.toString()).contains("WhenInfo");
    assertThat(whenInfo.toString()).contains("2024-06-15");
    assertThat(whenInfo.toString()).contains("SATURDAY");
  }
}
