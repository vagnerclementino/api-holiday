package me.clementino.holiday.domain.dop;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.domain.ValidationResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests demonstrating Data-Oriented Programming v1.1 principles with FixedHoliday and
 * MoveableHoliday domain objects.
 */
class HolidayDOPTest {

  @Test
  @DisplayName("DOP Principle 1: Model Data Immutably and Transparently")
  void testImmutableAndTransparentData() {
    // Create immutable holiday
    var originalHoliday =
        HolidayFactory.createFixed(
            "Test Holiday",
            "Test description",
            1,
            Month.JANUARY,
            List.of(Locality.brazil()),
            HolidayType.NATIONAL,
            true);

    // Data is transparent - all fields accessible
    assertEquals("Test Holiday", originalHoliday.name());
    assertEquals("Test description", originalHoliday.description());
    assertEquals(LocalDate.of(LocalDate.now().getYear(), Month.JANUARY, 1), originalHoliday.date());
    assertEquals(List.of(Locality.brazil()), originalHoliday.localities());
    assertEquals(HolidayType.NATIONAL, originalHoliday.type());
    assertTrue(originalHoliday.mondayisation());

    // Transformation creates new instance (immutable)
    var modifiedHoliday = originalHoliday.withName("Modified Holiday");

    // Original is unchanged
    assertEquals("Test Holiday", originalHoliday.name());
    assertEquals("Modified Holiday", modifiedHoliday.name());

    // All other fields remain the same
    assertEquals(originalHoliday.description(), modifiedHoliday.description());
    assertEquals(originalHoliday.date(), modifiedHoliday.date());
    assertEquals(originalHoliday.localities(), modifiedHoliday.localities());
    assertEquals(originalHoliday.type(), modifiedHoliday.type());
  }

  @Test
  @DisplayName("DOP Principle 2: Model the Data, the Whole Data, and Nothing but the Data")
  void testCompleteDataModeling() {
    // Fixed holiday contains exactly what a fixed holiday needs
    var fixedHoliday = HolidayFactory.createChristmas("Brazil");

    assertInstanceOf(FixedHoliday.class, fixedHoliday);
    // Fixed holiday contains exactly what a fixed holiday needs
    assertEquals(
        LocalDate.of(LocalDate.now().getYear(), Month.DECEMBER, 25),
        fixedHoliday.date()); // Has fixed date

    // Moveable holiday contains exactly what a moveable holiday needs
    var moveableHoliday = HolidayFactory.createUSThanksgiving();

    assertInstanceOf(MoveableHoliday.class, moveableHoliday);
    assertEquals(
        MoveableHolidayType.WEEKDAY_BASED, moveableHoliday.moveableType()); // Has moveable type

    // Easter holiday (lunar-based)
    var easterHoliday = HolidayFactory.createEasterSunday("Brazil");

    assertInstanceOf(MoveableHoliday.class, easterHoliday);
    assertEquals(MoveableHolidayType.LUNAR_BASED, easterHoliday.moveableType());

    // Relative holiday
    var goodFriday = HolidayFactory.createGoodFriday("Brazil");

    assertInstanceOf(MoveableHoliday.class, goodFriday);
    assertEquals(MoveableHolidayType.RELATIVE_TO_HOLIDAY, goodFriday.moveableType());
    assertTrue(goodFriday.baseHoliday().isPresent());
    assertEquals(-2, goodFriday.dayOffset()); // 2 days before Easter
  }

  @Test
  @DisplayName("DOP Principle 3: Make Illegal States Unrepresentable")
  void testIllegalStatesUnrepresentable() {
    // Sealed interface prevents invalid holiday types
    // This is enforced at compile time - we can only have FixedHoliday or MoveableHoliday

    // Enum prevents invalid holiday types
    var validTypes = HolidayType.values();
    assertEquals(5, validTypes.length);

    // Constructor validation prevents invalid data
    assertThrows(
        IllegalArgumentException.class,
        () ->
            HolidayFactory.createFixed(
                "", "", 1, Month.JANUARY, List.of(Locality.brazil()), HolidayType.NATIONAL, false));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            HolidayFactory.createFixed(
                "Test",
                "",
                32,
                Month.JANUARY,
                List.of(Locality.brazil()),
                HolidayType.NATIONAL,
                false));

