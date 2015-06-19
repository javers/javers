package org.javers.core.metamodel.object;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectAccessHook;
import org.javers.core.metamodel.clazz.Entity;
import org.javers.core.metamodel.clazz.ManagedClass;
import org.javers.core.metamodel.clazz.ValueObject;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;
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
        ManagedClass targetManagedClass = getManagedClassOf(targetCdo);

        if (targetManagedClass instanceof Entity) {
            return InstanceId.createFromInstance(targetCdo, (Entity) targetManagedClass);
        }

        if (targetManagedClass instanceof ValueObject && hasNoOwner(owner)) {
            return new UnboundedValueObjectId((ValueObject)targetManagedClass);
        }

        if (targetManagedClass instanceof ValueObject && hasOwner(owner)) {
            return new ValueObjectId((ValueObject) targetManagedClass, owner);
        }

        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }

    public UnboundedValueObjectId createFromClass(Class valueObjectClass){
        ValueObject valueObject = typeMapper.getManagedClass(valueObjectClass, ValueObject.class);
        return new UnboundedValueObjectId(valueObject);
    }

    public ValueObjectId createFromPath(GlobalId owner, Class valueObjectClass, String path){
        ValueObject valueObject = typeMapper.getManagedClass(valueObjectClass, ValueObject.class);
        return new ValueObjectId(valueObject, owner, path);
    }


    public InstanceId createFromId(Object localId, Entity entity){
        return InstanceId.createFromId(localId, entity);
    }

    public InstanceId createFromId(Object localId, Class entityClass){
        Entity entity = typeMapper.getManagedClass(entityClass, Entity.class);
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

    private ManagedClass getManagedClassOf(Object cdo) {
        Validate.argumentIsNotNull(cdo);
        return typeMapper.getJaversManagedType(cdo.getClass()).getManagedClass();
    }

    private boolean hasOwner(OwnerContext context) {
        return (context != null && context.getGlobalId() != null);
    }

    private boolean hasNoOwner(OwnerContext context) {
        return (context == null || context.getGlobalId() == null);
    }
}
