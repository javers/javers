package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * @author bartosz.walacik
 */
class LocalDateTypeAdapter extends BasicStringTypeAdapter<LocalDate> {
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_DATE;

    @Override
    public String serialize(LocalDate sourceValue) {
        return sourceValue.format(ISO_FORMAT);
    }

    @Override
    public LocalDate deserialize(String serializedValue) {
        return LocalDate.parse(serializedValue, ISO_FORMAT);
    }

    @Override
    public Class getValueType() {
        return LocalDate.class;
    }
}
