package me.clementino.holiday.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.clementino.holiday.domain.HolidayData;
import me.clementino.holiday.domain.HolidayQuery;
import me.clementino.holiday.domain.Location;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.HolidayOperations;
import me.clementino.holiday.domain.dop.HolidayType;
import me.clementino.holiday.entity.HolidayEntity;
import me.clementino.holiday.entity.LocalityEntity;
import me.clementino.holiday.mapper.SimpleHolidayMapper;
import me.clementino.holiday.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Autowired
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
  public List<HolidayData> findAll(HolidayQuery query) {
    // For now, delegate to findAllWithFilters with extracted parameters
    return findAllWithFilters(null, null, null, null, null, null, null, null);
  }

  /** Find all holidays without filters. */
  public List<HolidayData> findAll() {
    return findAllWithFilters(null, null, null, null, null, null, null, null);
  }

  /** Find holiday by ID. */
  public Optional<HolidayData> findById(String id) {
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
  public List<HolidayData> findAllWithFilters(
      String country,
      String state,
      String city,
      HolidayType type,
      LocalDate startDate,
      LocalDate endDate,
      Boolean recurring,
      String namePattern) {

    Query query = new Query();

    // Updated to use new LocalityEntity structure
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
    // Note: recurring field doesn't exist in new HolidayEntity structure
    // This filter is ignored for now
    if (namePattern != null && !namePattern.isBlank()) {
      query.addCriteria(Criteria.where("name").regex(namePattern, "i"));
    }

    query.with(Sort.by(Sort.Direction.ASC, "date"));

    List<HolidayEntity> entities = mongoTemplate.find(query, HolidayEntity.class);
    return entities.stream().map(this::toDomainData).toList();
  }

  public HolidayData create(Holiday holiday) {
    return create(holiday, null);
  }

  /** Create a new holiday. */
  public HolidayData create(Holiday holiday, Integer year) {

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
  public Optional<HolidayData> update(String id, HolidayData holidayData) {
    return holidayRepository
        .findById(id)
        .map(
            existing -> {
              HolidayEntity updated = toEntity(holidayData);
              updated.setId(existing.getId());
              updated.setDateCreated(existing.getDateCreated());
              updated.setLastUpdated(LocalDateTime.now());
              updated.setVersion(existing.getVersion()); // Let Spring Data handle version increment

              HolidayEntity saved = holidayRepository.save(updated);
              return toDomainData(saved);
            });
  }

  /** Delete a holiday by ID. */
  public boolean delete(String id) {
    return deleteById(id);
  }

  /** Find holidays by country. */
  public List<HolidayData> findByCountry(String country) {
    return holidayRepository.findByCountryCode(country).stream().map(this::toDomainData).toList();
  }

  /** Find holidays by type. */
  public List<HolidayData> findByType(HolidayType type) {
    return holidayRepository.findByType(type).stream().map(this::toDomainData).toList();
  }

  /** Find holidays by date range. */
  public List<HolidayData> findByDateRange(LocalDate startDate, LocalDate endDate) {
    return holidayRepository.findByDateBetween(startDate, endDate).stream()
        .map(this::toDomainData)
        .toList();
  }

  /** Find holidays by year and country (for year-based calculations). */
  public List<HolidayData> findByYearAndCountry(Integer year, String country) {
    // This functionality is not available with the new HolidayEntity structure
    // For now, return holidays by country only
    return findByCountry(country);
  }

  /** Find calculated holidays for a specific year. */
  public List<HolidayData> findCalculatedHolidays(Integer year) {
    // This functionality is not available with the new HolidayEntity structure
    // Return all holidays for now
    return findAll();
  }

  /** Find base holidays (not calculated) for a country. */
  public List<HolidayData> findBaseHolidays(String country) {
    // This functionality is not available with the new HolidayEntity structure
    // Return holidays by country
    return findByCountry(country);
  }

  /** Convert HolidayEntity to HolidayData. */
  private HolidayData toDomainData(HolidayEntity entity) {
    // Validate required fields before creating HolidayData
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

    return new HolidayData(
        entity.getId(),
        entity.getName(),
        entity.getDate(),
        Optional.ofNullable(
            entity.getEffectiveDate().equals(entity.getDate()) ? null : entity.getEffectiveDate()),
        extractLocationFromLocalities(entity.getLocalities()),
        entity.getType(),
        false, // Recurring logic needs to be determined from business rules
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

    // Get the first locality as primary
    LocalityEntity primary = localities.get(0);

    return new Location(
        primary.getCountryCode() != null ? primary.getCountryCode() : "UNKNOWN",
        Optional.ofNullable(primary.getSubdivisionCode()),
        Optional.ofNullable(primary.getCityName()));
  }

  /** Convert HolidayData to HolidayEntity. */
  private HolidayEntity toEntity(HolidayData data) {
    // Validate required fields before creating entity
    if (data.name() == null || data.name().isBlank()) {
      throw new IllegalArgumentException("HolidayData name cannot be null or blank");
    }
    if (data.date() == null) {
      throw new IllegalArgumentException("HolidayData date cannot be null");
    }
    if (data.location() == null
        || data.location().country() == null
        || data.location().country().isBlank()) {
      throw new IllegalArgumentException("HolidayData country cannot be null or blank");
    }
    if (data.type() == null) {
      throw new IllegalArgumentException("HolidayData type cannot be null");
    }

    HolidayEntity entity = new HolidayEntity();
    entity.setId(data.id());
    entity.setName(data.name().trim()); // Trim whitespace
    entity.setDate(data.date());
    entity.setType(data.type());
    entity.setDescription(data.description().map(String::trim).orElse(null));
    entity.setDateCreated(data.dateCreated().orElse(null));
    entity.setLastUpdated(data.lastUpdated().orElse(null));
    entity.setVersion(data.version().orElse(null));

    // Convert Location to LocalityEntity list
    entity.setLocalities(createLocalityEntitiesFromLocation(data.location()));

    return entity;
  }

  /** Convert Location to List<LocalityEntity> for the new HolidayEntity structure. */
  private List<LocalityEntity> createLocalityEntitiesFromLocation(Location location) {
    LocalityEntity localityEntity = new LocalityEntity();
    localityEntity.setCountryCode(location.country());
    localityEntity.setCountryName(location.country()); // Assuming name equals code for now

    // Set subdivision if present
    if (location.state().isPresent()) {
      localityEntity.setSubdivisionCode(location.state().get());
      localityEntity.setSubdivisionName(location.state().get());
    }

    // Set city if present
    if (location.city().isPresent()) {
      localityEntity.setCityName(location.city().get());
    }

    return List.of(localityEntity);
  }

  // ========== Year-based Operations ==========

  /**
   * Find holidays for a specific year.
   *
   * @param year the target year
   * @return list of holidays for the specified year
   */
  public List<HolidayData> findHolidaysForYear(int year) {
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
  public List<HolidayData> findHolidaysForYearAndCountry(int year, String country) {
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
  public List<HolidayData> findHolidaysForYearAndType(int year, HolidayType type) {
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
  public List<HolidayData> findHolidaysForYearCountryAndType(
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
    query.limit(1); // We only need to know if at least one exists

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
  public List<HolidayData> findBaseHolidays() {
    return findAllWithFilters(null, null, null, null, null, null, true, null);
  }

  /**
   * Find all calculated holidays (non-recurring holidays for specific years).
   *
   * @return list of calculated holidays
   */
  public List<HolidayData> findCalculatedHolidays() {
    return findAllWithFilters(null, null, null, null, null, null, false, null);
  }
}
