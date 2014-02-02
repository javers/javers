package org.javers.core.metamodel.object;

import org.javers.core.metamodel.property.ValueObject;

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
    private final GlobalCdoId ownerId;
    private final String fragment;

    public ValueObjectId(ValueObject valueObject, GlobalCdoId ownerId, String fragment) {
        super(valueObject);
        argumentsAreNotNull(ownerId, fragment);
        this.ownerId = ownerId;
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
    public Object getCdoId() {
        return null;
    }

    public GlobalCdoId getOwnerId() {
        return ownerId;
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
               && this.ownerId.equals(other.ownerId);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + fragment.hashCode() + ownerId.hashCode();
    }

}
