package me.clementino.holiday.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import me.clementino.holiday.domain.Holiday;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.dto.CreateHolidayRequest;
import me.clementino.holiday.dto.HolidayResponse;
import me.clementino.holiday.dto.UpdateHolidayRequest;
import me.clementino.holiday.mapper.HolidayMapper;
import me.clementino.holiday.service.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/holidays")
@Tag(name = "Holiday API", description = "Operations for managing holidays")
public class HolidayController {

    private final HolidayService holidayService;
    private final HolidayMapper holidayMapper;

    @Autowired
    public HolidayController(HolidayService holidayService, HolidayMapper holidayMapper) {
        this.holidayService = holidayService;
        this.holidayMapper = holidayMapper;
    }

    @GetMapping
    @Operation(summary = "Get all holidays", description = "Retrieve all holidays with optional filtering")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved holidays")
    public ResponseEntity<List<HolidayResponse>> getAllHolidays(
            @Parameter(description = "Filter by country")
            @RequestParam(required = false) String country,
            
            @Parameter(description = "Filter by state")
            @RequestParam(required = false) String state,
            
            @Parameter(description = "Filter by city")
            @RequestParam(required = false) String city,
            
            @Parameter(description = "Filter by holiday type")
            @RequestParam(required = false) HolidayType type,
            
            @Parameter(description = "Filter by start date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            
            @Parameter(description = "Filter by end date (yyyy-MM-dd)")
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        List<Holiday> holidays = holidayService.findAll(country, state, city, type, startDate, endDate);
        List<HolidayResponse> response = holidayMapper.toResponseList(holidays);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get holiday by ID", description = "Retrieve a specific holiday by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved holiday")
    @ApiResponse(responseCode = "404", description = "Holiday not found")
    public ResponseEntity<HolidayResponse> getHolidayById(
            @Parameter(description = "Holiday ID") @PathVariable String id) {  // Changed UUID to String
        
        Holiday holiday = holidayService.findById(id);
        HolidayResponse response = holidayMapper.toResponse(holiday);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new holiday", description = "Create a new holiday")
    @ApiResponse(responseCode = "201", description = "Holiday created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<HolidayResponse> createHoliday(
            @Valid @RequestBody CreateHolidayRequest request) {
        
        Holiday holiday = holidayMapper.toEntity(request);
        Holiday savedHoliday = holidayService.save(holiday);
        HolidayResponse response = holidayMapper.toResponse(savedHoliday);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a holiday", description = "Update an existing holiday")
    @ApiResponse(responseCode = "200", description = "Holiday updated successfully")
    @ApiResponse(responseCode = "404", description = "Holiday not found")
    @ApiResponse(responseCode = "400", description = "Invalid input data")
    public ResponseEntity<HolidayResponse> updateHoliday(
            @Parameter(description = "Holiday ID") @PathVariable String id,  // Changed UUID to String
            @Valid @RequestBody UpdateHolidayRequest request) {
        
        Holiday existingHoliday = holidayService.findById(id);
        holidayMapper.updateEntity(existingHoliday, request);
        Holiday updatedHoliday = holidayService.save(existingHoliday);
        HolidayResponse response = holidayMapper.toResponse(updatedHoliday);
        
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a holiday", description = "Delete an existing holiday")
    @ApiResponse(responseCode = "204", description = "Holiday deleted successfully")
    @ApiResponse(responseCode = "404", description = "Holiday not found")
    public ResponseEntity<Void> deleteHoliday(
            @Parameter(description = "Holiday ID") @PathVariable String id) {  // Changed UUID to String
        
        holidayService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
