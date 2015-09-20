package org.javers.java8support;

import org.javers.core.json.BasicStringTypeAdapter;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author bartosz.walacik
 */
public class ZonedDateTimeTypeAdapter extends BasicStringTypeAdapter<ZonedDateTime> {
    private static final DateTimeFormatter ISO_FORMAT = DateTimeFormatter.ISO_ZONED_DATE_TIME;

    @Override
    public String serialize(ZonedDateTime sourceValue) {
        return sourceValue.format(ISO_FORMAT);
    }

    @Override
    public ZonedDateTime deserialize(String serializedValue) {
        return ZonedDateTime.parse(serializedValue, ISO_FORMAT);
    }


    @Override
    public Class getValueType() {
        return ZonedDateTime.class;
    }
}
