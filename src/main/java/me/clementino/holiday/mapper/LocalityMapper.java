package me.clementino.holiday.mapper;

import me.clementino.holiday.domain.dop.Locality;
import me.clementino.holiday.dto.CreateHolidayRequest;
import me.clementino.holiday.dto.LocalityResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between DOP Locality domain objects and DTOs.
 *
 * <p>This mapper handles the hierarchical nature of geographical locations using sealed interfaces
 * and provides type-safe transformations with validation for locality consistency.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li>Hierarchical mapping support for Country/Subdivision/City
 *   <li>Validation for locality consistency (subdivision must belong to country, etc.)
 *   <li>Pattern matching for sealed interface variants
 *   <li>Bidirectional mapping between domain objects and DTOs
 * </ul>
 */
@Mapper(componentModel = "spring")
public interface LocalityMapper {

  // ===== DOMAIN TO DTO MAPPINGS =====

  /**
   * Maps a DOP Locality domain object to LocalityResponse DTO using pattern matching.
   *
   * @param locality the domain locality object
   * @return mapped response DTO
   */
  default LocalityResponse toResponse(Locality locality) {
    if (locality == null) {
      return null;
    }

    return switch (locality) {
      case Locality.Country country -> LocalityResponse.country(country.code(), country.name());
      case Locality.Subdivision subdivision ->
          LocalityResponse.subdivision(
              subdivision.country().code(),
              subdivision.country().name(),
              subdivision.code(),
              subdivision.name());
      case Locality.City city ->
          LocalityResponse.city(
              city.country().code(),
              city.country().name(),
              city.subdivision().code(),
              city.subdivision().name(),
              city.name());
    };
  }

  // ===== DTO TO DOMAIN MAPPINGS =====

