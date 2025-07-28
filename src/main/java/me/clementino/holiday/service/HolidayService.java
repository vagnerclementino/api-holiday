package me.clementino.holiday.service;

import me.clementino.holiday.domain.Holiday;
import me.clementino.holiday.domain.HolidayType;
import me.clementino.holiday.repository.HolidayRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HolidayService {

    private final HolidayRepository holidayRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public HolidayService(HolidayRepository holidayRepository, MongoTemplate mongoTemplate) {
        this.holidayRepository = holidayRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Find all holidays with optional filtering.
     */
    public List<Holiday> findAll(String country, String state, String city, 
                                HolidayType type, LocalDate startDate, LocalDate endDate) {
        
        Query query = new Query();
        
        if (country != null && !country.trim().isEmpty()) {
            query.addCriteria(Criteria.where("country").regex(country, "i"));
        }
        
        if (state != null && !state.trim().isEmpty()) {
            query.addCriteria(Criteria.where("state").regex(state, "i"));
        }
        
        if (city != null && !city.trim().isEmpty()) {
            query.addCriteria(Criteria.where("city").regex(city, "i"));
        }
        
        if (type != null) {
            query.addCriteria(Criteria.where("type").is(type));
        }
        
        if (startDate != null && endDate != null) {
            query.addCriteria(Criteria.where("date").gte(startDate).lte(endDate));
        } else if (startDate != null) {
            query.addCriteria(Criteria.where("date").gte(startDate));
        } else if (endDate != null) {
            query.addCriteria(Criteria.where("date").lte(endDate));
        }
        
        query.with(Sort.by(Sort.Direction.ASC, "date", "name"));
        
        return mongoTemplate.find(query, Holiday.class);
    }

    /**
     * Find holiday by ID.
     */
    public Holiday findById(String id) {
        return holidayRepository.findById(id)
                .orElseThrow(() -> new HolidayNotFoundException("Holiday not found with id: " + id));
    }

    /**
     * Save a holiday.
     */
    public Holiday save(Holiday holiday) {
        return holidayRepository.save(holiday);
    }

    /**
     * Delete holiday by ID.
     */
    public void deleteById(String id) {
        if (!holidayRepository.existsById(id)) {
            throw new HolidayNotFoundException("Holiday not found with id: " + id);
        }
        holidayRepository.deleteById(id);
    }

    /**
     * Check if holiday exists by ID.
     */
    public boolean existsById(String id) {
        return holidayRepository.existsById(id);
    }

    /**
     * Find holidays by country.
     */
    public List<Holiday> findByCountry(String country) {
        return holidayRepository.findByCountryIgnoreCase(country);
    }

    /**
     * Find holidays by type.
     */
    public List<Holiday> findByType(HolidayType type) {
        return holidayRepository.findByType(type);
    }

    /**
     * Find holidays by date range.
     */
    public List<Holiday> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return holidayRepository.findByDateBetween(startDate, endDate);
    }
}
