package me.clementino.holiday.mapper;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import me.clementino.holiday.domain.dop.FixedHoliday;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.MoveableFromBaseHoliday;
import me.clementino.holiday.domain.dop.MoveableHoliday;
import me.clementino.holiday.domain.dop.MoveableHolidayType;
import me.clementino.holiday.domain.dop.ObservedHoliday;
import me.clementino.holiday.dto.CreateHolidayRequest;
import me.clementino.holiday.dto.HolidayResponse;
import me.clementino.holiday.dto.UpdateHolidayRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for converting between DOP Holiday domain objects and DTOs.
 *
 * <p>This mapper demonstrates DOP principles by providing type-safe transformations between
 * immutable domain objects and DTOs, with custom handling for sealed interface variants.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li>Type-safe mapping between Holiday sealed interface variants and DTOs
 *   <li>Custom mapping methods for sealed interface pattern matching
 *   <li>Automatic generation of boilerplate mapping code
 *   <li>Integration with Spring dependency injection
 * </ul>
 */
@Mapper(
    componentModel = "spring",
    uses = {LocalityMapper.class})
public interface HolidayMapper {

  // ===== DOMAIN TO DTO MAPPINGS =====

  /**
   * Maps a DOP Holiday domain object to HolidayResponse DTO.
   *
   * @param holiday the domain holiday object
   * @return mapped response DTO
   */
  @Mapping(target = "id", source = ".", qualifiedByName = "extractHolidayId")
  @Mapping(target = "variant", source = ".", qualifiedByName = "mapHolidayVariant")
  @Mapping(target = "locality", source = ".", qualifiedByName = "mapPrimaryLocality")
  @Mapping(target = "createdAt", source = ".", qualifiedByName = "mapCreatedAt")
  @Mapping(target = "updatedAt", source = ".", qualifiedByName = "mapUpdatedAt")
  @Mapping(target = "version", source = ".", qualifiedByName = "mapVersion")
  HolidayResponse toResponse(Holiday holiday);

  /**
   * Maps a list of DOP Holiday domain objects to HolidayResponse DTOs.
   *
   * @param holidays list of domain holiday objects
   * @return list of mapped response DTOs
   */
  List<HolidayResponse> toResponseList(List<Holiday> holidays);

  /**
   * Maps HolidayData domain object to HolidayResponse DTO.
   *
   * @param holidayData the domain holiday data object
   * @return mapped response DTO
   */
  default HolidayResponse toResponse(me.clementino.holiday.domain.HolidayData holidayData) {
    if (holidayData == null) {
      return null;
    }

    // Create locality response from HolidayData
    var localityResponse = createLocalityResponseFromHolidayData(holidayData);

    // Create variant response - simplified as Fixed for HolidayData
    var variant = new HolidayResponse.HolidayVariantResponse.Fixed(holidayData.date());

    return new HolidayResponse(
        holidayData.id(),
        holidayData.name(),
        holidayData.description().orElse(""),
        holidayData.type(),
        localityResponse,
        variant,
        holidayData
            .dateCreated()
            .map(ldt -> ldt.atOffset(ZoneOffset.UTC))
            .orElse(OffsetDateTime.now()),
        holidayData
            .lastUpdated()
            .map(ldt -> ldt.atOffset(ZoneOffset.UTC))
            .orElse(OffsetDateTime.now()),
        holidayData.version().orElse(1));
  }

  /**
   * Maps a list of HolidayData domain objects to HolidayResponse DTOs.
   *
   * @param holidayDataList list of domain holiday data objects
   * @return list of mapped response DTOs
   */
  default List<HolidayResponse> toResponseListFromHolidayData(
      List<me.clementino.holiday.domain.HolidayData> holidayDataList) {
    if (holidayDataList == null) {
      return null;
    }
    return holidayDataList.stream().map(this::toResponse).toList();
  }

  // ===== DTO TO DOMAIN MAPPINGS =====

  /**
   * Maps CreateHolidayRequest DTO to DOP Holiday domain object.
   *
   * @param request the create request DTO
   * @return mapped domain holiday object
   */
  default Holiday fromCreateRequest(CreateHolidayRequest request) {
    return mapCreateRequestToHoliday(request);
  }

  /**
   * Maps UpdateHolidayRequest DTO to DOP Holiday domain object.
   *
   * @param request the update request DTO
   * @param existingHoliday the existing holiday to update
   * @return mapped domain holiday object
   */
  default Holiday fromUpdateRequest(UpdateHolidayRequest request, Holiday existingHoliday) {
    return mapUpdateRequestToHoliday(request);
  }

  // ===== CUSTOM MAPPING METHODS =====

  /**
   * Extracts holiday ID from domain object. Since DOP holidays don't have IDs, this returns null
   * for new holidays or extracts from metadata if available.
   */
  @Named("extractHolidayId")
  default String extractHolidayId(Holiday holiday) {
    // For DOP holidays, ID is typically managed at the persistence layer
    // This method can be enhanced to extract ID from metadata if needed
    return null;
  }