  /**
   * Maps LocalityResponse DTO to DOP Locality domain object using pattern matching on type.
   *
   * @param response the response DTO
   * @return mapped domain locality object
   */
  default Locality fromResponse(LocalityResponse response) {
    if (response == null) {
      return null;
    }

    return switch (response.type()) {
      case COUNTRY -> new Locality.Country(response.countryCode(), response.countryName());
      case SUBDIVISION -> {
        var country = new Locality.Country(response.countryCode(), response.countryName());
        yield new Locality.Subdivision(
            country,
            response
                .subdivisionCode()
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "Subdivision code is required for subdivision locality")),
            response
                .subdivisionName()
                .orElseThrow(
                    () ->
                        new IllegalArgumentException(
                            "Subdivision name is required for subdivision locality")));
      }
      case CITY -> {
        var country = new Locality.Country(response.countryCode(), response.countryName());
        var subdivision =
            new Locality.Subdivision(
                country,
                response
                    .subdivisionCode()
                    .orElseThrow(
                        () ->
                            new IllegalArgumentException(
                                "Subdivision code is required for city locality")),
                response
                    .subdivisionName()
                    .orElseThrow(
                        () ->
                            new IllegalArgumentException(
                                "Subdivision name is required for city locality")));
        yield new Locality.City(
            response
                .cityName()
                .orElseThrow(
                    () -> new IllegalArgumentException("City name is required for city locality")),
            subdivision,
            country);
      }
    };
  }

  /**
   * Maps CreateHolidayRequest.LocalityDto to DOP Locality domain object.
   *
   * @param localityDto the request DTO
   * @return mapped domain locality object
   */
  @Named("fromDto")
  static Locality fromDto(CreateHolidayRequest.LocalityDto localityDto) {
    if (localityDto == null) {
      return null;
    }

    var country = new Locality.Country(localityDto.countryCode(), localityDto.countryName());

    // Determine locality type based on available fields
    if (localityDto.cityName().isPresent()) {
      // City level - requires subdivision
      if (localityDto.subdivisionCode().isEmpty() || localityDto.subdivisionName().isEmpty()) {
        throw new IllegalArgumentException(
            "Subdivision code and name are required when city name is provided");
      }
      var subdivision =
          new Locality.Subdivision(
              country, localityDto.subdivisionCode().get(), localityDto.subdivisionName().get());
      return new Locality.City(localityDto.cityName().get(), subdivision, country);
    } else if (localityDto.subdivisionCode().isPresent()
        && localityDto.subdivisionName().isPresent()) {
      // Subdivision level
      return new Locality.Subdivision(
          country, localityDto.subdivisionCode().get(), localityDto.subdivisionName().get());
    } else {
      // Country level
      return country;
    }
  }

  /**
   * Maps DOP Locality domain object to CreateHolidayRequest.LocalityDto.
   *
   * @param locality the domain locality object
   * @return mapped request DTO
   */
  default CreateHolidayRequest.LocalityDto toDto(Locality locality) {
    if (locality == null) {
      return null;
    }

    return switch (locality) {
      case Locality.Country country ->
          new CreateHolidayRequest.LocalityDto(
              country.code(),
              country.name(),
              java.util.Optional.empty(),
              java.util.Optional.empty(),
              java.util.Optional.empty());
      case Locality.Subdivision subdivision ->
          new CreateHolidayRequest.LocalityDto(
              subdivision.country().code(),
              subdivision.country().name(),
              java.util.Optional.of(subdivision.code()),
              java.util.Optional.of(subdivision.name()),
              java.util.Optional.empty());
      case Locality.City city ->
          new CreateHolidayRequest.LocalityDto(
              city.country().code(),
              city.country().name(),
              java.util.Optional.of(city.subdivision().code()),
              java.util.Optional.of(city.subdivision().name()),
              java.util.Optional.of(city.name()));
    };
  }

  // ===== VALIDATION METHODS =====

  /**
   * Validates locality consistency - ensures that hierarchical relationships are correct.
   *
   * @param locality the locality to validate
   * @return true if the locality is consistent, false otherwise
   */
  default boolean validateLocalityConsistency(Locality locality) {
    if (locality == null) {
      return false;
    }

    return switch (locality) {
      case Locality.Country country -> validateCountry(country);
      case Locality.Subdivision subdivision -> validateSubdivision(subdivision);
      case Locality.City city -> validateCity(city);
    };
  }

  /**
   * Validates a country locality.
   *
   * @param country the country to validate
   * @return true if valid
   */
  default boolean validateCountry(Locality.Country country) {
    return country != null
        && country.code() != null
        && !country.code().isBlank()
        && country.code().length() == 2
        && country.name() != null
        && !country.name().isBlank();
  }

  /**
   * Validates a subdivision locality.
   *
   * @param subdivision the subdivision to validate
   * @return true if valid
   */
  default boolean validateSubdivision(Locality.Subdivision subdivision) {
    return subdivision != null
        && validateCountry(subdivision.country())
        && subdivision.code() != null
        && !subdivision.code().isBlank()
        && subdivision.name() != null
        && !subdivision.name().isBlank();
  }

  /**
   * Validates a city locality.
   *
   * @param city the city to validate
   * @return true if valid
   */
  default boolean validateCity(Locality.City city) {
    return city != null
        && validateSubdivision(city.subdivision())
        && city.name() != null
        && !city.name().isBlank()
        && city.country().equals(city.subdivision().country()); // Consistency check
  }

  // ===== UTILITY METHODS =====

  /**
   * Gets the hierarchical level of a locality (1=country, 2=subdivision, 3=city).
   *
   * @param locality the locality
   * @return hierarchical level
   */
  default int getHierarchicalLevel(Locality locality) {
    if (locality == null) {
      return 0;
    }

    return switch (locality) {
      case Locality.Country country -> 1;
      case Locality.Subdivision subdivision -> 2;
      case Locality.City city -> 3;
    };
  }

  /**
   * Checks if one locality is a parent of another in the hierarchy.
   *
   * @param parent the potential parent locality
   * @param child the potential child locality
   * @return true if parent is a hierarchical parent of child
   */
  default boolean isParentOf(Locality parent, Locality child) {
    if (parent == null || child == null) {
      return false;
    }

    return switch (parent) {
      case Locality.Country parentCountry ->
          switch (child) {
            case Locality.Country childCountry -> false; // Same level
            case Locality.Subdivision childSubdivision ->
                parentCountry.equals(childSubdivision.country());
            case Locality.City childCity -> parentCountry.equals(childCity.country());
          };
      case Locality.Subdivision parentSubdivision ->
          switch (child) {
            case Locality.Country childCountry -> false; // Child can't be higher level
            case Locality.Subdivision childSubdivision -> false; // Same level
            case Locality.City childCity -> parentSubdivision.equals(childCity.subdivision());
          };
      case Locality.City parentCity -> false; // City can't be parent of anything
    };
  }

  /**
   * Gets the country from any locality type.
   *
   * @param locality the locality
   * @return the country
   */
  default Locality.Country getCountry(Locality locality) {
    if (locality == null) {
      return null;
    }

    return switch (locality) {
      case Locality.Country country -> country;
      case Locality.Subdivision subdivision -> subdivision.country();
      case Locality.City city -> city.country();
    };
  }

  /**
   * Gets the subdivision from a city or subdivision locality.
   *
   * @param locality the locality
   * @return the subdivision, or null if not applicable
   */
  default Locality.Subdivision getSubdivision(Locality locality) {
    if (locality == null) {
      return null;
    }

    return switch (locality) {
      case Locality.Country country -> null;
      case Locality.Subdivision subdivision -> subdivision;
      case Locality.City city -> city.subdivision();
    };
  }
}
