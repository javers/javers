package org.javers.core.metamodel.object;

import org.javers.core.metamodel.type.EntityType;

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

    public ValueObjectId(String typeName, GlobalId ownerId, String fragment) {
        super(typeName);
        argumentsAreNotNull(ownerId, fragment);
        this.ownerId = ownerId;
        this.fragment = fragment;
    }

    /**
     * Path to ValueObject, should be unique in the Entity <b>instance</b> scope.
     * Usually, property name.
     * It works like <i>fragment identifier</i> in URL
     */
    public String getFragment() {
        return fragment;
    }

    public boolean hasOwnerOfType(EntityType entityType){
        return ownerId.getTypeName().equals(entityType.getName());
    }

    public GlobalId getOwnerId() {
        return ownerId;
    }

    @Override
    public String value() {
        String sep = "#";
        if (getOwnerId() instanceof ValueObjectId){
            sep = "/";
        }
        return getOwnerId().value()+sep+fragment;
    }
}
