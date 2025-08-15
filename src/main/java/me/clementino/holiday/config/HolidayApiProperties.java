package me.clementino.holiday.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Holiday API. Binds properties from application.yml under the
 * 'holiday-api' prefix.
 */
@ConfigurationProperties(prefix = "holiday-api")
public record HolidayApiProperties(
    String version,
    String description,
    Java24Properties java24,
    DataOrientedProgrammingProperties dataOrientedProgramming,
    MapStructProperties mapstruct,
    HolidayCalculationProperties holidayCalculation,
    ValidationProperties validation) {

  public record Java24Properties(
      boolean enabled, boolean previewFeatures, boolean virtualThreads, String patternMatching) {}

  public record DataOrientedProgrammingProperties(
      boolean records, boolean sealedInterfaces, boolean immutableData, boolean pureFunctions) {}

  public record MapStructProperties(
      String defaultComponentModel,
      String unmappedTargetPolicy,
      boolean suppressTimestampInGenerated) {}

  public record HolidayCalculationProperties(
      boolean cacheEnabled,
      boolean autoCalculateMissingYears,
      int maxYearsAhead,
      int maxYearsBehind) {}

  public record ValidationProperties(
      boolean failFast, boolean detailedErrors, boolean validateNested) {}
}
