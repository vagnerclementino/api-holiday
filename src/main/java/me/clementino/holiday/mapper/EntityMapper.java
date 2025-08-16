package me.clementino.holiday.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;
import me.clementino.holiday.domain.dop.FixedHoliday;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.Locality;
import me.clementino.holiday.domain.dop.MoveableFromBaseHoliday;
import me.clementino.holiday.domain.dop.MoveableHoliday;
import me.clementino.holiday.domain.dop.ObservedHoliday;
import me.clementino.holiday.entity.HolidayEntity;
import me.clementino.holiday.entity.LocalityEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Manual mapper for converting between DOP Holiday domain objects and MongoDB entities.
 *
 * <p>This mapper handles the complex task of serializing sealed interfaces for persistence while
 * maintaining the immutable nature of domain objects. It provides bidirectional mapping between
 * domain objects and entities with proper serialization handling.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li>Bidirectional mapping between DOP Holiday sealed interface and HolidayEntity
 *   <li>Serialization of sealed interfaces for persistence
 *   <li>Proper handling of hierarchical locality data
 *   <li>Metadata preservation (timestamps, version, etc.)
 *   <li>Year-based calculation support
 * </ul>
 */
@Component
public class EntityMapper {

  @Autowired private ObjectMapper objectMapper;

  // ===== DOMAIN TO ENTITY MAPPINGS =====

  /**
   * Maps a DOP Holiday domain object to HolidayEntity.
   *
   * @param domainHoliday the domain holiday object
   * @return mapped HolidayEntity
   */
  public HolidayEntity toEntity(Holiday domainHoliday) {
    if (domainHoliday == null) {
      return null;
    }

    HolidayEntity entity = new HolidayEntity();

    // Map basic fields
    entity.setName(domainHoliday.name());
    entity.setDescription(domainHoliday.description());
    entity.setDate(domainHoliday.date());
    entity.setType(domainHoliday.type());

    // Extract locality information
    List<Locality> localities = domainHoliday.localities();
    if (localities != null && !localities.isEmpty()) {
      Locality firstLocality = localities.getFirst();

      // Set flat locality fields for querying
      entity.setCountry(extractCountry(firstLocality));
      entity.setState(extractState(firstLocality));
      entity.setCity(extractCity(firstLocality));

      // Set embedded locality entities
      entity.setLocalities(
          localities.stream().map(LocalityEntity::fromDopLocality).collect(Collectors.toList()));
    }

    // Set holiday variant information
    entity.setHolidayVariant(domainHoliday.getClass().getSimpleName());

    // Serialize holiday data
    try {
      entity.setHolidayData(objectMapper.writeValueAsString(domainHoliday));
      entity.setLocalityData(objectMapper.writeValueAsString(localities));
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to serialize holiday data", e);
    }

    return entity;
  }

  /**
   * Maps a DOP Holiday domain object to HolidayEntity with year information.
   *
   * @param domainHoliday the domain holiday object
   * @param year the year for which this holiday is calculated
   * @return mapped HolidayEntity with year information
   */
  public HolidayEntity toEntityWithYear(Holiday domainHoliday, Integer year) {
    HolidayEntity entity = toEntity(domainHoliday);
    entity.setYear(year);
    entity.setCalculated(true);

    // Set calculation rule based on holiday type
    entity.setCalculationRule(getCalculationRule(domainHoliday));

    // Set type-specific fields
    switch (domainHoliday) {
      case ObservedHoliday observedHoliday -> {
        entity.setMondayisation(observedHoliday.mondayisation());
        entity.setObserved(observedHoliday.observed());
      }
      case MoveableFromBaseHoliday moveableFromBase -> {
        entity.setDayOffset(moveableFromBase.dayOffset());
        // Note: baseHolidayId would need to be set separately based on business logic
      }
      default -> {
        // No additional fields for FixedHoliday and MoveableHoliday
      }
    }

    return entity;
  }

  // ===== ENTITY TO DOMAIN MAPPINGS =====

  /**
   * Maps a HolidayEntity to DOP Holiday domain object.
   *
   * @param entity the holiday entity
   * @return mapped DOP Holiday object
   */
  public Holiday toDomain(HolidayEntity entity) {
    if (entity == null) {
      return null;
    }

    if (entity.getHolidayData() != null && !entity.getHolidayData().isBlank()) {
      // Deserialize from stored holiday data
      return deserializeHoliday(entity.getHolidayData(), entity.getHolidayVariant());
    }

    // Fallback: construct from entity fields
    return constructHolidayFromEntity(entity);
  }

  // ===== HELPER METHODS =====

  private String extractCountry(Locality locality) {
    return switch (locality) {
      case Locality.Country country -> country.code();
      case Locality.Subdivision subdivision -> subdivision.country().code();
      case Locality.City city -> city.country().code();
    };
  }

  private String extractState(Locality locality) {
    return switch (locality) {
      case Locality.Country country -> null;
      case Locality.Subdivision subdivision -> subdivision.code();
      case Locality.City city -> city.subdivision().code();
    };
  }

  private String extractCity(Locality locality) {
    return switch (locality) {
      case Locality.Country country -> null;
      case Locality.Subdivision subdivision -> null;
      case Locality.City city -> city.name();
    };
  }

  private String getCalculationRule(Holiday holiday) {
    return switch (holiday) {
      case FixedHoliday fixed -> "FIXED";
      case ObservedHoliday observed -> "OBSERVED";
      case MoveableHoliday moveable -> "MOVEABLE:" + moveable.knownHoliday().name();
      case MoveableFromBaseHoliday moveableFromBase ->
          "MOVEABLE_FROM_BASE:"
              + moveableFromBase.knownHoliday().name()
              + ":"
              + moveableFromBase.dayOffset();
    };
  }

  private Holiday deserializeHoliday(String holidayData, String holidayVariant) {
    try {
      return switch (holidayVariant) {
        case "FixedHoliday" -> objectMapper.readValue(holidayData, FixedHoliday.class);
        case "ObservedHoliday" -> objectMapper.readValue(holidayData, ObservedHoliday.class);
        case "MoveableHoliday" -> objectMapper.readValue(holidayData, MoveableHoliday.class);
        case "MoveableFromBaseHoliday" ->
            objectMapper.readValue(holidayData, MoveableFromBaseHoliday.class);
        default -> throw new IllegalArgumentException("Unknown holiday variant: " + holidayVariant);
      };
    } catch (JsonProcessingException e) {
      throw new RuntimeException("Failed to deserialize holiday", e);
    }
  }

  private Holiday constructHolidayFromEntity(HolidayEntity entity) {
    // This is a fallback method to construct a basic FixedHoliday from entity fields
    // In practice, you'd need more sophisticated logic based on the holiday variant

    List<Locality> localities =
        entity.getLocalities() != null
            ? entity.getLocalities().stream()
                .map(LocalityEntity::toDopLocality)
                .collect(Collectors.toList())
            : List.of(new Locality.Country(entity.getCountry(), entity.getCountry()));

    return new FixedHoliday(
        entity.getName(),
        entity.getDescription(),
        entity.getDate(),
        localities,
        entity.getType(),
        entity.isMondayisation());
  }
}
