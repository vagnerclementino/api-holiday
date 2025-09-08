package me.clementino.holiday.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.HolidayOperations;
import me.clementino.holiday.domain.dop.HolidayType;
import me.clementino.holiday.domain.dop.Location;
import me.clementino.holiday.dto.HolidayDataDTO;
import me.clementino.holiday.entity.HolidayEntity;
import me.clementino.holiday.entity.LocalityEntity;
import me.clementino.holiday.mapper.HolidayMapper;
import me.clementino.holiday.repository.HolidayRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Service layer that orchestrates operations on holiday data using DOP principles.
 *
 * <p>This service works with HolidayEntity for persistence while maintaining DOP Holiday domain
 * objects for business logic. It provides seamless conversion between entity and domain layers.
 */
@Service
public class HolidayService {

  private final HolidayRepository holidayRepository;
  private final MongoTemplate mongoTemplate;
  private final HolidayOperations holidayOperations;
  private final HolidayMapper mapper;

  public HolidayService(
      HolidayRepository holidayRepository,
      MongoTemplate mongoTemplate,
      HolidayOperations holidayOperations,
      HolidayMapper mapper) {
    this.holidayRepository = holidayRepository;
    this.mongoTemplate = mongoTemplate;
    this.holidayOperations = holidayOperations;
    this.mapper = mapper;
  }

  /** Find holiday by ID. */
  public Optional<HolidayDataDTO> findById(String id) {
    return holidayRepository.findById(id).map(this::toDomainData);
  }

