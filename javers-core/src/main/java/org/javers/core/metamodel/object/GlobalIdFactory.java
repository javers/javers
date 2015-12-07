package org.javers.core.metamodel.object;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectAccessHook;
import org.javers.core.metamodel.type.*;
import org.javers.repository.jql.GlobalIdDTO;
import org.javers.repository.jql.InstanceIdDTO;
import org.javers.repository.jql.UnboundedValueObjectIdDTO;
import org.javers.repository.jql.ValueObjectIdDTO;

/**
 * @author bartosz walacik
 */
public class GlobalIdFactory {

    private final TypeMapper typeMapper;
    private ObjectAccessHook objectAccessHook;
    private final GlobalIdPathParser pathParser;

    public GlobalIdFactory(TypeMapper typeMapper, ObjectAccessHook objectAccessHook) {
        this.typeMapper = typeMapper;
        this.objectAccessHook = objectAccessHook;
        this.pathParser = new GlobalIdPathParser(typeMapper);
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
            return new UnboundedValueObjectId(targetManagedType.getName());
        }

        if (targetManagedType instanceof ValueObjectType && hasOwner(owner)) {
            return new ValueObjectId(targetManagedType.getName(), owner.getGlobalId(), owner.getPath());
        }

        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }

    public UnboundedValueObjectId createUnboundedValueObjectId(Class valueObjectClass){
        ValueObjectType valueObject = typeMapper.getJaversManagedType(valueObjectClass, ValueObjectType.class);
        return new UnboundedValueObjectId(valueObject.getName());
    }

    public InstanceId createInstanceId(Object localId, EntityType entity){
        return new InstanceId(entity.getName(), localId);
    }

    public ValueObjectId createValueObjectIdFromPath(GlobalId owner, String fragment){
        ManagedType ownerType = typeMapper.getJaversManagedType(owner);
        ValueObjectType valueObjectType = pathParser.parseChildValueObject(ownerType,fragment);
        return new ValueObjectId(valueObjectType.getName(), owner, fragment);
    }

    public void touchValueObjectFromPath(ManagedType ownerType, String fragment){
        pathParser.parseChildValueObject(ownerType, fragment);
    }


    public ValueObjectId createValueObjectId(Class valueObjectClass, GlobalId owner, String fragment){
        ValueObjectType valueObject = typeMapper.getJaversManagedType(valueObjectClass, ValueObjectType.class);
        return new ValueObjectId(valueObject.getName(), owner, fragment);
    }

    public InstanceId createInstanceId(Object localId, Class entityClass){
        EntityType entity = typeMapper.getJaversManagedType(entityClass, EntityType.class);
        return createInstanceId(localId, entity);
    }

    public GlobalId createFromDto(GlobalIdDTO globalIdDTO){
        if (globalIdDTO instanceof InstanceIdDTO){
            InstanceIdDTO idDTO = (InstanceIdDTO) globalIdDTO;
            return createInstanceId(idDTO.getCdoId(), idDTO.getEntity());
        }
        if (globalIdDTO instanceof UnboundedValueObjectIdDTO){
            UnboundedValueObjectIdDTO idDTO = (UnboundedValueObjectIdDTO) globalIdDTO;
            return createUnboundedValueObjectId(idDTO.getVoClass());
        }
        if (globalIdDTO instanceof ValueObjectIdDTO){
            ValueObjectIdDTO idDTO = (ValueObjectIdDTO) globalIdDTO;
            GlobalId ownerId = createFromDto(idDTO.getOwnerIdDTO());
            return createValueObjectIdFromPath(ownerId, idDTO.getPath());
        }
        throw new RuntimeException("type " + globalIdDTO.getClass() + " is not implemented");
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
