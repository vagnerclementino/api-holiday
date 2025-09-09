package me.clementino.holiday.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.DayOfWeek;
import java.time.LocalDate;

/**
 * Information about when a holiday occurs, including date and weekday.
 *
 * <p>This record demonstrates DOP principles by being immutable and transparent, containing exactly
 * the data needed to represent when a holiday happens.
 */
@Schema(description = "Information about when a holiday occurs")
public record WhenInfoDTO(
    @Schema(description = "The date when the holiday occurs", example = "2024-12-25")
        LocalDate date,
    @Schema(description = "The day of the week when the holiday occurs", example = "WEDNESDAY")
        DayOfWeek weekday) {

  /**
   * Create WhenInfoDTO from a LocalDate.
   *
   * @param date the holiday date
   * @return WhenInfoDTO with date and calculated weekday
   */
  public static WhenInfoDTO from(LocalDate date) {
    return new WhenInfoDTO(date, date.getDayOfWeek());
  }
}
