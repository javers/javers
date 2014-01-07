package org.javers.core.diff;

import com.google.gson.annotations.Expose;

/**
 * Immutable wrapper for client's primitives and Value Objects.
 * <br/><br/>
 *
 * TODO refine dehydrate approach
 * Value needs to be dehydrated before persisting.
 * Dehydration is simply serialization to JSON.
 *
 * @author bartosz walacik
 */
public class Value {
    private final Object value;

    public Value(Object value) {
        this.value = value;
    }

    /**
     * original Value
     */
    public Object value() {
        return value;
    }

    @Override
    public String toString() {
        return "value:"+value;
    }
}

