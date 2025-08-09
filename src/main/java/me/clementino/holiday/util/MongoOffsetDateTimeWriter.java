package me.clementino.holiday.util;

import java.time.OffsetDateTime;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

/** MongoDB converter for writing OffsetDateTime to Date. */
@WritingConverter
public class MongoOffsetDateTimeWriter implements Converter<OffsetDateTime, Date> {

  @Override
  public Date convert(OffsetDateTime offsetDateTime) {
    return Date.from(offsetDateTime.toInstant());
  }
}
