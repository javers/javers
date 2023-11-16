package org.javers.java8support;

import java.time.MonthDay;
import org.javers.core.json.BasicStringTypeAdapter;

class MonthDayTypeAdapter extends BasicStringTypeAdapter<MonthDay> {
    @Override
    public String serialize(MonthDay sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public MonthDay deserialize(String serializedValue) {
        return MonthDay.parse(serializedValue);
    }

    @Override
    public Class getValueType() {
        return MonthDay.class;
    }
}
