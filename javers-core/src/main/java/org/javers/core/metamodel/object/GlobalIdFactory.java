package org.javers.core.metamodel.object;

import org.javers.common.collections.Objects;
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

    public ValueObjectId createFromPath(InstanceId owner, Class valueObjectClass, String path){
        ManagedClass targetManagedClass = getManagedClass(valueObjectClass);
        return new ValueObjectId((ValueObject) targetManagedClass, owner, path);
    }

    /**
     *  @throws JaversException ENTITY_NOT_MAPPED if given javaClass is NOT mapped to Entity
     */
    public InstanceId createFromId(Object localId, Class entityClass){
        ManagedClass managedClass = getManagedClass(entityClass);

        if (!(managedClass instanceof Entity)){
            throw new JaversException(JaversExceptionCode.ENTITY_NOT_MAPPED, entityClass, managedClass.getClass().getSimpleName());
        }

        return InstanceId.createFromId(localId, (Entity) managedClass);
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

    private ManagedClass getManagedClassOf(Object cdo) {
        Validate.argumentIsNotNull(cdo);
        return getManagedClass(cdo.getClass());
    }

    /**
     * if given javaClass is mapped to {@link ManagedType} returns {@link ManagedType#getManagedClass()}
     *
     * @throws JaversException CLASS_NOT_MANAGED if given javaClass is NOT mapped to {@link ManagedType}
     */
    public ManagedClass getManagedClass(Class javaClass) {
        JaversType jType = typeMapper.getJaversType(javaClass);
        if (jType instanceof ManagedType) {
            return ((ManagedType)jType).getManagedClass();
        }

        throw new JaversException(JaversExceptionCode.CLASS_NOT_MANAGED, javaClass, jType.getClass().getSimpleName());
    }

    private boolean hasOwner(OwnerContext context) {
        return (context != null && context.getGlobalCdoId() != null);
    }

    private boolean hasNoOwner(OwnerContext context) {
        return (context == null || context.getGlobalCdoId() == null);
    }
}
