package me.clementino.holiday.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import me.clementino.holiday.domain.HolidayCommand;
import me.clementino.holiday.domain.HolidayData;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.dto.CreateHolidayRequest;
import me.clementino.holiday.dto.HolidayResponse;
import me.clementino.holiday.dto.UpdateHolidayRequest;
import me.clementino.holiday.mapper.HolidayMapper;
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

@RestController
@RequestMapping("/api/holidays")
@Tag(
    name = "Holiday API",
    description = "Operations for managing holidays using Data-Oriented Programming principles")
public class HolidayController {

  private final HolidayService holidayService;
  private final HolidayMapper holidayMapper;

  @Autowired
  public HolidayController(HolidayService holidayService, HolidayMapper holidayMapper) {
    this.holidayService = holidayService;
    this.holidayMapper = holidayMapper;
  }

  @GetMapping
  @Operation(
      summary = "Get all holidays",
      description = "Retrieve all holidays with optional filtering using DOP query patterns")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved holidays")
  public ResponseEntity<List<HolidayResponse>> getAllHolidays(
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

    // Use DOP approach with HolidayData
    List<HolidayData> holidays =
        holidayService.findAll(country, state, city, type, startDate, endDate);

    // Convert to response DTOs
    List<HolidayResponse> response = holidays.stream().map(holidayMapper::toResponse).toList();

    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get holiday by ID", description = "Retrieve a specific holiday by its ID")
  @ApiResponse(responseCode = "200", description = "Successfully retrieved holiday")
  @ApiResponse(responseCode = "404", description = "Holiday not found")
  public ResponseEntity<HolidayResponse> getHolidayById(
      @Parameter(description = "Holiday ID") @PathVariable String id) {

    HolidayData holiday = holidayService.findById(id);
    HolidayResponse response = holidayMapper.toResponse(holiday);

    return ResponseEntity.ok(response);
  }

  @PostMapping
  @Operation(
      summary = "Create a new holiday",
      description = "Create a new holiday using DOP command pattern")
  @ApiResponse(responseCode = "201", description = "Holiday created successfully")
  @ApiResponse(responseCode = "400", description = "Invalid input data")
  public ResponseEntity<HolidayResponse> createHoliday(
      @Valid @RequestBody CreateHolidayRequest request) {

    // TODO: Implement conversion from new DTO structure to domain objects
    // This will need to be updated to work with the new DOP Holiday sealed
    // interface
    throw new UnsupportedOperationException(
        "Implementation pending - needs DOP Holiday conversion");
  }

  @PutMapping("/{id}")
  @Operation(
      summary = "Update a holiday",
      description = "Update an existing holiday using DOP command pattern")
  @ApiResponse(responseCode = "200", description = "Holiday updated successfully")
  @ApiResponse(responseCode = "404", description = "Holiday not found")
  @ApiResponse(responseCode = "400", description = "Invalid input data")
  public ResponseEntity<HolidayResponse> updateHoliday(
      @Parameter(description = "Holiday ID") @PathVariable String id,
      @Valid @RequestBody UpdateHolidayRequest request) {

    // TODO: Implement conversion from new DTO structure to domain objects
    // This will need to be updated to work with the new DOP Holiday sealed
    // interface
    throw new UnsupportedOperationException(
        "Implementation pending - needs DOP Holiday conversion");
  }

  @DeleteMapping("/{id}")
  @Operation(
      summary = "Delete a holiday",
      description = "Delete an existing holiday using DOP command pattern")
  @ApiResponse(responseCode = "204", description = "Holiday deleted successfully")
  @ApiResponse(responseCode = "404", description = "Holiday not found")
  public ResponseEntity<Void> deleteHoliday(
      @Parameter(description = "Holiday ID") @PathVariable String id) {

    // Create and execute DOP delete command
    HolidayCommand.Delete command = HolidayCommand.Delete.of(id);
    holidayService.executeCommand(command);

    return ResponseEntity.noContent().build();
  }
}
