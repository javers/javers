package org.javers.core.graph;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.LiveCdo;
import org.javers.core.metamodel.object.OwnerContext;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.Optional;

/**
 * @author bartosz walacik
 */
public class LiveCdoFactory implements CdoFactory {

    private final GlobalIdFactory globalIdFactory;
    private ObjectAccessHook objectAccessHook;
    private TypeMapper typeMapper;

    public LiveCdoFactory(GlobalIdFactory globalIdFactory, ObjectAccessHook objectAccessHook, TypeMapper typeMapper) {
        this.globalIdFactory = globalIdFactory;
        this.objectAccessHook = objectAccessHook;
        this.typeMapper = typeMapper;
    }

    @Override
    public LiveCdo create(Object cdo, OwnerContext owner) {
        GlobalId globalId = globalIdFactory.createId(cdo, owner);

        ManagedType managedType = typeMapper.getJaversManagedType(globalId);

        Optional<ObjectAccessProxy> objectAccessor = objectAccessHook.createAccessor(cdo);

        if (objectAccessor.isPresent()) {
            return new LazyCdoWrapper(objectAccessor.get().getObjectSupplier(), globalId, managedType);
        }
        else {
            return new LiveCdoWrapper(cdo, globalId, managedType);
        }
    }

    @Override
    public String typeDesc() {
        return "live";
    }
}
