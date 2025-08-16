package me.clementino.holiday.entity.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.clementino.holiday.domain.dop.FixedHoliday;
import me.clementino.holiday.domain.dop.Holiday;
import me.clementino.holiday.domain.dop.MoveableFromBaseHoliday;
import me.clementino.holiday.domain.dop.MoveableHoliday;
import me.clementino.holiday.domain.dop.ObservedHoliday;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

/**
 * Custom MongoDB converters for DOP Holiday sealed interface serialization.
 *
 * <p>These converters handle the complex task of serializing and deserializing DOP Holiday sealed
 * interface variants to/from MongoDB documents. They preserve the exact type information and data
 * structure while enabling efficient storage and retrieval.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li>Type-safe serialization of sealed interface variants
 *   <li>Automatic type detection during deserialization
 *   <li>JSON-based storage format for flexibility
 *   <li>Error handling and logging for debugging
 * </ul>
 */
public class HolidayVariantConverter {

  private static final Logger logger = LoggerFactory.getLogger(HolidayVariantConverter.class);

  /** Converter for writing DOP Holiday objects to MongoDB. */
  @Component
  @WritingConverter
  public static class HolidayToStringConverter implements Converter<Holiday, String> {

    private final ObjectMapper objectMapper;

    public HolidayToStringConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public String convert(Holiday source) {
      try {
        // Create a wrapper object that includes type information
        HolidayWrapper wrapper = new HolidayWrapper();
        wrapper.type = source.getClass().getSimpleName();
        wrapper.data = objectMapper.writeValueAsString(source);

        return objectMapper.writeValueAsString(wrapper);
      } catch (JsonProcessingException e) {
        logger.error("Failed to serialize Holiday object: {}", source, e);
        throw new RuntimeException("Failed to serialize Holiday object", e);
      }
    }
  }

  /** Converter for reading DOP Holiday objects from MongoDB. */
  @Component
  @ReadingConverter
  public static class StringToHolidayConverter implements Converter<String, Holiday> {

    private final ObjectMapper objectMapper;

    public StringToHolidayConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public Holiday convert(String source) {
      try {
        // First, deserialize the wrapper to get type information
        HolidayWrapper wrapper = objectMapper.readValue(source, HolidayWrapper.class);

        // Then deserialize the actual holiday object based on type
        return switch (wrapper.type) {
          case "FixedHoliday" -> objectMapper.readValue(wrapper.data, FixedHoliday.class);
          case "ObservedHoliday" -> objectMapper.readValue(wrapper.data, ObservedHoliday.class);
          case "MoveableHoliday" -> objectMapper.readValue(wrapper.data, MoveableHoliday.class);
          case "MoveableFromBaseHoliday" ->
              objectMapper.readValue(wrapper.data, MoveableFromBaseHoliday.class);
          default -> {
            logger.error("Unknown holiday type: {}", wrapper.type);
            throw new IllegalArgumentException("Unknown holiday type: " + wrapper.type);
          }
        };
      } catch (JsonProcessingException e) {
        logger.error("Failed to deserialize Holiday object from: {}", source, e);
        throw new RuntimeException("Failed to deserialize Holiday object", e);
      }
    }
  }

  /** Wrapper class to store type information along with serialized data. */
  private static class HolidayWrapper {
    public String type;
    public String data;
  }
}
