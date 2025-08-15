package me.clementino.holiday.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import me.clementino.holiday.domain.*;
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
 */
@Service
public class HolidayService {

  private final HolidayRepository holidayRepository;
  private final MongoTemplate mongoTemplate;
  private final HolidayOperations holidayOperations;

  @Autowired
  public HolidayService(
      HolidayRepository holidayRepository,
      MongoTemplate mongoTemplate,
      HolidayOperations holidayOperations) {
    this.holidayRepository = holidayRepository;
    this.mongoTemplate = mongoTemplate;
    this.holidayOperations = holidayOperations;
  }

  /** Find all holidays with optional filtering using DOP query object. */
  public List<HolidayData> findAll(HolidayQuery query) {
    List<Holiday> persistenceEntities = findPersistenceEntities(query);
    List<HolidayData> holidayData = persistenceEntities.stream().map(this::toDomainData).toList();

    // Apply additional filtering using pure operations
    return holidayOperations.filterHolidays(holidayData, query);
  }

  /** Legacy method for backward compatibility. */
  public List<HolidayData> findAll(
      String country,
      String state,
      String city,
      HolidayType type,
      LocalDate startDate,
      LocalDate endDate) {
    HolidayQuery query = HolidayQuery.empty();
    return findAll(query);
  }

  /** Find holiday by ID, returning domain data. */
  public HolidayData findById(String id) {
    Holiday persistenceEntity =
        holidayRepository
            .findById(id)
            .orElseThrow(() -> new HolidayNotFoundException("Holiday not found with id: " + id));
    return toDomainData(persistenceEntity);
  }

  /** Execute a create command. */
  public HolidayData executeCommand(HolidayCommand.Create command) {
    // TODO: Implement conversion from new DOP Holiday sealed interface to
    // HolidayData
    throw new UnsupportedOperationException(
        "Implementation pending - needs DOP Holiday conversion");
  }

  /** Execute an update command. */
  public HolidayData executeCommand(HolidayCommand.Update command) {
    // TODO: Implement conversion from new DOP Holiday sealed interface to
    // HolidayData
    throw new UnsupportedOperationException(
        "Implementation pending - needs DOP Holiday conversion");
  }

  /** Execute a delete command. */
  public void executeCommand(HolidayCommand.Delete command) {
    if (!holidayRepository.existsById(command.id())) {
      throw new HolidayNotFoundException("Holiday not found with id: " + command.id());
    }
    holidayRepository.deleteById(command.id());
  }

  /** Legacy save method for backward compatibility. */
  public HolidayData save(HolidayData holidayData) {
    // Generate ID if not present
    HolidayData dataWithId =
        holidayData.id() == null ? holidayData.withId(UUID.randomUUID().toString()) : holidayData;

    Holiday persistenceEntity = toPersistenceEntity(dataWithId);
    Holiday saved = holidayRepository.save(persistenceEntity);
    return toDomainData(saved);
  }

  /** Delete holiday by ID. */
  public void deleteById(String id) {
    executeCommand(HolidayCommand.Delete.of(id));
  }

  /** Check if holiday exists by ID. */
  public boolean existsById(String id) {
    return holidayRepository.existsById(id);
  }

  /** Find holidays by country using DOP query. */
  public List<HolidayData> findByCountry(String country) {
    return findAll(HolidayQuery.byCountry(country));
  }

  /** Find holidays by type using DOP query. */
  public List<HolidayData> findByType(HolidayType type) {
    return findAll(HolidayQuery.byType(type));
  }

  /** Find holidays by date range using DOP query. */
  public List<HolidayData> findByDateRange(LocalDate startDate, LocalDate endDate) {
    return findAll(HolidayQuery.byDateRange(startDate, endDate));
  }

  // Private helper methods for data conversion

  private List<Holiday> findPersistenceEntities(HolidayQuery query) {
    Query mongoQuery = new Query();

    if (query.countryCode().isPresent()) {
      mongoQuery.addCriteria(Criteria.where("country").regex(query.countryCode().get(), "i"));
    }

    if (query.subdivisionCode().isPresent()) {
      mongoQuery.addCriteria(Criteria.where("state").regex(query.subdivisionCode().get(), "i"));
    }

    if (query.cityName().isPresent()) {
      mongoQuery.addCriteria(Criteria.where("city").regex(query.cityName().get(), "i"));
    }

    if (query.type().isPresent()) {
      mongoQuery.addCriteria(Criteria.where("type").is(query.type().get()));
    }

    if (query.startDate().isPresent() && query.endDate().isPresent()) {
      mongoQuery.addCriteria(
          Criteria.where("date").gte(query.startDate().get()).lte(query.endDate().get()));
    } else if (query.startDate().isPresent()) {
      mongoQuery.addCriteria(Criteria.where("date").gte(query.startDate().get()));
    } else if (query.endDate().isPresent()) {
      mongoQuery.addCriteria(Criteria.where("date").lte(query.endDate().get()));
    }

    mongoQuery.with(Sort.by(Sort.Direction.ASC, "date", "name"));

    return mongoTemplate.find(mongoQuery, Holiday.class);
  }

  private HolidayData toDomainData(Holiday persistenceEntity) {
    Location location =
        new Location(
            persistenceEntity.getCountry() != null ? persistenceEntity.getCountry() : "Unknown",
            persistenceEntity.getState(),
            persistenceEntity.getCity());

    return new HolidayData(
        persistenceEntity.getId(),
        persistenceEntity.getName(),
        persistenceEntity.getDate(),
        Optional.ofNullable(persistenceEntity.getObserved()),
        location,
        persistenceEntity.getType(),
        persistenceEntity.isRecurring(),
        Optional.ofNullable(persistenceEntity.getDescription()),
        Optional.ofNullable(persistenceEntity.getDateCreated()),
        Optional.ofNullable(persistenceEntity.getLastUpdated()),
        Optional.ofNullable(persistenceEntity.getVersion()));
  }

  private Holiday toPersistenceEntity(HolidayData domainData) {
    Holiday entity = new Holiday();
    entity.setId(domainData.id());
    entity.setName(domainData.name());
    entity.setDate(domainData.date());
    entity.setObserved(domainData.observed().orElse(null));
    entity.setCountry(domainData.location().country());
    entity.setState(domainData.location().state().orElse(null));
    entity.setCity(domainData.location().city().orElse(null));
    entity.setType(domainData.type());
    entity.setRecurring(domainData.recurring());
    entity.setDescription(domainData.description().orElse(null));
    entity.setDateCreated(domainData.dateCreated().orElse(null));
    entity.setLastUpdated(domainData.lastUpdated().orElse(null));
    entity.setVersion(null); // Set version to null to avoid conflicts
    return entity;
  }
}
