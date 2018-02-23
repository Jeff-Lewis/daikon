package org.talend.daikon.converter;

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;

public class StringConverter extends WithFormatConverter<StringConverter, String> {

    @Override
    public String convert(Object value) {
        if (value == null) {
            return returnDefaultValue();
        } else if (value instanceof ByteBuffer) {
            return new String(((ByteBuffer) value).array());
        } else if (getNumberFormatter() != null && value instanceof Number) {
            return getNumberFormatter().format(value);
        } else if (getDateFormatter() != null) {
            if (value instanceof Number) {
                return LocalDate.ofEpochDay(((Number) value).longValue()).format(getDateFormatter());
            } else if (value instanceof LocalDate) {
            }
        } else if (getTimeMillisFormatter() != null) {
            if (value instanceof Number) {
                return Instant.ofEpochMilli(((Number) value).intValue()).atZone(ZoneOffset.ofTotalSeconds(0)).toLocalTime()
                        .format(getTimeMillisFormatter());
            } else if (value instanceof LocalTime) {
            }
        } else if (getTimestampMillisFormatter() != null) {
            if (value instanceof Number) {
                return Instant.ofEpochMilli(((Number) value).intValue()).atZone(ZoneOffset.ofTotalSeconds(0)).toLocalDateTime()
                        .format(getTimestampMillisFormatter());
            } else if (value instanceof LocalDateTime) {
            }
        }
        return value.toString();
    }
}
