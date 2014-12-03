package org.javers.core.diff.changetype.map;

import org.javers.common.string.ToStringBuilder;
import org.javers.core.diff.changetype.Atomic;

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

    @Override
    public String toString() {
        return ToStringBuilder.toString(this, "key", getKey(), "value", getValue());
    }
}
