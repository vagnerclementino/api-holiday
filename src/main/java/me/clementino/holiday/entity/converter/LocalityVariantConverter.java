package me.clementino.holiday.entity.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.clementino.holiday.domain.dop.Locality;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

/**
 * Custom MongoDB converters for DOP Locality sealed interface serialization.
 *
 * <p>These converters handle the serialization and deserialization of DOP Locality sealed interface
 * variants to/from MongoDB documents. They preserve the hierarchical locality structure while
 * enabling efficient storage and querying.
 *
 * <p><strong>Key Features:</strong>
 *
 * <ul>
 *   <li>Type-safe serialization of locality variants (Country, Subdivision, City)
 *   <li>Automatic type detection during deserialization
 *   <li>JSON-based storage format for flexibility
 *   <li>Hierarchical locality structure preservation
 * </ul>
 */
public class LocalityVariantConverter {

  private static final Logger logger = LoggerFactory.getLogger(LocalityVariantConverter.class);

  /** Converter for writing DOP Locality objects to MongoDB. */
  @Component
  @WritingConverter
  public static class LocalityToStringConverter implements Converter<Locality, String> {

    private final ObjectMapper objectMapper;

    public LocalityToStringConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public String convert(Locality source) {
      try {
        // Create a wrapper object that includes type information
        LocalityWrapper wrapper = new LocalityWrapper();
        wrapper.type = source.getClass().getSimpleName();
        wrapper.data = objectMapper.writeValueAsString(source);

        return objectMapper.writeValueAsString(wrapper);
      } catch (JsonProcessingException e) {
        logger.error("Failed to serialize Locality object: {}", source, e);
        throw new RuntimeException("Failed to serialize Locality object", e);
      }
    }
  }

  /** Converter for reading DOP Locality objects from MongoDB. */
  @Component
  @ReadingConverter
  public static class StringToLocalityConverter implements Converter<String, Locality> {

    private final ObjectMapper objectMapper;

    public StringToLocalityConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public Locality convert(String source) {
      try {
        // First, deserialize the wrapper to get type information
        LocalityWrapper wrapper = objectMapper.readValue(source, LocalityWrapper.class);

        // Then deserialize the actual locality object based on type
        return switch (wrapper.type) {
          case "Country" -> objectMapper.readValue(wrapper.data, Locality.Country.class);
          case "Subdivision" -> objectMapper.readValue(wrapper.data, Locality.Subdivision.class);
          case "City" -> objectMapper.readValue(wrapper.data, Locality.City.class);
          default -> {
            logger.error("Unknown locality type: {}", wrapper.type);
            throw new IllegalArgumentException("Unknown locality type: " + wrapper.type);
          }
        };
      } catch (JsonProcessingException e) {
        logger.error("Failed to deserialize Locality object from: {}", source, e);
        throw new RuntimeException("Failed to deserialize Locality object", e);
      }
    }
  }

  /** Wrapper class to store type information along with serialized data. */
  private static class LocalityWrapper {
    public String type;
    public String data;
  }
}
