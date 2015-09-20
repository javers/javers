package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;

import java.time.Duration;

/**
 * @author bartosz.walacik
 */
class DurationTypeAdapter extends BasicStringTypeAdapter<Duration> {
    @Override
    public String serialize(Duration sourceValue) {
        return sourceValue.toString();
    }

    @Override
    public Duration deserialize(String serializedValue) {
        return Duration.parse(serializedValue);
    }

    @Override
    public Class getValueType() {
        return Duration.class;
    }
}
