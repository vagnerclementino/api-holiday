package me.clementino.holiday.config;

import java.util.Arrays;
import org.bson.types.ObjectId;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration
@EnableMongoAuditing
public class MongoConfig {

  @Bean
  public MongoCustomConversions customConversions() {
    return new MongoCustomConversions(
        Arrays.asList(new ObjectIdToStringConverter(), new StringToObjectIdConverter()));
  }

  /** Converter from ObjectId to String */
  public static class ObjectIdToStringConverter implements Converter<ObjectId, String> {
    @Override
    public String convert(ObjectId objectId) {
      return objectId.toHexString();
    }
  }

  /** Converter from String to ObjectId */
  public static class StringToObjectIdConverter implements Converter<String, ObjectId> {
    @Override
    public ObjectId convert(String source) {
      return ObjectId.isValid(source) ? new ObjectId(source) : null;
    }
  }
}
