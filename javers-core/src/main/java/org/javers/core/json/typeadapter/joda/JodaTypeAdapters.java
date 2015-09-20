package org.javers.core.json.typeadapter.joda;

import org.javers.common.collections.Lists;
import org.javers.core.json.JsonTypeAdapter;

import java.util.List;

/**
 * @author bartosz.walacik
 */
public class JodaTypeAdapters {

    public static List<JsonTypeAdapter> adapters() {
        return (List)Lists.immutableListOf(
                new LocalDateTimeTypeAdapter(),
                new LocalDateTypeAdapter()
        );
    }
}
