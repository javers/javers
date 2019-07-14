package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;
import java.util.Optional;

/**
 * @author bartosz walacik
 */
public class LiveCdoFactory {

    private final GlobalIdFactory globalIdFactory;
    private ObjectAccessHook objectAccessHook;
    private TypeMapper typeMapper;
    private ObjectHasher objectHasher;

    LiveCdoFactory(GlobalIdFactory globalIdFactory, ObjectAccessHook objectAccessHook, TypeMapper typeMapper, ObjectHasher objectHasher) {
        this.globalIdFactory = globalIdFactory;
        this.objectAccessHook = objectAccessHook;
        this.typeMapper = typeMapper;
        this.objectHasher = objectHasher;
    }

    ValueObjectId regenerateValueObjectHash(LiveCdo valueObject, List<LiveCdo> descendantVOs) {
        List<LiveCdo> objectsToBeHashed = Lists.immutableListOf(descendantVOs, valueObject);
        String newHash = objectHasher.hash(objectsToBeHashed);

        ValueObjectIdWithHash id = (ValueObjectIdWithHash) valueObject.getGlobalId();

        return id.freeze(newHash);
    }

    LiveCdo create(Object cdo, OwnerContext owner) {
        GlobalId globalId = globalIdFactory.createId(cdo, owner);

        Optional<ObjectAccessProxy> objectAccessor = objectAccessHook.createAccessor(cdo);
        Class<?> targetClass = objectAccessor.map((p) -> p.getTargetClass()).orElse(cdo.getClass());
        ManagedType managedType = typeMapper.getJaversManagedType(targetClass);

        if (objectAccessor.isPresent()) {
            return new LazyCdoWrapper(objectAccessor.get().getObjectSupplier(), globalId, managedType);
        }
        else {
            return new LiveCdoWrapper(cdo, globalId, managedType);
        }
    }

    GlobalId createId(Object cdo, OwnerContext owner) {
        return globalIdFactory.createId(cdo, owner);
    }

    GlobalIdFactory getGlobalIdFactory() {
        return globalIdFactory;
    }
}
