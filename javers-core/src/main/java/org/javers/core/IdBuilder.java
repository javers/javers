package org.javers.core;

import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.*;

/**
 * @author bartosz walacik
 */
public class IdBuilder {
    private final GlobalIdFactory globalIdFactory;

    private GlobalCdoId owner;

    public IdBuilder(GlobalIdFactory globalIdFactory) {
        this.globalIdFactory = globalIdFactory;
    }

    public IdBuilder withUnboundedOwner(Class ownerValueObjectClass) {
        owner = unboundedValueObjectId(ownerValueObjectClass);
        return this;
    }

    public IdBuilder withOwner(Object localId, Class ownerEntityClass) {
        owner = instanceId(localId, ownerEntityClass);
        return this;
    }


    public ValueObjectId voId(Class valueObjectClass, String path){
        Validate.conditionFulfilled(owner != null, "call withOwner() first");
        return globalIdFactory.createFromPath(owner, valueObjectClass, path);
    }

    public InstanceId instanceId(Object instance){
        Validate.argumentsAreNotNull(instance);

        return (InstanceId)globalIdFactory.createId(instance, null);
    }

    public UnboundedValueObjectId unboundedValueObjectId(Class valueObjectClass){
        Validate.argumentsAreNotNull(valueObjectClass);
        return globalIdFactory.createFromClass(valueObjectClass);

    }

    public InstanceId instanceId(Object localId, Class entityClass){
        Validate.argumentsAreNotNull(localId, entityClass);
        return  globalIdFactory.createFromId(localId, entityClass);
    }
}
