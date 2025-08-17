package me.clementino.holiday.mapper;

import java.util.Optional;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.Location;
import me.clementino.holiday.dto.CreateHolidayRequest;
import me.clementino.holiday.dto.UpdateHolidayRequest;
import me.clementino.holiday.util.CountryCodeUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between DTOs and Domain objects.
 *
 * <p>This mapper handles the conversion from external DTOs to internal domain objects following DOP
 * principles.
 */
@Mapper(componentModel = "spring")
public interface HolidayDomainMapper {

  /**
   * Convert CreateHolidayRequest to Holiday domain object.
   *
   * @param request the create request
   * @return Holiday domain object
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "observedDate", expression = "java(Optional.empty())")
  @Mapping(target = "location", source = ".", qualifiedByName = "createLocation")
  @Mapping(target = "description", source = "description", qualifiedByName = "optionalString")
  @Mapping(target = "dateCreated", expression = "java(Optional.empty())")
  @Mapping(target = "lastUpdated", expression = "java(Optional.empty())")
  Holiday toHoliday(CreateHolidayRequest request);

  /**
   * Convert UpdateHolidayRequest to Holiday domain object.
   *
   * @param request the update request
   * @param existingId the existing holiday ID
   * @return Holiday domain object
   */
  @Mapping(target = "id", source = "existingId")
  @Mapping(target = "observedDate", expression = "java(Optional.empty())")
  @Mapping(target = "location", source = "request", qualifiedByName = "createLocation")
  @Mapping(
      target = "description",
      source = "request.description",
      qualifiedByName = "optionalString")
  @Mapping(target = "dateCreated", expression = "java(Optional.empty())")
  @Mapping(target = "lastUpdated", expression = "java(Optional.empty())")
  Holiday toHoliday(UpdateHolidayRequest request, String existingId);

  /** Create Location from request fields. */
  @Named("createLocation")
  default Location createLocation(CreateHolidayRequest request) {
    String normalizedCountry =
        CountryCodeUtil.normalizeCountry(request.country()).orElse(request.country());

    return new Location(
        normalizedCountry,
        Optional.ofNullable(request.state()),
        Optional.ofNullable(request.city()));
  }

  /** Create Location from update request fields. */
  @Named("createLocation")
  default Location createLocation(UpdateHolidayRequest request) {
    String normalizedCountry =
        CountryCodeUtil.normalizeCountry(request.country()).orElse(request.country());

    return new Location(
        normalizedCountry,
        Optional.ofNullable(request.state()),
        Optional.ofNullable(request.city()));
  }

  /** Convert String to Optional<String>. */
  @Named("optionalString")
  default Optional<String> optionalString(String value) {
    return Optional.ofNullable(value).filter(s -> !s.isBlank());
  }
}
