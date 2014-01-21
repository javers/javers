package org.javers.model.domain;

import org.javers.model.mapping.ValueObject;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Unbounded unwrap object, has '/' as symbolic cdoId representation.
 * <p/>
 * This kind of Id is assigned by graph builder to unwrap object which is not embedded in any Entity instance.
 * (by design or by accident)
 * <p/>
 *
 * Its recommended to avoid Unbounded unwrap objects since they don't have real global id.
 * Prefer embedding unwrap objects in Entity instances to leverage {@link ValueObjectId} global Id.
 *
 *
 * @author bartosz walacik
 */
public class UnboundedValueObjectId extends GlobalCdoId {
    private transient final ValueObject valueObject;

    public UnboundedValueObjectId(ValueObject valueObject) {
        argumentIsNotNull(valueObject);
        this.valueObject = valueObject;
    }

    @Override
    public ValueObject getCdoClass() {
        return valueObject;
    }

    @Override
    public String toString() {
        return valueObject.getSourceClass().getName()+"/";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || !(o instanceof UnboundedValueObjectId)) {return false;}

        UnboundedValueObjectId other = (UnboundedValueObjectId) o;
        return valueObject.equals(other.valueObject);
    }

    @Override
    public int hashCode() {
        return valueObject.hashCode();
    }

    @Override
    public Object getCdoId() {
        return "/";
    }
}
