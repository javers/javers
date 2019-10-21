package org.javers.core.diff.changetype;

import org.javers.common.collections.Primitives;
import org.javers.core.diff.appenders.HashWrapper;
import org.javers.core.metamodel.property.MissingProperty;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable wrapper for client's Primitives, Values and Id's
 * <br><br>
 *
 * TODO refine dehydrate approach
 * Value needs to be dehydrated before persisting. Dehydration is simply serialization to JSON.
 *
 * @author bartosz walacik
 */
public class Atomic implements Serializable {

    private final Object value;

    public Atomic(Object value) {
        this.value = value instanceof HashWrapper ? ((HashWrapper)value).unwrap() : value;
    }

    public boolean isNull() {
        return value == null;
    }

    /**
     * @return true if value is not null and is primitive, box or String
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
        return MissingProperty.INSTANCE == value ? null : value;
    }

    @Override
    public String toString() {
        return "value:"+value;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Atomic)) {
            return false;
        }

        Atomic other = (Atomic)obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        if (value == null) {
            return 0;
        }
        return value.hashCode();
    }
}

