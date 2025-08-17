package me.clementino.holiday.domain;

/**
 * Enum representing different types of holidays. Following DOP principle: Make illegal states
 * unrepresentable.
 */
public enum HolidayType {
  NATIONAL,
  STATE,
  MUNICIPAL,
  RELIGIOUS,
  COMMERCIAL;

  /**
   * Checks if this holiday type is governmental (official).
   *
   * @return true if the holiday is governmental
   */
  public boolean isGovernmental() {
    return this == NATIONAL || this == STATE || this == MUNICIPAL;
  }
}
