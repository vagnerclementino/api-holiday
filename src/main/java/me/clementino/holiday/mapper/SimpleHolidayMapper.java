package me.clementino.holiday.mapper;

import java.time.LocalDateTime;
import java.util.List;
import me.clementino.holiday.domain.HolidayData;
import me.clementino.holiday.domain.dop.FixedHoliday;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.Locality;
import me.clementino.holiday.domain.dop.MoveableFromBaseHoliday;
import me.clementino.holiday.domain.dop.MoveableHoliday;
import me.clementino.holiday.domain.dop.ObservedHoliday;
import me.clementino.holiday.dto.HolidayResponseDTO;
import me.clementino.holiday.dto.LocationInfo;
import me.clementino.holiday.dto.WhenInfo;
import me.clementino.holiday.entity.HolidayEntity;
import me.clementino.holiday.entity.LocalityEntity;
import org.springframework.stereotype.Component;

/** Simple mapper for Holiday data using DOP principles. */
@Component
public class SimpleHolidayMapper {

  /** Convert HolidayData to HolidayResponseDTO. */
  public HolidayResponseDTO toResponse(HolidayData holidayData) {
    // Create when info (required)
    WhenInfo when = WhenInfo.from(holidayData.date());

    // Create observed info (optional)
    WhenInfo observed = holidayData.observed().map(WhenInfo::from).orElse(null);

    // Create location info (at least one required)
    LocationInfo locationInfo = LocationInfo.from(holidayData.location());
    List<LocationInfo> where = List.of(locationInfo);

    return new HolidayResponseDTO(
        holidayData.id(),
        holidayData.name(),
        when,
        observed,
        where,
        holidayData.type(),
        holidayData.description().orElse(null),
        holidayData.dateCreated().map(LocalDateTime::toString).orElse(null),
        holidayData.lastUpdated().map(LocalDateTime::toString).orElse(null));
  }

  /**
   * Convert Holiday (DOP domain object) to HolidayEntity (persistence entity).
   *
   * <p>This method demonstrates DOP principle of separating operations from data by providing a
   * pure transformation function that converts between different data representations using pattern
   * matching.
   *
   * @param holiday the Holiday domain object (sealed interface)
   * @return HolidayEntity for persistence
   */
  public HolidayEntity toEntity(Holiday holiday) {
    // Use pattern matching to handle different Holiday types
    return switch (holiday) {
      case FixedHoliday fixed -> toEntityFromFixed(fixed);
      case ObservedHoliday observed -> toEntityFromObserved(observed);
      case MoveableHoliday moveable -> toEntityFromMoveable(moveable);
      case MoveableFromBaseHoliday moveableFromBase ->
          toEntityFromMoveableFromBase(moveableFromBase);
    };
  }

  /** Convert FixedHoliday to HolidayEntity. */
  private HolidayEntity toEntityFromFixed(FixedHoliday fixed) {
    // Get primary locality for basic fields
    Locality primaryLocality = fixed.localities().getFirst();
    String country = getCountryCode(primaryLocality);

    HolidayEntity entity =
        new HolidayEntity(fixed.name(), fixed.description(), fixed.date(), country, fixed.type());

    // Convert DOP localities to LocalityEntity list
    entity.setLocalities(convertLocalitiesFromDOP(fixed.localities()));

    return entity;
  }

  /** Convert ObservedHoliday to HolidayEntity. */
  private HolidayEntity toEntityFromObserved(ObservedHoliday observed) {
    // Get primary locality for basic fields
    Locality primaryLocality = observed.localities().get(0);
    String country = getCountryCode(primaryLocality);

    HolidayEntity entity =
        new HolidayEntity(
            observed.name(), observed.description(), observed.date(), country, observed.type());

    // Convert DOP localities to LocalityEntity list
    entity.setLocalities(convertLocalitiesFromDOP(observed.localities()));

    return entity;
  }

  /** Convert MoveableHoliday to HolidayEntity. */
  private HolidayEntity toEntityFromMoveable(MoveableHoliday moveable) {
    // Get primary locality for basic fields
    Locality primaryLocality = moveable.localities().get(0);
    String country = getCountryCode(primaryLocality);

    HolidayEntity entity =
        new HolidayEntity(
            moveable.name(), moveable.description(), moveable.date(), country, moveable.type());

    // Convert DOP localities to LocalityEntity list
    entity.setLocalities(convertLocalitiesFromDOP(moveable.localities()));

    return entity;
  }

  /** Convert MoveableFromBaseHoliday to HolidayEntity. */
  private HolidayEntity toEntityFromMoveableFromBase(MoveableFromBaseHoliday moveableFromBase) {
    // Get primary locality for basic fields
    Locality primaryLocality = moveableFromBase.localities().get(0);
    String country = getCountryCode(primaryLocality);

    HolidayEntity entity =
        new HolidayEntity(
            moveableFromBase.name(),
            moveableFromBase.description(),
            moveableFromBase.date(),
            country,
            moveableFromBase.type());

    // Convert DOP localities to LocalityEntity list
    entity.setLocalities(convertLocalitiesFromDOP(moveableFromBase.localities()));

    return entity;
  }

  /** Extract country code from a Locality using pattern matching. */
  private String getCountryCode(Locality locality) {
    return switch (locality) {
      case Locality.Country country -> country.code();
      case Locality.Subdivision subdivision -> subdivision.country().code();
      case Locality.City city -> city.subdivision().country().code();
    };
  }

  /** Convert DOP Locality list to LocalityEntity list. */
  private List<LocalityEntity> convertLocalitiesFromDOP(List<Locality> localities) {
    return localities.stream().map(this::convertLocalityFromDOP).toList();
  }

  /** Convert single DOP Locality to LocalityEntity. */
  private LocalityEntity convertLocalityFromDOP(Locality locality) {
    return switch (locality) {
      case Locality.Country country -> {
        LocalityEntity entity = new LocalityEntity();
        entity.setCountryCode(country.code());
        entity.setCountryName(country.name());
        yield entity;
      }
      case Locality.Subdivision subdivision -> {
        LocalityEntity entity = new LocalityEntity();
        entity.setCountryCode(subdivision.country().code());
        entity.setCountryName(subdivision.country().name());
        entity.setSubdivisionCode(subdivision.code());
        entity.setSubdivisionName(subdivision.name());
        yield entity;
      }
      case Locality.City city -> {
        LocalityEntity entity = new LocalityEntity();
        entity.setCountryCode(city.subdivision().country().code());
        entity.setCountryName(city.subdivision().country().name());
        entity.setSubdivisionCode(city.subdivision().code());
        entity.setSubdivisionName(city.subdivision().name());
        entity.setCityName(city.name());
        yield entity;
      }
    };
  }
}
