package org.javers.core.metamodel.object;

import org.javers.core.metamodel.property.ValueObject;

import static org.javers.common.validation.Validate.argumentsAreNotNull;
import static org.javers.core.metamodel.object.InstanceId.InstanceIdDTO.instanceId;

/**
 * ValueObject global unique identifier.
 * <br/><br/>
 *
 * Since ValueObjects doesn't have public Id,
 * they are identified by <i>fragment path</i> in the context of owning Entity instance.
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

    public ValueObjectId(ValueObject valueObject, OwnerContext ownerContext) {
        this(valueObject, ownerContext.getGlobalCdoId(), ownerContext.getPath());
    }

    /**
     * Path to ValueObject, should be unique in the Entity <b>instance</b> scope.
     * Usually, property name.
     * It works like <i>fragment identifier</i> in URL
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
    public String value() {
        return getOwnerId().value()+"#"+fragment;
    }

    public static class ValueObjectIdDTO extends GlobalCdoIdDTO{
        private InstanceId.InstanceIdDTO ownerId;
        private final String fragment;

        private ValueObjectIdDTO(Class  ownerClass, Object ownerLocalId, String fragment) {
            ownerId = instanceId(ownerLocalId, ownerClass);
            this.fragment = fragment;
        }

        public static ValueObjectIdDTO valueObjectId(Object ownerLocalId, Class  ownerClass, String fragment){
            return new ValueObjectIdDTO(ownerClass, ownerLocalId, fragment);
        }

        @Override
        public String value() {
            return ownerId.value()+"#"+fragment;
        }
    }
}
