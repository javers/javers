package org.javers.java8support;

import java.time.YearMonth;
import org.javers.core.json.BasicStringTypeAdapter;

class YearMonthTypeAdapter extends BasicStringTypeAdapter<YearMonth> {
    @Override
    public String serialize(YearMonth sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public YearMonth deserialize(String serializedValue) {
        return YearMonth.parse(serializedValue);
    }

    @Override
    public Class getValueType() {
        return YearMonth.class;
    }
}
