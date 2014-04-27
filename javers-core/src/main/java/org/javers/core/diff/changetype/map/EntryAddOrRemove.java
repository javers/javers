package org.javers.core.diff.changetype.map;

import org.javers.core.diff.changetype.Value;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public abstract  class EntryAddOrRemove extends EntryChange {
    private final Value value;

    EntryAddOrRemove(Object key, Object value) {
        super(key);
        this.value = new Value(value);
    }

    public Object getValue() {
        return value.unwrap();
    }

    public Value getWrappedValue(){
        return value;
    }
}
