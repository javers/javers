package org.javers.core.metamodel.object;

import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.ValueObjectType;

import static org.javers.common.validation.Validate.argumentsAreNotNull;

/**
 * ValueObject global unique identifier.
 * <br> <br>
 *
 * Since ValueObjects doesn't have public Id,
 * they are identified by <i>fragment path</i> in the context of owning Entity instance.
 *
 * @author bartosz walacik
 */
public class ValueObjectId extends GlobalId {
    private final GlobalId ownerId;
    private final String fragment;
    private transient final ValueObjectType valueObject;

    public ValueObjectId(ValueObjectType valueObject, GlobalId ownerId, String fragment) {
        argumentsAreNotNull(valueObject, ownerId, fragment);
        this.ownerId = ownerId;
        this.fragment = fragment;
        this.valueObject = valueObject;
    }

    public ValueObjectId(ValueObjectType valueObject, OwnerContext ownerContext) {
        this(valueObject, ownerContext.getGlobalId(), ownerContext.getPath());
    }

    /**
     * Path to ValueObject, should be unique in the Entity <b>instance</b> scope.
     * Usually, property name.
     * It works like <i>fragment identifier</i> in URL
     */
    public String getFragment() {
        return fragment;
    }

    public GlobalId getOwnerId() {
        return ownerId;
    }

    @Override
    public String value() {
        return getOwnerId().value()+"#"+fragment;
    }

    @Override
    public ValueObjectType getManagedType() {
        return valueObject;
    }
}
