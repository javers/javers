package org.javers.core.diff.changetype.map;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public abstract  class EntryAddOrRemove extends EntryChange {
    private final Object key;
    private final Object value;

    protected EntryAddOrRemove(Object key, Object value) {
        argumentIsNotNull(key);

        this.key = key;
        this.value = value;
    }

    public Object getKey() {
        return key;
    }

    public Object getValue() {
        return value;
    }
}
