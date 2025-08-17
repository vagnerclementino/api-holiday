package me.clementino.holiday.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import me.clementino.holiday.domain.HolidayData;
import me.clementino.holiday.domain.Location;
import me.clementino.holiday.domain.dop.HolidayType;
import me.clementino.holiday.dto.CreateHolidayRequest;
import me.clementino.holiday.dto.HolidayResponseDTO;
import me.clementino.holiday.dto.UpdateHolidayRequest;
import me.clementino.holiday.mapper.HolidayCreationMapper;
import me.clementino.holiday.mapper.SimpleHolidayMapper;
import me.clementino.holiday.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for Holiday API operations using Data-Oriented Programming principles.
 *
 * <p>This controller demonstrates DOP principles by:
 *
 * <ul>
 *   <li>Separating operations from data - business logic is in the service layer
 *   <li>Using immutable data structures (records) for requests and responses
 *   <li>Clear data transformation between layers
 * </ul>
 */
@RestController
@RequestMapping("/api/holidays")
@Tag(
    name = "Holiday API",
    description = "Operations for managing holidays using Data-Oriented Programming principles")
public class HolidayController {

  private final HolidayService holidayService;
  private final SimpleHolidayMapper holidayMapper;
  private final HolidayCreationMapper creationMapper;

  @Autowired
  public HolidayController(
      HolidayService holidayService,
      SimpleHolidayMapper holidayMapper,
      HolidayCreationMapper creationMapper) {
    this.holidayService = holidayService;
    this.holidayMapper = holidayMapper;
    this.creationMapper = creationMapper;
  }

  @GetMapping
  @Operation(
      summary = "Get all holidays",
      description = "Retrieve all holidays with optional filtering using DOP query patterns")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved holidays")
  public ResponseEntity<List<HolidayResponseDTO>> getAllHolidays(
      @Parameter(description = "Filter by country") @RequestParam(required = false) String country,
      @Parameter(description = "Filter by state") @RequestParam(required = false) String state,
      @Parameter(description = "Filter by city") @RequestParam(required = false) String city,
      @Parameter(description = "Filter by holiday type") @RequestParam(required = false)
          HolidayType type,
      @Parameter(description = "Filter by start date (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate startDate,
      @Parameter(description = "Filter by end date (yyyy-MM-dd)")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate endDate,
      @Parameter(description = "Filter by recurring holidays") @RequestParam(required = false)
          Boolean recurring,
      @Parameter(description = "Filter by name pattern") @RequestParam(required = false)
          String namePattern) {

    try {
      // Use service to find holidays with filters
      List<HolidayData> holidays =
          holidayService.findAllWithFilters(
              country, state, city, type, startDate, endDate, recurring, namePattern);

      // Convert to response DTOs
      List<HolidayResponseDTO> responses =
          holidays.stream().map(holidayMapper::toResponse).toList();

      return ResponseEntity.ok(responses);
    } catch (Exception e) {
      // Return empty list on error to prevent 500
      return ResponseEntity.ok(List.of());
    }
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get holiday by ID", description = "Retrieve a specific holiday by its ID")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved holiday")
  @ApiResponse(responseCode = "404", description = "Holiday not found")
  public ResponseEntity<HolidayResponseDTO> getHolidayById(
      @Parameter(description = "Holiday ID") @PathVariable String id) {

    try {
      Optional<HolidayData> holiday = holidayService.findById(id);

      if (holiday.isEmpty()) {
        return ResponseEntity.notFound().build();
      }

      HolidayResponseDTO response = holidayMapper.toResponse(holiday.get());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }

  @PostMapping
  @Operation(
      summary = "Create a new holiday",
      description = "Create a new holiday using DOP principles")
  @ApiResponse(responseCode = "201", description = "Holiday created successfully")
  @ApiResponse(responseCode = "400", description = "Invalid input data")
  public ResponseEntity<HolidayResponseDTO> createHoliday(
      @Valid @RequestBody CreateHolidayRequest request) {

    try {
      var holiday = creationMapper.toHoliday(request);
      var createdHoliday = holidayService.create(holiday);
      var response = holidayMapper.toResponse(createdHoliday);
      return ResponseEntity.status(201).body(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update a holiday",
      description = "Update an existing holiday using DOP principles")
  @ApiResponse(responseCode = "200", description = "Holiday updated successfully")
  @ApiResponse(responseCode = "404", description = "Holiday not found")
  @ApiResponse(responseCode = "400", description = "Invalid input data")
  public ResponseEntity<HolidayResponseDTO> updateHoliday(
      @Parameter(description = "Holiday ID") @PathVariable String id,
      @Valid @RequestBody UpdateHolidayRequest request) {

    try {
      // Check if holiday exists
      Optional<HolidayData> existingHoliday = holidayService.findById(id);
      if (existingHoliday.isEmpty()) {
        return ResponseEntity.notFound().build();
      }

      // Create updated HolidayData with merged values (null means keep current value)
      HolidayData current = existingHoliday.get();
      HolidayData updated =
          new HolidayData(
              id, // Keep same ID
              request.name() != null ? request.name() : current.name(),
              request.date() != null ? request.date() : current.date(),
              request.observed() != null ? Optional.of(request.observed()) : current.observed(),
              new Location(
                  request.country() != null ? request.country() : current.location().country(),
                  request.state() != null
                      ? Optional.of(request.state())
                      : current.location().state(),
                  request.city() != null ? Optional.of(request.city()) : current.location().city()),
              request.type() != null ? request.type() : current.type(),
              request.recurring() != null ? request.recurring() : current.recurring(),
              request.description() != null
                  ? Optional.of(request.description())
                  : current.description(),
              current.dateCreated(), // Preserve creation date
              Optional.empty(), // lastUpdated will be set by service
              Optional.empty() // Let service manage version control
              );

      // Update using service
      Optional<HolidayData> result = holidayService.update(id, updated);

      if (result.isEmpty()) {
        return ResponseEntity.notFound().build();
      }

      HolidayResponseDTO response = holidayMapper.toResponse(result.get());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a holiday", description = "Delete an existing holiday by ID")
  @ApiResponse(responseCode = "204", description = "Holiday deleted successfully")
  @ApiResponse(responseCode = "404", description = "Holiday not found")
  public ResponseEntity<Void> deleteHoliday(
      @Parameter(description = "Holiday ID") @PathVariable String id) {

    try {
      boolean deleted = holidayService.deleteById(id);

      if (!deleted) {
        return ResponseEntity.notFound().build();
      }

      return ResponseEntity.noContent().build();
    } catch (Exception e) {
      return ResponseEntity.notFound().build();
    }
  }
}
