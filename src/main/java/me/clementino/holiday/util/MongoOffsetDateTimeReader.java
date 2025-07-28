package me.clementino.holiday.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * MongoDB converter for reading OffsetDateTime from Date.
 */
@ReadingConverter
public class MongoOffsetDateTimeReader implements Converter<Date, OffsetDateTime> {

    @Override
    public OffsetDateTime convert(Date date) {
        return date.toInstant().atOffset(ZoneOffset.UTC);
    }
}
