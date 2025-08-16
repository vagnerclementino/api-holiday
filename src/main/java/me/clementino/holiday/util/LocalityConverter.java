package me.clementino.holiday.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.clementino.holiday.domain.dop.Locality;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

/**
 * MongoDB converters for serializing and deserializing DOP Locality sealed interface variants.
 * These converters handle the persistence of sealed interface implementations to/from JSON strings.
 */
public class LocalityConverter {

  /** Converter for writing Locality sealed interface to JSON string for MongoDB storage. */
  @WritingConverter
  public static class LocalityToStringConverter implements Converter<Locality, String> {

    private final ObjectMapper objectMapper;

    public LocalityToStringConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public String convert(@NonNull Locality locality) {
      try {
        return objectMapper.writeValueAsString(locality);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to serialize Locality to JSON", e);
      }
    }
  }

  /**
   * Converter for reading JSON string from MongoDB and deserializing to Locality sealed interface.
   */
  @ReadingConverter
  public static class StringToLocalityConverter implements Converter<String, Locality> {

    private final ObjectMapper objectMapper;

    public StringToLocalityConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public Locality convert(@NonNull String json) {
      try {
        return objectMapper.readValue(json, Locality.class);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to deserialize JSON to Locality", e);
      }
    }
  }
}