  /**
   * Maps DOP Holiday sealed interface variants to HolidayVariantResponse DTOs using pattern
   * matching.
   */
  @Named("mapHolidayVariant")
  default HolidayResponse.HolidayVariantResponse mapHolidayVariant(Holiday holiday) {
    return switch (holiday) {
      case FixedHoliday fixed -> new HolidayResponse.HolidayVariantResponse.Fixed(fixed.date());
      case ObservedHoliday observed ->
          new HolidayResponse.HolidayVariantResponse.Observed(
              observed.date(),
              Optional.ofNullable(observed.observed()),
              observed.mondayisation(),
              observed.isWeekend());
      case MoveableHoliday moveable ->
          new HolidayResponse.HolidayVariantResponse.Moveable(
              moveable.knownHoliday(),
              MoveableHolidayType.LUNAR_BASED, // Default type, would need to be stored separately
              moveable.mondayisation(),
              Optional.of(moveable.date()),
              Optional.empty() // Year would be extracted from metadata if available
              );
      case MoveableFromBaseHoliday moveableFromBase ->
          new HolidayResponse.HolidayVariantResponse.MoveableFromBase(
              null, // Base holiday ID would be extracted from metadata
              moveableFromBase.baseHoliday().name(),
              moveableFromBase.dayOffset(),
              moveableFromBase.mondayisation(),
              Optional.of(moveableFromBase.date()),
              Optional.empty() // Year would be extracted from metadata if available
              );
    };
  }

  /**
   * Maps the primary locality from the holiday's localities list. Takes the first locality as the
   * primary one.
   */
  @Named("mapPrimaryLocality")
  default me.clementino.holiday.domain.dop.Locality mapPrimaryLocality(Holiday holiday) {
    var localities = holiday.localities();
    return localities != null && !localities.isEmpty() ? localities.getFirst() : null;
  }

  /**
   * Maps created timestamp. For DOP holidays, this would typically come from metadata or be set to
   * current time.
   */
  @Named("mapCreatedAt")
  default OffsetDateTime mapCreatedAt(Holiday holiday) {
    // For DOP holidays, timestamps are typically managed at the persistence layer
    return OffsetDateTime.now();
  }

  /**
   * Maps updated timestamp. For DOP holidays, this would typically come from metadata or be set to
   * current time.
   */
  @Named("mapUpdatedAt")
  default OffsetDateTime mapUpdatedAt(Holiday holiday) {
    // For DOP holidays, timestamps are typically managed at the persistence layer
    return OffsetDateTime.now();
  }

  /**
   * Maps version number. For DOP holidays, this would typically come from metadata or default to 1.
   */
  @Named("mapVersion")
  default Integer mapVersion(Holiday holiday) {
    // For DOP holidays, version is typically managed at the persistence layer
    return 1;
  }

  /** Maps CreateHolidayRequest to DOP Holiday using pattern matching on the variant. */
  @Named("mapCreateRequestToHoliday")
  default Holiday mapCreateRequestToHoliday(CreateHolidayRequest request) {
    var locality = LocalityMapper.fromDto(request.locality());
    var localities = List.of(locality);

    return switch (request.variant()) {
      case CreateHolidayRequest.HolidayVariantDto.Fixed fixed ->
          new FixedHoliday(
              request.name(),
              request.description() != null ? request.description() : "",
              fixed.date(),
              localities,
              request.type());
      case CreateHolidayRequest.HolidayVariantDto.Observed observed ->
          new ObservedHoliday(
              request.name(),
              request.description() != null ? request.description() : "",
              observed.date(),
              localities,
              request.type(),
              observed.observedDate().orElse(null),
              observed.mondayisation());
      case CreateHolidayRequest.HolidayVariantDto.Moveable moveable ->
          new MoveableHoliday(
              request.name(),
              request.description() != null ? request.description() : "",
              moveable.calculatedDate().orElse(null), // Will be calculated later
              localities,
              request.type(),
              moveable.knownHoliday(),
              moveable.mondayisation());
      case CreateHolidayRequest.HolidayVariantDto.MoveableFromBase moveableFromBase -> {
        // For MoveableFromBase, we need to resolve the base holiday
        // This would typically be done in the service layer
        throw new UnsupportedOperationException(
            "MoveableFromBase holiday creation requires base holiday resolution in service layer");
      }
    };
  }

  /**
   * Maps UpdateHolidayRequest to DOP Holiday. This creates a new immutable holiday instance with
   * updated values.
   */
  @Named("mapUpdateRequestToHoliday")
  default Holiday mapUpdateRequestToHoliday(UpdateHolidayRequest request) {
    // Similar to create request mapping, but would merge with existing holiday
    // This is a simplified implementation - full implementation would handle
    // partial updates
    throw new UnsupportedOperationException(
        "Update request mapping requires service layer logic for merging with existing holiday");
  }

  // ===== UTILITY METHODS =====

  /** Converts LocalDateTime to OffsetDateTime using UTC offset. */
  default OffsetDateTime localDateTimeToOffsetDateTime(LocalDateTime localDateTime) {
    return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
  }

  /** Converts OffsetDateTime to LocalDateTime by extracting the local part. */
  default LocalDateTime offsetDateTimeToLocalDateTime(OffsetDateTime offsetDateTime) {
    return offsetDateTime != null ? offsetDateTime.toLocalDateTime() : null;
  }

  /** Creates LocalityResponse from HolidayData location information. */
  default me.clementino.holiday.dto.LocalityResponse createLocalityResponseFromHolidayData(
      me.clementino.holiday.domain.HolidayData holidayData) {
    var location = holidayData.location();
    String country = location.country();
    var state = location.state();
    var city = location.city();

    if (city.isPresent() && state.isPresent()) {
      return me.clementino.holiday.dto.LocalityResponse.city(
          country, country, state.get(), state.get(), city.get());
    } else if (state.isPresent()) {
      return me.clementino.holiday.dto.LocalityResponse.subdivision(
          country, country, state.get(), state.get());
    } else {
      return me.clementino.holiday.dto.LocalityResponse.country(country, country);
    }
  }
}
