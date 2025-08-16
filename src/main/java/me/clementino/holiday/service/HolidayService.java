package me.clementino.holiday.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.clementino.holiday.domain.*;
import me.clementino.holiday.entity.HolidayEntity;
import me.clementino.holiday.mapper.EntityMapper;
import me.clementino.holiday.operations.HolidayOperations;
import me.clementino.holiday.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

/**
 * Service layer that orchestrates operations on holiday data. Following DOP principles by
 * delegating pure operations to HolidayOperations and handling persistence concerns here.
 *
 * <p>This service now works with HolidayEntity for persistence while maintaining DOP Holiday domain
 * objects for business logic. It provides seamless conversion between entity and domain layers
 * using EntityMapper.
 */
@Service
public class HolidayService {

  private final HolidayRepository holidayRepository;
  private final MongoTemplate mongoTemplate;
  private final HolidayOperations holidayOperations;
  private final EntityMapper entityMapper;

  @Autowired
  public HolidayService(
      HolidayRepository holidayRepository,
      MongoTemplate mongoTemplate,
      HolidayOperations holidayOperations,
      EntityMapper entityMapper) {
    this.holidayRepository = holidayRepository;
    this.mongoTemplate = mongoTemplate;
    this.holidayOperations = holidayOperations;
    this.entityMapper = entityMapper;
  }

  /** Find all holidays with optional filtering using DOP query object. */
  public List<HolidayData> findAll(HolidayQuery query) {
    List<HolidayEntity> persistenceEntities = findPersistenceEntities(query);
    List<HolidayData> holidayData = persistenceEntities.stream().map(this::toDomainData).toList();

    // Apply additional filtering using pure operations
    return holidayOperations.filterHolidays(holidayData, query);
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

    if (country != null && !country.isBlank()) {
      query.addCriteria(Criteria.where("country").regex(country, "i"));
    }
    if (state != null && !state.isBlank()) {
      query.addCriteria(Criteria.where("state").regex(state, "i"));
    }
    if (city != null && !city.isBlank()) {
      query.addCriteria(Criteria.where("city").regex(city, "i"));
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
    if (recurring != null) {
      query.addCriteria(Criteria.where("recurring").is(recurring));
    }
    if (namePattern != null && !namePattern.isBlank()) {
      query.addCriteria(Criteria.where("name").regex(namePattern, "i"));
    }

    query.with(Sort.by(Sort.Direction.ASC, "date"));

    List<HolidayEntity> entities = mongoTemplate.find(query, HolidayEntity.class);
    return entities.stream().map(this::toDomainData).toList();
  }

  public HolidayData create(HolidayData holidayData) {
    HolidayEntity entity = toEntity(holidayData);
    entity.setId(UUID.randomUUID().toString());
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
              updated.setVersion(existing.getVersion());
              updated.setDateCreated(existing.getDateCreated());
              return holidayRepository.save(updated);
            })
        .map(this::toDomainData);
  }

  /** Delete a holiday by ID. */
  public boolean delete(String id) {
    if (holidayRepository.existsById(id)) {
      holidayRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /** Find holidays by country. */
  public List<HolidayData> findByCountry(String country) {
    return holidayRepository.findByCountryIgnoreCase(country).stream()
        .map(this::toDomainData)
        .toList();
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
    return holidayRepository.findByYearAndCountryIgnoreCase(year, country).stream()
        .map(this::toDomainData)
        .toList();
  }

  /** Find calculated holidays for a specific year. */
  public List<HolidayData> findCalculatedHolidays(Integer year) {
    return holidayRepository.findByYearAndIsCalculatedTrue(year).stream()
        .map(this::toDomainData)
        .toList();
  }

  /** Find base holidays (not calculated) for a country. */
  public List<HolidayData> findBaseHolidays(String country) {
    return holidayRepository.findByCountryIgnoreCaseAndIsCalculatedFalse(country).stream()
        .map(this::toDomainData)
        .toList();
  }

  // ===== PRIVATE HELPER METHODS =====

  private List<HolidayEntity> findPersistenceEntities(HolidayQuery query) {
    if (query == null || query.isEmpty()) {
      return holidayRepository.findAll(Sort.by(Sort.Direction.ASC, "date"));
    }

    Query mongoQuery = new Query();

    // Add country filter
    query
        .country()
        .ifPresent(
            country -> mongoQuery.addCriteria(Criteria.where("country").regex(country, "i")));

    // Add type filter
    query.type().ifPresent(type -> mongoQuery.addCriteria(Criteria.where("type").is(type)));

    // Add date range filters
    query
        .startDate()
        .ifPresent(startDate -> mongoQuery.addCriteria(Criteria.where("date").gte(startDate)));
    query
        .endDate()
        .ifPresent(endDate -> mongoQuery.addCriteria(Criteria.where("date").lte(endDate)));

    // Add year filter
    query.year().ifPresent(year -> mongoQuery.addCriteria(Criteria.where("year").is(year)));

    mongoQuery.with(Sort.by(Sort.Direction.ASC, "date"));

    return mongoTemplate.find(mongoQuery, HolidayEntity.class);
  }

  private HolidayData toDomainData(HolidayEntity entity) {
    return new HolidayData(
        entity.getId(),
        entity.getName(),
        entity.getDate(),
        new Location(
            entity.getCountry(),
            Optional.ofNullable(entity.getState()),
            Optional.ofNullable(entity.getCity())),
        entity.getType(),
        entity.isRecurring(),
        Optional.ofNullable(entity.getDescription()));
  }

  private HolidayEntity toEntity(HolidayData holidayData) {
    HolidayEntity entity = new HolidayEntity();
    entity.setName(holidayData.name());
    entity.setDescription(holidayData.description().orElse(""));
    entity.setDate(holidayData.date());
    entity.setCountry(holidayData.location().country());
    entity.setState(holidayData.location().state().orElse(null));
    entity.setCity(holidayData.location().city().orElse(null));
    entity.setType(holidayData.type());
    entity.setRecurring(holidayData.recurring());
    return entity;
  }
}
