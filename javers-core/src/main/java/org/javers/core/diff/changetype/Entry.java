package org.javers.core.diff.changetype;

import java.util.Map;

/**
 * Immutable
 *
 * @author bartosz walacik
 */
public class Entry implements Map.Entry {
    private final Object key;
    private final Object value;

    public Entry(Map.Entry entry) {
        this.key = entry.getKey();
        this.value = entry.getValue();
    }

    public Entry(Object key, Object value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public Object getKey() {
        return key;
    }

    @Override
    public Object getValue() {
        return value;
    }

    @Override
    public Object setValue(Object value) {
        throw new IllegalStateException("not implemented");
    }
}
