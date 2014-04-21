package org.javers.core.diff.changetype.map;

import org.javers.core.diff.changetype.Value;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public abstract  class EntryAddOrRemove extends EntryChange {
    //Value key; this looks stupid but in fact it makes sense
    private final Value key;

    private final Value value;

    EntryAddOrRemove(Object key, Object value) {
        argumentIsNotNull(key);

        this.key = new Value(key);
        this.value = new Value(value);
    }

    @Override
    public Object getKey() {
        return key.unwrap();
    }

    public Object getValue() {
        return value.unwrap();
    }

    public Value getWrappedKey() {
        return key;
    }

    public Value getWrappedValue(){
        return value;
    }
}
