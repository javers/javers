package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * @author bartosz.walacik
 */
class LocalDateTimeTypeAdapter extends BasicStringTypeAdapter<LocalDateTime> {
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public String serialize(LocalDateTime sourceValue) {
        return sourceValue.format(ISO_FORMAT);
    }

    @Override
    public LocalDateTime deserialize(String serializedValue) {
        return LocalDateTime.parse(serializedValue, ISO_FORMAT);
    }

    @Override
    public Class getValueType() {
        return LocalDateTime.class;
    }
}
