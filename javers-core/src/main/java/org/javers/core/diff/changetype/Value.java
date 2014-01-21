package org.javers.core.diff.changetype;

import org.javers.common.collections.Objects;
import org.javers.common.collections.Primitives;

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

    public boolean isNull() {
        return value == null;
    }

    /**
     * true if value is not null and is primitive, box or String
     * @return
     */
    public boolean isJsonBasicType() {
        if(isNull()) {
            return false;
        }

        return Primitives.isJsonBasicType(value);
    }

    /**
     * original Value
     */
    public Object unwrap() {
        return value;
    }

    @Override
    public String toString() {
        return "value:"+value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Value)) {
            return false;
        }

        Value other = (Value)obj;
        return Objects.nullSafeEquals(value, other.value);
    }

    @Override
    public int hashCode() {
        if (value == null) {
            return 0;
        }
        return value.hashCode();
    }
}

