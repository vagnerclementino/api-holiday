package me.clementino.holiday.domain.dop;

/**
 * Enum representing well-known holidays with standardized names. This ensures consistency across
 * the application and prevents typos in holiday names.
 *
 * <p>DOP Principle: Make Illegal States Unrepresentable - by using an enum, we prevent invalid
 * holiday names and ensure type safety.
 */
public enum KnownHoliday {
  // Fixed holidays
  NEW_YEAR("New Year's Day", "First day of the Gregorian calendar year"),
  CHRISTMAS("Christmas Day", "Christian celebration of the birth of Jesus Christ"),
  INDEPENDENCE_DAY_US("Independence Day", "United States independence celebration"),
  INDEPENDENCE_DAY_BRAZIL("Independence Day", "Brazil's independence from Portugal"),

  // Moveable holidays (calculated)
  EASTER("Easter Sunday", "Christian celebration of the resurrection of Jesus Christ"),
  GOOD_FRIDAY("Good Friday", "Christian observance of the crucifixion of Jesus Christ"),
  EASTER_MONDAY("Easter Monday", "Christian holiday following Easter Sunday"),
  PALM_SUNDAY("Palm Sunday", "Christian holiday commemorating Jesus' entry into Jerusalem"),

  // Weekday-based holidays
  THANKSGIVING_US("Thanksgiving Day", "United States harvest celebration"),
  MEMORIAL_DAY_US("Memorial Day", "United States day of remembrance"),
  LABOR_DAY_US("Labor Day", "United States workers' celebration"),
  MOTHERS_DAY("Mother's Day", "Celebration honoring mothers"),
  FATHERS_DAY("Father's Day", "Celebration honoring fathers"),

  // International holidays
  LABOR_DAY_INTERNATIONAL("International Workers' Day", "International celebration of workers"),
  WOMENS_DAY("International Women's Day", "Celebration of women's rights and achievements"),
  VALENTINES_DAY("Valentine's Day", "Celebration of romantic love"),

  // Religious holidays
  EPIPHANY("Epiphany", "Christian celebration of the revelation of God incarnate"),
  ALL_SAINTS_DAY("All Saints' Day", "Christian celebration honoring all saints"),
  ALL_SOULS_DAY("All Souls' Day", "Christian day of prayer for the souls of the dead"),

  // Cultural holidays
  HALLOWEEN("Halloween", "Traditional celebration with costumes and trick-or-treating"),
  ST_PATRICKS_DAY("St. Patrick's Day", "Cultural celebration of Irish heritage"),
  CINCO_DE_MAYO("Cinco de Mayo", "Mexican celebration of victory over French forces");

  private final String displayName;
  private final String description;

  KnownHoliday(String displayName, String description) {
    this.displayName = displayName;
    this.description = description;
  }

  /** Returns the standardized display name for this holiday. */
  public String getDisplayName() {
    return displayName;
  }

  /** Returns a description of this holiday. */
  public String getDescription() {
    return description;
  }

  /** Checks if this is a fixed holiday (occurs on the same date every year). */
  public boolean isFixed() {
    return switch (this) {
      case NEW_YEAR,
              CHRISTMAS,
              INDEPENDENCE_DAY_US,
              INDEPENDENCE_DAY_BRAZIL,
              LABOR_DAY_INTERNATIONAL,
              WOMENS_DAY,
              VALENTINES_DAY,
              EPIPHANY,
              ALL_SAINTS_DAY,
              ALL_SOULS_DAY,
              HALLOWEEN,
              ST_PATRICKS_DAY,
              CINCO_DE_MAYO ->
          true;
      default -> false;
    };
  }

  /** Checks if this is a moveable holiday (date calculated based on rules). */
  public boolean isMoveable() {
    return !isFixed();
  }

  /** Checks if this holiday is derived from another holiday (like Good Friday from Easter). */
  public boolean isDerived() {
    return switch (this) {
      case GOOD_FRIDAY, EASTER_MONDAY, PALM_SUNDAY -> true;
      default -> false;
    };
  }

  /** Returns the base holiday for derived holidays. */
  public KnownHoliday getBaseHoliday() {
    return switch (this) {
      case GOOD_FRIDAY, EASTER_MONDAY, PALM_SUNDAY -> EASTER;
      default ->
          throw new IllegalStateException(
              "Holiday " + this + " is not derived from another holiday");
    };
  }

  /** Returns the day offset from the base holiday for derived holidays. */
  public int getDayOffset() {
    return switch (this) {
      case GOOD_FRIDAY -> -2; // 2 days before Easter
      case EASTER_MONDAY -> 1; // 1 day after Easter
      case PALM_SUNDAY -> -7; // 7 days before Easter (1 week)
      default ->
          throw new IllegalStateException(
              "Holiday " + this + " is not derived from another holiday");
    };
  }
}
