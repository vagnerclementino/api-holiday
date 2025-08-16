package me.clementino.holiday.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.clementino.holiday.domain.LocalityEntity;
import me.clementino.holiday.domain.dop.Locality;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

/**
 * MongoDB converters for LocalityEntity that handle both embedded document structure and DOP
 * Locality sealed interface serialization.
 */
public class LocalityEntityConverter {

  /**
   * Converter for writing LocalityEntity to MongoDB document. Automatically serializes the DOP
   * Locality data if present.
   */
  @WritingConverter
  public static class LocalityEntityToDocumentConverter
      implements Converter<LocalityEntity, org.bson.Document> {

    private final ObjectMapper objectMapper;

    public LocalityEntityToDocumentConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public org.bson.Document convert(@NonNull LocalityEntity entity) {
      org.bson.Document doc = new org.bson.Document();

      doc.put("countryCode", entity.getCountryCode());
      doc.put("countryName", entity.getCountryName());
      doc.put("subdivisionCode", entity.getSubdivisionCode());
      doc.put("subdivisionName", entity.getSubdivisionName());
      doc.put("cityName", entity.getCityName());
      doc.put("localityType", entity.getLocalityType().name());

      // Serialize DOP Locality data if not already present
      if (entity.getDopLocalityData() == null) {
        try {
          Locality dopLocality = entity.toDopLocality();
          String serializedData = objectMapper.writeValueAsString(dopLocality);
          doc.put("dopLocalityData", serializedData);
        } catch (JsonProcessingException e) {
          throw new RuntimeException("Failed to serialize DOP Locality data", e);
        }
      } else {
        doc.put("dopLocalityData", entity.getDopLocalityData());
      }

      return doc;
    }
  }

  /**
   * Converter for reading MongoDB document to LocalityEntity. Automatically deserializes DOP
   * Locality data if present.
   */
  @ReadingConverter
  public static class DocumentToLocalityEntityConverter
      implements Converter<org.bson.Document, LocalityEntity> {

    private final ObjectMapper objectMapper;

    public DocumentToLocalityEntityConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public LocalityEntity convert(@NonNull org.bson.Document doc) {
      LocalityEntity entity = new LocalityEntity();

      entity.setCountryCode(doc.getString("countryCode"));
      entity.setCountryName(doc.getString("countryName"));
      entity.setSubdivisionCode(doc.getString("subdivisionCode"));
      entity.setSubdivisionName(doc.getString("subdivisionName"));
      entity.setCityName(doc.getString("cityName"));

      String localityTypeStr = doc.getString("localityType");
      if (localityTypeStr != null) {
        entity.setLocalityType(LocalityEntity.LocalityType.valueOf(localityTypeStr));
      }

      entity.setDopLocalityData(doc.getString("dopLocalityData"));

      return entity;
    }
  }

  /** Converter for DOP Locality to LocalityEntity. */
  @WritingConverter
  public static class LocalityToLocalityEntityConverter
      implements Converter<Locality, LocalityEntity> {

    private final ObjectMapper objectMapper;

    public LocalityToLocalityEntityConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public LocalityEntity convert(@NonNull Locality locality) {
      LocalityEntity entity = LocalityEntity.fromDopLocality(locality);

      // Serialize the DOP data for storage
      try {
        String serializedData = objectMapper.writeValueAsString(locality);
        entity.setDopLocalityData(serializedData);
      } catch (JsonProcessingException e) {
        throw new RuntimeException("Failed to serialize DOP Locality", e);
      }

      return entity;
    }
  }

  /** Converter for LocalityEntity to DOP Locality. */
  @ReadingConverter
  public static class LocalityEntityToLocalityConverter
      implements Converter<LocalityEntity, Locality> {

    private final ObjectMapper objectMapper;

    public LocalityEntityToLocalityConverter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public Locality convert(@NonNull LocalityEntity entity) {
      // Try to deserialize from DOP data first
      if (entity.getDopLocalityData() != null && !entity.getDopLocalityData().isBlank()) {
        try {
          return objectMapper.readValue(entity.getDopLocalityData(), Locality.class);
        } catch (JsonProcessingException e) {
          // Fall back to entity conversion if deserialization fails
        }
      }

      // Fall back to converting from entity fields
      return entity.toDopLocality();
    }
  }
}
