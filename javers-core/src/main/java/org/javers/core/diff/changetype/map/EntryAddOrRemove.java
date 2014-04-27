package org.javers.core.diff.changetype.map;

import org.javers.core.diff.changetype.Atomic;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public abstract  class EntryAddOrRemove extends EntryChange {
    private final Atomic value;

    EntryAddOrRemove(Object key, Object value) {
        super(key);
        this.value = new Atomic(value);
    }

    public Object getValue() {
        return value.unwrap();
    }

    public Atomic getWrappedValue(){
        return value;
    }
}
