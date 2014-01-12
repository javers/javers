package org.javers.model.domain;

import org.javers.common.validation.Validate;
import org.javers.model.mapping.Entity;
import org.javers.model.mapping.ValueObject;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * ValueObject placeholder identifier.
 * <br/><br/>
 *
 * Since ValueObjects doesn't have public Id,
 * they are identified in the context of owning Entity instance.
 *
 * @author bartosz walacik
 */
public class ValueObjectId extends UnboundedValueObjectId {
    private final InstanceId owningInstanceId;
    private final String fragment;

    public ValueObjectId(ValueObject valueObject, InstanceId owningInstanceId, String fragment) {
        super(valueObject);
        argumentsAreNotNull(owningInstanceId, fragment);
        this.owningInstanceId = owningInstanceId;
        this.fragment = fragment;
    }

    /**
     * Placeholder Identifier of (client's) ValueObject,
     * should be unique in Entity <b>instance</b> scope
     */
    public String getFragment() {
        return fragment;
    }

    @Override
    public String getCdoId() {
        return "#"+fragment;
    }

    @Override
    public String toString() {
        return super.toString()+"#"+fragment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || !(o instanceof ValueObjectId)) {return false;}

        ValueObjectId other = (ValueObjectId) o;
        return super.equals(other)
               && this.fragment.equals(other.fragment)
               && this.owningInstanceId.equals(other.owningInstanceId);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + fragment.hashCode() + owningInstanceId.hashCode();
    }

}
