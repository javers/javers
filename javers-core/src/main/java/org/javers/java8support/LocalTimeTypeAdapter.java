package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author bartosz.walacik
 */
public class LocalTimeTypeAdapter extends BasicStringTypeAdapter<LocalTime> {
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_LOCAL_TIME;

    @Override
    public String serialize(LocalTime sourceValue) {
        return sourceValue.format(ISO_FORMAT);
    }

    @Override
    public LocalTime deserialize(String serializedValue) {

        return LocalTime.parse(serializedValue, ISO_FORMAT);
    }

    @Override
    public Class getValueType() {
        return LocalTime.class;
    }
}
