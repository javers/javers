package org.javers.core.diff.changetype.map;

import org.javers.core.diff.changetype.Value;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public abstract class EntryChange {
    private final Value key;

    EntryChange(Object key) {
        argumentIsNotNull(key);
        this.key = new Value(key);
    }

    public Object getKey() {
        return key.unwrap();
    }

    public Value getWrappedKey() {
        return key;
    }

}
