package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;

import java.time.Year;

/**
 * @author bartosz.walacik
 */
class YearTypeAdapter extends BasicStringTypeAdapter<Year> {
    @Override
    public String serialize(Year sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public Year deserialize(String serializedValue) {
        return Year.parse(serializedValue);
    }

    @Override
    public Class getValueType() {
        return Year.class;
    }
}
