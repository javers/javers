package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;
import org.javers.core.json.typeadapter.util.UtilTypeCoreAdapters;
import java.time.LocalDateTime;

/**
 * @author bartosz.walacik
 */
class LocalDateTimeTypeAdapter extends BasicStringTypeAdapter<LocalDateTime> {

    @Override
    public String serialize(LocalDateTime sourceValue) {
        return UtilTypeCoreAdapters.serialize(sourceValue);
    }

    @Override
    public LocalDateTime deserialize(String serializedValue) {
        return LocalDateTime.from(UtilTypeCoreAdapters.deserializeLocalDateTime(serializedValue));
    }

    @Override
    public Class getValueType() {
        return LocalDateTime.class;
    }
}
