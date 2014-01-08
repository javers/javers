package org.javers.model.domain;

import org.javers.common.validation.Validate;
import org.javers.model.mapping.Entity;

/**
 * ValueObject placeholder identifier.
 * <br/><br/>
 *
 * Since ValueObjects doesn't have public Id,
 * they are identified in the context of owning Entity instance.
 *
 * @author bartosz walacik
 */
public class ValueObjectId extends GlobalCdoId {
    private final String fragment;

    public ValueObjectId(Object cdoId, Entity entity, String fragment) {
        super(cdoId, entity);
        Validate.argumentIsNotNull(fragment);

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
    public String toString() {
        return super.toString()+"#"+fragment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null) { return false;}
        if (!(o instanceof GlobalCdoId)) {return false;}

        ValueObjectId other = (ValueObjectId) o;
        return super.equals(other) && this.fragment.equals(other.fragment);
    }

    @Override
    public int hashCode() {
        return super.hashCode() + fragment.hashCode();
    }

}
