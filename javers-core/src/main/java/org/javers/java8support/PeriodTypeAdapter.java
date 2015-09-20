package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;

import java.time.Period;

/**
 * @author bartosz.walacik
 */
class PeriodTypeAdapter extends BasicStringTypeAdapter<Period> {
    @Override
    public String serialize(Period sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public Period deserialize(String serializedValue) {
        return Period.parse(serializedValue);
    }

    @Override
    public Class getValueType() {
        return Period.class;
    }
}
