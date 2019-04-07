package org.javers.core.metamodel.object;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectAccessHook;
import org.javers.core.graph.ObjectAccessProxy;
import org.javers.core.metamodel.object.ValueObjectIdWithHash.ValueObjectIdWithPlaceholder;
import org.javers.core.metamodel.object.ValueObjectIdWithHash.ValueObjectIdWithPlaceholderOnParent;
import org.javers.core.metamodel.type.*;
import org.javers.repository.jql.GlobalIdDTO;
import org.javers.repository.jql.InstanceIdDTO;
import org.javers.repository.jql.UnboundedValueObjectIdDTO;
import org.javers.repository.jql.ValueObjectIdDTO;
import java.util.Optional;

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
     * @param ownerContext for bounded ValueObjects, optional
     */
    public GlobalId createId(Object targetCdo, OwnerContext ownerContext) {
        Validate.argumentsAreNotNull(targetCdo);

        Optional<ObjectAccessProxy> cdoProxy = objectAccessHook.createAccessor(targetCdo);

        Class<?> targetClass = cdoProxy.map((p) -> p.getTargetClass()).orElse(targetCdo.getClass());
        ManagedType targetManagedType = typeMapper.getJaversManagedType(targetClass);

        if (targetManagedType instanceof EntityType) {
            if (cdoProxy.isPresent() && cdoProxy.get().getLocalId().isPresent()){
                return createInstanceId(cdoProxy.get().getLocalId().get(), targetClass);
            }
            else {
                return ((EntityType) targetManagedType).createIdFromInstance(targetCdo);
            }
        }

        if (targetManagedType instanceof ValueObjectType && !hasOwner(ownerContext)) {
            return new UnboundedValueObjectId(targetManagedType.getName());
        }

        if (targetManagedType instanceof ValueObjectType && hasOwner(ownerContext)) {
            String pathFromRoot = createPathFromRoot(ownerContext.getOwnerId(), ownerContext.getPath());

            if (ownerContext.requiresObjectHasher()) {
                return new ValueObjectIdWithPlaceholder(targetManagedType.getName(),
                        getRootOwnerId(ownerContext),
                        pathFromRoot);
            } else if (ownerContext.getOwnerId() instanceof ValueObjectIdWithHash) {
                return new ValueObjectIdWithPlaceholderOnParent(targetManagedType.getName(),
                        (ValueObjectIdWithHash)ownerContext.getOwnerId(),
                        ownerContext.getPath());
            }
            else {
                return new ValueObjectId(targetManagedType.getName(), getRootOwnerId(ownerContext), pathFromRoot);
            }
        }

        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED);
    }

    private GlobalId getRootOwnerId(OwnerContext ownerContext) {
        if (ownerContext.getOwnerId() instanceof ValueObjectId){
            return ((ValueObjectId)ownerContext.getOwnerId()).getOwnerId();
        } else{
            return ownerContext.getOwnerId();
        }
    }

    private String createPathFromRoot(GlobalId parentId, String fragment) {
        if (parentId instanceof ValueObjectId){
           return ((ValueObjectId)parentId).getFragment()+"/"+fragment;
        } else{
           return fragment;
        }
    }

    public UnboundedValueObjectId createUnboundedValueObjectId(Class valueObjectClass){
        ValueObjectType valueObject = typeMapper.getJaversManagedType(valueObjectClass, ValueObjectType.class);
        return new UnboundedValueObjectId(valueObject.getName());
    }

    @Deprecated
    public ValueObjectId createValueObjectIdFromPath(GlobalId owner, String fragment){
        ManagedType ownerType = typeMapper.getJaversManagedType(owner);
        ValueObjectType valueObjectType = pathParser.parseChildValueObject(ownerType,fragment);
        return new ValueObjectId(valueObjectType.getName(), owner, fragment);
    }

    public InstanceId createIdFromInstance(Object instance) {
        EntityType entityType = typeMapper.getJaversManagedType(instance.getClass(), EntityType.class);
        return entityType.createIdFromInstance(instance);
    }

    public InstanceId createInstanceId(Object localId, Class entityClass) {
        EntityType entity = typeMapper.getJaversManagedType(entityClass, EntityType.class);
        return entity.createIdFromInstanceId(localId);
    }

    public InstanceId createInstanceId(Object localId, String typeName) {
        Optional<EntityType> entity = typeMapper.getJaversManagedTypeMaybe(typeName, EntityType.class);
        return entity.map(e -> e.createIdFromInstanceId(localId))
                     .orElseGet(() -> new InstanceId(typeName, localId, ToStringBuilder.smartToString(localId)));
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

    private boolean hasOwner(OwnerContext context) {
        return context != null && context.getOwnerId() != null;
    }
}
