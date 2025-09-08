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
import me.clementino.holiday.dto.HolidayQueryDTO;
import me.clementino.holiday.entity.HolidayEntity;
import me.clementino.holiday.entity.LocalityEntity;
import me.clementino.holiday.mapper.SimpleHolidayMapper;
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
  private final SimpleHolidayMapper mapper;

  public HolidayService(
      HolidayRepository holidayRepository,
      MongoTemplate mongoTemplate,
      HolidayOperations holidayOperations,
      SimpleHolidayMapper mapper) {
    this.holidayRepository = holidayRepository;
    this.mongoTemplate = mongoTemplate;
    this.holidayOperations = holidayOperations;
    this.mapper = mapper;
  }

  /** Find all holidays with optional filtering using DOP query object. */
  public List<HolidayDataDTO> findAll(HolidayQueryDTO query) {
    return findAllWithFilters(null, null, null, null, null, null, null, null);
  }

  /** Find all holidays without filters. */
  public List<HolidayDataDTO> findAll() {
    return findAllWithFilters(null, null, null, null, null, null, null, null);
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
      Boolean recurring,
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

    var defaultYear = Optional.ofNullable(year).orElse(LocalDate.now().getYear());

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

  /** Delete a holiday by ID. */
  public boolean delete(String id) {
    return deleteById(id);
  }

  /** Find holidays by country. */
  public List<HolidayDataDTO> findByCountry(String country) {
    return holidayRepository.findByCountryCode(country).stream().map(this::toDomainData).toList();
  }

  /** Find holidays by type. */
  public List<HolidayDataDTO> findByType(HolidayType type) {
    return holidayRepository.findByType(type).stream().map(this::toDomainData).toList();
  }

  /** Find holidays by date range. */
  public List<HolidayDataDTO> findByDateRange(LocalDate startDate, LocalDate endDate) {
    return holidayRepository.findByDateBetween(startDate, endDate).stream()
        .map(this::toDomainData)
        .toList();
  }

  /** Find holidays by year and country (for year-based calculations). */
  public List<HolidayDataDTO> findByYearAndCountry(Integer year, String country) {
    return findByCountry(country);
  }

  /** Find calculated holidays for a specific year. */
  public List<HolidayDataDTO> findCalculatedHolidays(Integer year) {
    return findAll();
  }

  /** Find base holidays (not calculated) for a country. */
  public List<HolidayDataDTO> findBaseHolidays(String country) {
    return findByCountry(country);
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

  /**
   * Find holidays for a specific year.
   *
   * @param year the target year
   * @return list of holidays for the specified year
   */
  public List<HolidayDataDTO> findHolidaysForYear(int year) {
    LocalDate startOfYear = LocalDate.of(year, 1, 1);
    LocalDate endOfYear = LocalDate.of(year, 12, 31);

    return findAllWithFilters(null, null, null, null, startOfYear, endOfYear, null, null);
  }

  /**
   * Find holidays for a specific year and country.
   *
   * @param year the target year
   * @param country the country code
   * @return list of holidays for the specified year and country
   */
  public List<HolidayDataDTO> findHolidaysForYearAndCountry(int year, String country) {
    LocalDate startOfYear = LocalDate.of(year, 1, 1);
    LocalDate endOfYear = LocalDate.of(year, 12, 31);

    return findAllWithFilters(country, null, null, null, startOfYear, endOfYear, null, null);
  }

  /**
   * Find holidays for a specific year and type.
   *
   * @param year the target year
   * @param type the holiday type
   * @return list of holidays for the specified year and type
   */
  public List<HolidayDataDTO> findHolidaysForYearAndType(int year, HolidayType type) {
    LocalDate startOfYear = LocalDate.of(year, 1, 1);
    LocalDate endOfYear = LocalDate.of(year, 12, 31);

    return findAllWithFilters(null, null, null, type, startOfYear, endOfYear, null, null);
  }

  /**
   * Find holidays for a specific year, country, and type.
   *
   * @param year the target year
   * @param country the country code
   * @param type the holiday type
   * @return list of holidays for the specified criteria
   */
  public List<HolidayDataDTO> findHolidaysForYearCountryAndType(
      int year, String country, HolidayType type) {
    LocalDate startOfYear = LocalDate.of(year, 1, 1);
    LocalDate endOfYear = LocalDate.of(year, 12, 31);

    return findAllWithFilters(country, null, null, type, startOfYear, endOfYear, null, null);
  }

  /**
   * Check if any holidays exist for a specific year.
   *
   * @param year the target year
   * @return true if holidays exist for the year
   */
  public boolean hasHolidaysForYear(int year) {
    LocalDate startOfYear = LocalDate.of(year, 1, 1);
    LocalDate endOfYear = LocalDate.of(year, 12, 31);

    Query query = new Query();
    query.addCriteria(Criteria.where("date").gte(startOfYear).lte(endOfYear));
    query.limit(1);

    return mongoTemplate.exists(query, HolidayEntity.class);
  }

  /**
   * Count holidays for a specific year.
   *
   * @param year the target year
   * @return number of holidays for the year
   */
  public long countHolidaysForYear(int year) {
    LocalDate startOfYear = LocalDate.of(year, 1, 1);
    LocalDate endOfYear = LocalDate.of(year, 12, 31);

    Query query = new Query();
    query.addCriteria(Criteria.where("date").gte(startOfYear).lte(endOfYear));

    return mongoTemplate.count(query, HolidayEntity.class);
  }

  /**
   * Find all base/template holidays (recurring holidays that can be calculated for any year).
   *
   * @return list of base holidays
   */
  public List<HolidayDataDTO> findBaseHolidays() {
    return findAllWithFilters(null, null, null, null, null, null, true, null);
  }

  /**
   * Find all calculated holidays (non-recurring holidays for specific years).
   *
   * @return list of calculated holidays
   */
  public List<HolidayDataDTO> findCalculatedHolidays() {
    return findAllWithFilters(null, null, null, null, null, null, false, null);
  }
}
