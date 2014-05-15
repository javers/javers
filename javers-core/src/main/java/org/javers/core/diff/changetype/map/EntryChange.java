package org.javers.core.diff.changetype.map;

import org.javers.core.diff.changetype.Atomic;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public abstract class EntryChange {
    private final Atomic key;

    EntryChange(Object key) {
        argumentIsNotNull(key);
        this.key = new Atomic(key);
    }

    public Object getKey() {
        return key.unwrap();
    }

    public Atomic getWrappedKey() {
        return key;
    }

}
