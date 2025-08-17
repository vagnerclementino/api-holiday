package me.clementino.holiday.mapper;

import java.time.LocalDateTime;
import java.util.Optional;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.Location;
import me.clementino.holiday.entity.HolidayEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between Domain objects and Entities.
 *
 * <p>This mapper handles the conversion between domain objects and database entities, maintaining
 * immutability principles.
 */
@Mapper(componentModel = "spring")
public interface HolidayEntityMapper {

  /**
   * Convert Holiday domain object to HolidayEntity.
   *
   * @param holiday the domain object
   * @return HolidayEntity for persistence
   */
  @Mapping(target = "observedDate", source = "observedDate", qualifiedByName = "optionalToValue")
  @Mapping(target = "country", source = "location.country")
  @Mapping(target = "state", source = "location", qualifiedByName = "extractState")
  @Mapping(target = "city", source = "location", qualifiedByName = "extractCity")
  @Mapping(target = "description", source = "description", qualifiedByName = "optionalToValue")
  @Mapping(target = "dateCreated", source = "dateCreated", qualifiedByName = "optionalToDateTime")
  @Mapping(target = "lastUpdated", source = "lastUpdated", qualifiedByName = "optionalToDateTime")
  HolidayEntity toEntity(Holiday holiday);

  /**
   * Convert HolidayEntity to Holiday domain object.
   *
   * @param entity the entity
   * @return Holiday domain object
   */
  @Mapping(target = "observedDate", source = "observedDate", qualifiedByName = "valueToOptional")
  @Mapping(target = "location", source = ".", qualifiedByName = "createLocation")
  @Mapping(
      target = "description",
      source = "description",
      qualifiedByName = "valueToOptionalString")
  @Mapping(target = "dateCreated", source = "dateCreated", qualifiedByName = "dateTimeToOptional")
  @Mapping(target = "lastUpdated", source = "lastUpdated", qualifiedByName = "dateTimeToOptional")
  Holiday toDomain(HolidayEntity entity);

  /** Extract state from Location. */
  @Named("extractState")
  default String extractState(Location location) {
    return location.state().orElse(null);
  }

  /** Extract city from Location. */
  @Named("extractCity")
  default String extractCity(Location location) {
    return location.city().orElse(null);
  }

  /** Create Location from entity fields. */
  @Named("createLocation")
  default Location createLocation(HolidayEntity entity) {
    return new Location(
        entity.getCountry(),
        Optional.ofNullable(entity.getState()),
        Optional.ofNullable(entity.getCity()));
  }

  /** Convert Optional to value (null if empty). */
  @Named("optionalToValue")
  default <T> T optionalToValue(Optional<T> optional) {
    return optional.orElse(null);
  }

  /** Convert Optional<LocalDateTime> to LocalDateTime. */
  @Named("optionalToDateTime")
  default LocalDateTime optionalToDateTime(Optional<LocalDateTime> optional) {
    return optional.orElse(null);
  }

  /** Convert value to Optional. */
  @Named("valueToOptional")
  default <T> Optional<T> valueToOptional(T value) {
    return Optional.ofNullable(value);
  }

  /** Convert String to Optional<String>. */
  @Named("valueToOptionalString")
  default Optional<String> valueToOptionalString(String value) {
    return Optional.ofNullable(value).filter(s -> !s.isBlank());
  }

  /** Convert LocalDateTime to Optional<LocalDateTime>. */
  @Named("dateTimeToOptional")
  default Optional<LocalDateTime> dateTimeToOptional(LocalDateTime value) {
    return Optional.ofNullable(value);
  }
}