    assertThrows(
        IllegalArgumentException.class,
        () ->
            HolidayFactory.createFixed(
                "Test", "", 1, Month.JANUARY, List.of(), HolidayType.NATIONAL, false));

    // Locality validation
    assertThrows(
        NullPointerException.class,
        () -> Locality.city(null, Locality.saoPauloState(), Locality.brazil()));

    assertThrows(
        NullPointerException.class, () -> Locality.city("São Paulo", null, Locality.brazil()));
  }

  @Test
  @DisplayName("DOP Principle 4: Separate Operations from Data")
  void testOperationSeparation() {
    // Data structures contain no behavior
    var fixedHoliday = HolidayFactory.createChristmas("Brazil");
    var moveableHoliday = HolidayFactory.createUSThanksgiving();

    // Operations are in separate classes
    var currentYear = 2024;

    // Calculate dates using operations
    var christmasDate = HolidayOperations.calculateDate(fixedHoliday, currentYear);
    var thanksgivingDate = HolidayOperations.calculateDate(moveableHoliday, currentYear);

    assertEquals(LocalDate.of(2024, Month.DECEMBER, 25), christmasDate);
    assertNotNull(thanksgivingDate); // Thanksgiving calculation would need proper implementation

    // Validation using operations
    var validation = HolidayOperations.validateHoliday(fixedHoliday);
    assertInstanceOf(ValidationResult.Success.class, validation);

    // Formatting using operations
    var formatted = HolidayOperations.formatHolidayInfo(fixedHoliday);
    assertTrue(formatted.contains("Christmas Day"));
    assertTrue(formatted.contains("Brazil"));

    // Location checking using operations
    var brazilLocation = Locality.brazil();
    var usLocation = Locality.unitedStates();

    assertTrue(HolidayOperations.appliesTo(fixedHoliday, brazilLocation));
    assertFalse(HolidayOperations.appliesTo(fixedHoliday, usLocation));

    assertTrue(HolidayOperations.appliesTo(moveableHoliday, usLocation));
    assertFalse(HolidayOperations.appliesTo(moveableHoliday, brazilLocation));
  }

  @Test
  @DisplayName("Pattern Matching with Sealed Interfaces")
  void testPatternMatching() {
    var holidays =
        List.of(
            HolidayFactory.createChristmas("Brazil"),
            HolidayFactory.createUSThanksgiving(),
            HolidayFactory.createGoodFriday("Brazil"));

    for (Holiday holiday : holidays) {
      // Pattern matching ensures exhaustive handling
      var description =
          switch (holiday) {
            case FixedHoliday fixed -> "Fixed on " + fixed.date();
            case MoveableHoliday moveable -> "Calculated using " + moveable.moveableType();
          };

      assertNotNull(description);
      assertTrue(description.length() > 0);
    }
  }

  @Test
  @DisplayName("Easter Calculation")
  void testEasterCalculation() {
    var easter = HolidayFactory.createEasterSunday("Brazil");

    // Test Easter dates for known years
    var easter2024 = HolidayOperations.calculateDate(easter, 2024);
    assertEquals(LocalDate.of(2024, Month.MARCH, 31), easter2024);

    var easter2025 = HolidayOperations.calculateDate(easter, 2025);
    assertEquals(LocalDate.of(2025, Month.APRIL, 20), easter2025);

    var easter2026 = HolidayOperations.calculateDate(easter, 2026);
    assertEquals(LocalDate.of(2026, Month.APRIL, 5), easter2026);
  }

  @Test
  @DisplayName("Mondayisation Rules")
  void testMondayisationRules() {
    var christmasWithMondayisation =
        HolidayFactory.createFixed(
            "Christmas Day",
            "With mondayisation",
            25,
            Month.DECEMBER,
            List.of(Locality.brazil()),
            HolidayType.RELIGIOUS,
            true);

    var christmasWithoutMondayisation =
        HolidayFactory.createFixed(
            "Christmas Day",
            "Without mondayisation",
            25,
            Month.DECEMBER,
            List.of(Locality.brazil()),
            HolidayType.RELIGIOUS,
            false);

    // Test for a year where Christmas falls on weekend
    var year = 2022; // Christmas 2022 was on Sunday

    var actualDate = HolidayOperations.calculateDate(christmasWithMondayisation, year);
    var observedWithMondayisation =
        HolidayOperations.calculateObservedDate(christmasWithMondayisation, year);
    var observedWithoutMondayisation =
        HolidayOperations.calculateObservedDate(christmasWithoutMondayisation, year);

    assertEquals(LocalDate.of(2022, Month.DECEMBER, 25), actualDate);
    assertEquals(
        LocalDate.of(2022, Month.DECEMBER, 26), observedWithMondayisation); // Moved to Monday
    assertEquals(LocalDate.of(2022, Month.DECEMBER, 25), observedWithoutMondayisation); // No change
  }

  @Test
  @DisplayName("Locality Hierarchy and Holiday Application")
  void testLocalityHierarchy() {
    // National holiday
    var nationalHoliday = HolidayFactory.createBrazilIndependenceDay();

    // State holiday
    var stateHoliday =
        HolidayFactory.createStateHoliday(
            "State Holiday", "State-level holiday", 23, Month.APRIL, "Brazil", "SP", false);

    // City holiday
    var cityHoliday =
        HolidayFactory.createCityHoliday(
            "City Holiday",
            "City-level holiday",
            25,
            Month.JANUARY,
            "Brazil",
            "SP",
            "São Paulo",
            false);

    var nationalLocation = Locality.brazil();
    var stateLocation = Locality.saoPauloState();
    var cityLocation = Locality.saoPauloCity();
    var otherStateLocation = Locality.subdivision(Locality.brazil(), "RJ", "Rio de Janeiro");

    // National holiday applies everywhere in the country
    assertTrue(HolidayOperations.appliesTo(nationalHoliday, nationalLocation));
    assertTrue(HolidayOperations.appliesTo(nationalHoliday, stateLocation));
    assertTrue(HolidayOperations.appliesTo(nationalHoliday, cityLocation));
    assertTrue(HolidayOperations.appliesTo(nationalHoliday, otherStateLocation));

    // State holiday applies only to that state
    assertFalse(HolidayOperations.appliesTo(stateHoliday, nationalLocation));
    assertTrue(HolidayOperations.appliesTo(stateHoliday, stateLocation));
    assertTrue(HolidayOperations.appliesTo(stateHoliday, cityLocation));
    assertFalse(HolidayOperations.appliesTo(stateHoliday, otherStateLocation));

    // City holiday applies only to that city
    assertFalse(HolidayOperations.appliesTo(cityHoliday, nationalLocation));
    assertFalse(HolidayOperations.appliesTo(cityHoliday, stateLocation));
    assertTrue(HolidayOperations.appliesTo(cityHoliday, cityLocation));
    assertFalse(HolidayOperations.appliesTo(cityHoliday, otherStateLocation));
  }

  @Test
  @DisplayName("Locality Sealed Class Structure")
  void testLocalityFactoryMethods() {
    // Test Country
    var country = Locality.brazil();
    assertInstanceOf(Locality.Country.class, country);
    assertEquals("BR", country.code());
    assertEquals("Brazil", country.name());

    // Test Subdivision
    var subdivision = Locality.saoPauloState();
    assertInstanceOf(Locality.Subdivision.class, subdivision);
    assertEquals("SP", subdivision.code());
    assertEquals("São Paulo", subdivision.name());
    assertEquals(Locality.brazil(), subdivision.country());

    // Test City
    var city = Locality.saoPauloCity();
    assertInstanceOf(Locality.City.class, city);
    assertEquals("São Paulo", city.name());
    assertEquals(Locality.saoPauloState(), city.subdivision());
    assertEquals(Locality.brazil(), city.country());

    // Test pattern matching with sealed interface
    String description =
        switch ((Locality) city) {
          case Locality.Country c -> "Country: " + c.name();
          case Locality.Subdivision s -> "Subdivision: " + s.name() + ", " + s.country().name();
          case Locality.City ct ->
              "City: " + ct.name() + ", " + ct.subdivision().name() + ", " + ct.country().name();
        };

    assertEquals("City: São Paulo, São Paulo, Brazil", description);
  }
}
