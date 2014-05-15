package org.javers.core.metamodel.object;

import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.ValueObject;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

/**
 * @author bartosz walacik
 */
public class GlobalIdFactory {

    private final TypeMapper typeMapper;

    public GlobalIdFactory(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    public GlobalCdoId createId(Object targetCdo) {
        return createId(targetCdo, null);
    }

    /**
     * @param owner for bounded ValueObjects, optional
     */
    public GlobalCdoId createId(Object targetCdo, OwnerContext owner) {
        Validate.argumentsAreNotNull(targetCdo);

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
        ValueObject valueObject = getManagedClass(valueObjectClass, ValueObject.class);
        return new UnboundedValueObjectId(valueObject);
    }

    public ValueObjectId createFromPath(InstanceId owner, Class valueObjectClass, String path){
        ValueObject valueObject = getManagedClass(valueObjectClass, ValueObject.class);
        return new ValueObjectId(valueObject, owner, path);
    }

    public InstanceId createFromId(Object localId, Class entityClass){
        Entity entity = getManagedClass(entityClass, Entity.class);
        return InstanceId.createFromId(localId, entity);
    }

    public InstanceId createFromId(Object localId, Entity entity){
        return InstanceId.createFromId(localId, entity);
    }

    /**
     * If item is Primitive or Value - returns it,
     * if item is Entity or ValueObject - returns its globalId,
     * if item is already instance of GlobalCdoId - returns it.
     */
    public Object dehydrate(Object item, JaversType targetType, OwnerContext context){
        if (!(item instanceof GlobalCdoId) && targetType instanceof ManagedType) {
            return createId(item, context);
        } else {
            return item;
        }
    }

    /**
     * if given javaClass is mapped to {@link ManagedType}
     * returns {@link ManagedType#getManagedClass()}
     *
     * @throws JaversException MANAGED_CLASS_MAPPING_ERROR
     */
    public <T extends ManagedClass> T getManagedClass(Class javaClass, Class<T> expectedType) {
        ManagedType mType = typeMapper.getJaversManagedType(javaClass);

        if (mType.getManagedClass().getClass().equals( expectedType)) {
            return (T)mType.getManagedClass();
        }
        else {
            throw new JaversException(JaversExceptionCode.MANAGED_CLASS_MAPPING_ERROR,
                                      javaClass,
                                      mType.getManagedClass().getSimpleName(),
                                      expectedType.getSimpleName());
        }
    }

    private ManagedClass getManagedClassOf(Object cdo) {
        Validate.argumentIsNotNull(cdo);
        return typeMapper.getJaversManagedType(cdo.getClass()).getManagedClass();
    }

    private boolean hasOwner(OwnerContext context) {
        return (context != null && context.getGlobalCdoId() != null);
    }

    private boolean hasNoOwner(OwnerContext context) {
        return (context == null || context.getGlobalCdoId() == null);
    }
}
