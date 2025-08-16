package me.clementino.holiday.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.clementino.holiday.domain.dop.Holiday;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

/**
 * MongoDB converters for serializing and deserializing DOP Holiday sealed interface variants. These
 * converters handle the persistence of sealed interface implementations to/from JSON strings.
 */
public class HolidayVariantConverter {

  /** Converter for writing Holiday sealed interface to JSON string for MongoDB storage. */
  @WritingConverter
  public static class HolidayToStringConverter implements Converter<Holiday, String> {

    private final ObjectMapper objectMapper;

    public HolidayToStringConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public String convert(@NonNull Holiday holiday) {
      try {
        return objectMapper.writeValueAsString(holiday);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to serialize Holiday to JSON", e);
      }
    }
  }

  /**
   * Converter for reading JSON string from MongoDB and deserializing to Holiday sealed interface.
   */
  @ReadingConverter
  public static class StringToHolidayConverter implements Converter<String, Holiday> {

    private final ObjectMapper objectMapper;

    public StringToHolidayConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public Holiday convert(@NonNull String json) {
      try {
        return objectMapper.readValue(json, Holiday.class);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to deserialize JSON to Holiday", e);
      }
    }
  }
}
