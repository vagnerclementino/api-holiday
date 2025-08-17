package me.clementino.holiday.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.HolidayOperations;
import me.clementino.holiday.dto.CreateHolidayRequest;
import me.clementino.holiday.dto.HolidayFilter;
import me.clementino.holiday.dto.UpdateHolidayRequest;
import me.clementino.holiday.entity.HolidayEntity;
import me.clementino.holiday.mapper.HolidayDomainMapper;
import me.clementino.holiday.mapper.HolidayEntityMapper;
import me.clementino.holiday.repository.HolidayRepository;
import org.springframework.stereotype.Service;

/**
 * Holiday service following DOP principles.
 *
 * <p>This service orchestrates the flow: 1. Receives DTOs from controllers 2. Converts to domain
 * objects using mappers 3. Applies business logic using HolidayOperations 4. Converts to entities
 * and persists 5. Returns domain objects back to controllers
 */
@Service
public class HolidayService {

  private final HolidayRepository holidayRepository;
  private final HolidayDomainMapper domainMapper;
  private final HolidayEntityMapper entityMapper;
  private final HolidayOperations holidayOperations;

  public HolidayService(
      HolidayRepository holidayRepository,
      HolidayDomainMapper domainMapper,
      HolidayEntityMapper entityMapper,
      HolidayOperations holidayOperations) {
    this.holidayRepository = holidayRepository;
    this.domainMapper = domainMapper;
    this.entityMapper = entityMapper;
    this.holidayOperations = holidayOperations;
  }

  /**
   * Create a new holiday following DOP flow.
   *
   * @param request the create request DTO
   * @return created Holiday domain object
   */
  public Holiday create(CreateHolidayRequest request) {
    // 1. Convert DTO to domain object
    Holiday holiday = domainMapper.toHoliday(request);

    // 2. Apply business logic - calculate observed date
    Holiday holidayWithObservedDate = holidayOperations.calculateObservedDate(holiday);

    // 3. Validate the holiday
    var validationResult = holidayOperations.validateHoliday(holidayWithObservedDate);
    if (validationResult instanceof HolidayOperations.ValidationResult.Failure failure) {
      throw new IllegalArgumentException("Invalid holiday: " + failure.message());
    }

    // 4. Add timestamps
    Holiday holidayWithTimestamps =
        holidayWithObservedDate
            .withDateCreated(Optional.of(LocalDateTime.now()))
            .withLastUpdated(Optional.of(LocalDateTime.now()));

    // 5. Convert to entity and persist
    HolidayEntity entity = entityMapper.toEntity(holidayWithTimestamps);
    HolidayEntity savedEntity = holidayRepository.save(entity);

    // 6. Convert back to domain object
    return entityMapper.toDomain(savedEntity);
  }

  /**
   * Update an existing holiday following DOP flow.
   *
   * @param id the holiday ID
   * @param request the update request DTO
   * @return updated Holiday domain object
   */
  public Holiday update(String id, UpdateHolidayRequest request) {
    // 1. Check if holiday exists
    HolidayEntity existingEntity =
        holidayRepository
            .findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Holiday not found: " + id));

    // 2. Convert DTO to domain object with existing ID
    Holiday holiday = domainMapper.toHoliday(request, id);

    // 3. Apply business logic - calculate observed date
    Holiday holidayWithObservedDate = holidayOperations.calculateObservedDate(holiday);

    // 4. Validate the holiday
    var validationResult = holidayOperations.validateHoliday(holidayWithObservedDate);
    if (validationResult instanceof HolidayOperations.ValidationResult.Failure failure) {
      throw new IllegalArgumentException("Invalid holiday: " + failure.message());
    }

    // 5. Preserve creation date, update last updated
    Holiday holidayWithTimestamps =
        holidayWithObservedDate
            .withDateCreated(Optional.of(existingEntity.getDateCreated()))
            .withLastUpdated(Optional.of(LocalDateTime.now()));

    // 6. Convert to entity and persist
    HolidayEntity entity = entityMapper.toEntity(holidayWithTimestamps);
    HolidayEntity savedEntity = holidayRepository.save(entity);

    // 7. Convert back to domain object
    return entityMapper.toDomain(savedEntity);
  }

  /**
   * Find holiday by ID.
   *
   * @param id the holiday ID
   * @return Holiday domain object if found
   */
  public Optional<Holiday> findById(String id) {
    return holidayRepository.findById(id).map(entityMapper::toDomain);
  }

  /**
   * Find all holidays with optional filtering.
   *
   * @param filter the filter criteria
   * @return list of Holiday domain objects
   */
  public List<Holiday> findAll(HolidayFilter filter) {
    List<HolidayEntity> entities;

    if (hasFilters(filter)) {
      entities = findWithFilters(filter);
    } else {
      entities = holidayRepository.findAll();
    }

    return entities.stream().map(entityMapper::toDomain).toList();
  }

  /**
   * Delete holiday by ID.
   *
   * @param id the holiday ID
   * @return true if deleted, false if not found
   */
  public boolean deleteById(String id) {
    if (holidayRepository.existsById(id)) {
      holidayRepository.deleteById(id);
      return true;
    }
    return false;
  }

  private boolean hasFilters(HolidayFilter filter) {
    return filter.country() != null
        || filter.state() != null
        || filter.city() != null
        || filter.type() != null
        || filter.startDate() != null
        || filter.endDate() != null;
  }

  private List<HolidayEntity> findWithFilters(HolidayFilter filter) {
    // Start with all holidays
    List<HolidayEntity> results = holidayRepository.findAll();

    // Apply filters
    if (filter.country() != null) {
      results =
          results.stream().filter(h -> h.getCountry().equalsIgnoreCase(filter.country())).toList();
    }

    if (filter.state() != null) {
      results =
          results.stream().filter(h -> filter.state().equalsIgnoreCase(h.getState())).toList();
    }

    if (filter.city() != null) {
      results = results.stream().filter(h -> filter.city().equalsIgnoreCase(h.getCity())).toList();
    }

    if (filter.type() != null) {
      results = results.stream().filter(h -> h.getType() == filter.type()).toList();
    }

    if (filter.startDate() != null) {
      results = results.stream().filter(h -> !h.getDate().isBefore(filter.startDate())).toList();
    }

    if (filter.endDate() != null) {
      results = results.stream().filter(h -> !h.getDate().isAfter(filter.endDate())).toList();
    }

    return results;
  }
}
