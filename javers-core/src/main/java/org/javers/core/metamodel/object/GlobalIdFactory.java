package org.javers.core.metamodel.object;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectAccessHook;
import org.javers.core.metamodel.type.*;
import org.javers.repository.jql.GlobalIdDTO;

/**
 * @author bartosz walacik
 */
public class GlobalIdFactory {

    private final TypeMapper typeMapper;
    private ObjectAccessHook objectAccessHook;

    public GlobalIdFactory(TypeMapper typeMapper, ObjectAccessHook objectAccessHook) {
        this.typeMapper = typeMapper;
        this.objectAccessHook = objectAccessHook;
    }

    public GlobalId createId(Object targetCdo) {
        return createId(targetCdo, null);
    }

    /**
     * @param owner for bounded ValueObjects, optional
     */
    public GlobalId createId(Object targetCdo, OwnerContext owner) {
        Validate.argumentsAreNotNull(targetCdo);

        targetCdo = objectAccessHook.access(targetCdo);
        ManagedType targetManagedType = typeMapper.getJaversManagedType(targetCdo.getClass());

        if (targetManagedType instanceof EntityType) {
            return InstanceId.createFromInstance(targetCdo, (EntityType) targetManagedType);
        }

        if (targetManagedType instanceof ValueObjectType && hasNoOwner(owner)) {
            return new UnboundedValueObjectId((ValueObjectType)targetManagedType);
        }

        if (targetManagedType instanceof ValueObjectType && hasOwner(owner)) {
            return new ValueObjectId((ValueObjectType) targetManagedType, owner);
        }

        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }

    public UnboundedValueObjectId createFromClass(Class valueObjectClass){
        ValueObjectType valueObject = typeMapper.getJaversManagedType(valueObjectClass, ValueObjectType.class);
        return new UnboundedValueObjectId(valueObject);
    }

    public ValueObjectId createFromPath(GlobalId owner, Class valueObjectClass, String path){
        ValueObjectType valueObject = typeMapper.getJaversManagedType(valueObjectClass, ValueObjectType.class);
        return new ValueObjectId(valueObject, owner, path);
    }


    public InstanceId createFromId(Object localId, EntityType entity){
        return InstanceId.createFromId(localId, entity);
    }

    public InstanceId createFromId(Object localId, Class entityClass){
        EntityType entity = typeMapper.getJaversManagedType(entityClass, EntityType.class);
        return InstanceId.createFromId(localId, entity);
    }

    public GlobalId createFromDto(GlobalIdDTO idDto){
        return idDto.create(typeMapper);
    }

    /**
     * If item is Primitive or Value - returns it,
     * if item is Entity or ValueObject - returns its globalId,
     * if item is already instance of GlobalId - returns it.
     */
    public Object dehydrate(Object item, JaversType targetType, OwnerContext context){
        if (item == null) {
            return null;
        }
        if (!(item instanceof GlobalId) && targetType instanceof ManagedType) {
            return createId(item, context);
        } else {
            return item;
        }
    }

    private boolean hasOwner(OwnerContext context) {
        return (context != null && context.getGlobalId() != null);
    }

    private boolean hasNoOwner(OwnerContext context) {
        return (context == null || context.getGlobalId() == null);
    }
}
