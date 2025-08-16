package me.clementino.holiday.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import me.clementino.holiday.entity.converter.HolidayVariantConverter;
import me.clementino.holiday.entity.converter.LocalityVariantConverter;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.lang.NonNull;

/**
 * MongoDB configuration for DOP Holiday API.
 *
 * <p>This configuration enables MongoDB auditing and registers custom converters for DOP sealed
 * interface serialization. The converters handle the complex task of persisting sealed interface
 * variants while maintaining type safety and data integrity.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li>MongoDB auditing for automatic timestamp management
 *   <li>Custom converters for DOP Holiday sealed interface variants
 *   <li>Custom converters for DOP Locality sealed interface variants
 *   <li>ObjectId to String conversion for simplified ID handling
 * </ul>
 */
@Configuration
@EnableMongoAuditing
public class MongoConfig {

  /**
   * Registers custom MongoDB converters for DOP sealed interface serialization.
   *
   * @param objectMapper Jackson ObjectMapper for JSON serialization
   * @return MongoCustomConversions with all custom converters
   */
  @Bean
  public MongoCustomConversions customConversions(ObjectMapper objectMapper) {
    return new MongoCustomConversions(
        Arrays.asList(
            // DOP Holiday sealed interface converters
            new HolidayVariantConverter.HolidayToStringConverter(objectMapper),
            new HolidayVariantConverter.StringToHolidayConverter(objectMapper),

            // DOP Locality sealed interface converters
            new LocalityVariantConverter.LocalityToStringConverter(objectMapper),
            new LocalityVariantConverter.StringToLocalityConverter(objectMapper),

            // ObjectId converters for simplified ID handling
            new ObjectIdToStringConverter(),
            new StringToObjectIdConverter()));
  }

  /** Converter from ObjectId to String */
  public static class ObjectIdToStringConverter implements Converter<ObjectId, String> {
    @Override
    public String convert(@NonNull ObjectId objectId) {
      return objectId.toHexString();
    }
  }

  /** Converter from String to ObjectId */
  public static class StringToObjectIdConverter implements Converter<String, ObjectId> {
    @Override
    public ObjectId convert(@NonNull String source) {
      return ObjectId.isValid(source) ? new ObjectId(source) : null;
    }
  }
}
