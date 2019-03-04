package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;
import java.util.Optional;

/**
 * @author bartosz walacik
 */
class LiveCdoFactory {

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

        System.out.println("enriching hash for: " + valueObject.getGlobalId());

        List<LiveCdo> objectsToBeHashed = Lists.immutableListOf(descendantVOs, valueObject);
        String newHash = objectHasher.hash(objectsToBeHashed);

        ValueObjectId id = (ValueObjectId) valueObject.getGlobalId();

        ValueObjectId newId = globalIdFactory.replaceHashPlaceholder(id, newHash);

        System.out.println("new hash            " + newId + "\n");

        return newId;
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

    public GlobalIdFactory getGlobalIdFactory() {
        return globalIdFactory;
    }
}
