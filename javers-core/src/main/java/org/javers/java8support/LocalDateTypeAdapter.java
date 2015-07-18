package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


/**
 * @author bartosz.walacik
 */
public class LocalDateTypeAdapter extends BasicStringTypeAdapter<LocalDate> {

    @Override
    public String serialize(LocalDate sourceValue) {
        return sourceValue.format(DateTimeFormatter.ISO_DATE);
    }

    @Override
    public LocalDate deserialize(String serializedValue) {
        return LocalDate.parse(serializedValue, DateTimeFormatter.ISO_DATE);
    }

    @Override
    public Class getValueType() {
        return LocalDate.class;
    }
}