  /** Delete holiday by ID. */
  public boolean deleteById(String id) {
    if (holidayRepository.existsById(id)) {
      holidayRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /** Find all holidays with filters. */
  public List<HolidayDataDTO> findAllWithFilters(
      String country,
      String state,
      String city,
      HolidayType type,
      LocalDate startDate,
      LocalDate endDate,
      String namePattern) {

    Query query = new Query();

    if (country != null && !country.isBlank()) {
      query.addCriteria(Criteria.where("localities.countryCode").regex(country, "i"));
    }
    if (state != null && !state.isBlank()) {
      query.addCriteria(Criteria.where("localities.subdivisionCode").regex(state, "i"));
    }
    if (city != null && !city.isBlank()) {
      query.addCriteria(Criteria.where("localities.cityName").regex(city, "i"));
    }
    if (type != null) {
      query.addCriteria(Criteria.where("type").is(type));
    }
    if (startDate != null) {
      query.addCriteria(Criteria.where("date").gte(startDate));
    }
    if (endDate != null) {
      query.addCriteria(Criteria.where("date").lte(endDate));
    }
    if (namePattern != null && !namePattern.isBlank()) {
      query.addCriteria(Criteria.where("name").regex(namePattern, "i"));
    }

    query.with(Sort.by(Sort.Direction.ASC, "date"));

    List<HolidayEntity> entities = mongoTemplate.find(query, HolidayEntity.class);
    return entities.stream().map(this::toDomainData).toList();
  }

  public HolidayDataDTO create(Holiday holiday) {
    return create(holiday, null);
  }

  /** Create a new holiday. */
  public HolidayDataDTO create(Holiday holiday, Integer year) {

    int defaultYear = Optional.ofNullable(year).orElse(LocalDate.now().getYear());

    var holidayWithDate = holidayOperations.calculateDate(holiday, defaultYear);
    var holidayWithObserved = holidayOperations.calculateObservedDate(holidayWithDate, defaultYear);

    var entity = mapper.toEntity(holidayWithObserved);
    entity.setId(UUID.randomUUID().toString());
    entity.setDateCreated(LocalDateTime.now());
    entity.setLastUpdated(LocalDateTime.now());

    HolidayEntity saved = holidayRepository.save(entity);
    return toDomainData(saved);
  }

  /** Update an existing holiday. */
  public Optional<HolidayDataDTO> update(String id, HolidayDataDTO holidayDataDTO) {
    return holidayRepository
        .findById(id)
        .map(
            existing -> {
              HolidayEntity updated = toEntity(holidayDataDTO);
              updated.setId(existing.getId());
              updated.setDateCreated(existing.getDateCreated());
              updated.setLastUpdated(LocalDateTime.now());
              updated.setVersion(existing.getVersion());

              HolidayEntity saved = holidayRepository.save(updated);
              return toDomainData(saved);
            });
  }

  /** Convert HolidayEntity to HolidayDataDTO. */
  private HolidayDataDTO toDomainData(HolidayEntity entity) {
    if (entity.getName() == null || entity.getName().isBlank()) {
      throw new IllegalStateException(
          "HolidayEntity name is null or blank for ID: "
              + entity.getId()
              + ". This indicates a data corruption issue in MongoDB.");
    }
    if (entity.getDate() == null) {
      throw new IllegalStateException("HolidayEntity date is null for ID: " + entity.getId());
    }

    if (entity.getType() == null) {
      throw new IllegalStateException("HolidayEntity type is null for ID: " + entity.getId());
    }

    return new HolidayDataDTO(
        entity.getId(),
        entity.getName(),
        entity.getDate(),
        Optional.ofNullable(
            entity.getEffectiveDate().equals(entity.getDate()) ? null : entity.getEffectiveDate()),
        extractLocationFromLocalities(entity.getLocalities()),
        entity.getType(),
        false,
        Optional.ofNullable(entity.getDescription()),
        Optional.ofNullable(entity.getDateCreated()),
        Optional.ofNullable(entity.getLastUpdated()),
        Optional.ofNullable(entity.getVersion()));
  }

  /** Extract Location from List<LocalityEntity> for backward compatibility. */
  private Location extractLocationFromLocalities(List<LocalityEntity> localities) {
    if (localities == null || localities.isEmpty()) {
      return new Location("UNKNOWN", Optional.empty(), Optional.empty());
    }

    LocalityEntity primary = localities.getFirst();

    return new Location(
        primary.getCountryCode() != null ? primary.getCountryCode() : "UNKNOWN",
        Optional.ofNullable(primary.getSubdivisionCode()),
        Optional.ofNullable(primary.getCityName()));
  }

  /** Convert HolidayDataDTO to HolidayEntity. */
  private HolidayEntity toEntity(HolidayDataDTO data) {
    if (data.name() == null || data.name().isBlank()) {
      throw new IllegalArgumentException("HolidayDataDTO name cannot be null or blank");
    }
    if (data.date() == null) {
      throw new IllegalArgumentException("HolidayDataDTO date cannot be null");
    }
    if (data.location() == null
        || data.location().country() == null
        || data.location().country().isBlank()) {
      throw new IllegalArgumentException("HolidayDataDTO country cannot be null or blank");
    }
    if (data.type() == null) {
      throw new IllegalArgumentException("HolidayDataDTO type cannot be null");
    }

    HolidayEntity entity = new HolidayEntity();
    entity.setId(data.id());
    entity.setName(data.name().trim());
    entity.setDate(data.date());
    entity.setType(data.type());
    entity.setDescription(data.description().map(String::trim).orElse(null));
    entity.setDateCreated(data.dateCreated().orElse(null));
    entity.setLastUpdated(data.lastUpdated().orElse(null));
    entity.setVersion(data.version().orElse(null));

    entity.setLocalities(createLocalityEntitiesFromLocation(data.location()));

    return entity;
  }

  /** Convert Location to List<LocalityEntity> for the new HolidayEntity structure. */
  private List<LocalityEntity> createLocalityEntitiesFromLocation(Location location) {
    LocalityEntity localityEntity = new LocalityEntity();
    localityEntity.setCountryCode(location.country());
    localityEntity.setCountryName(location.country());

    if (location.state().isPresent()) {
      localityEntity.setSubdivisionCode(location.state().get());
      localityEntity.setSubdivisionName(location.state().get());
    }

    if (location.city().isPresent()) {
      localityEntity.setCityName(location.city().get());
    }

    return List.of(localityEntity);
  }
}
