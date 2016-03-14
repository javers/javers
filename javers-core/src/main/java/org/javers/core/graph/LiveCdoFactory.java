package org.javers.core.graph;

import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.TypeMapper;

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
    public CdoWrapper create (Object wrappedCdo, OwnerContext owner){
        Object wrappedCdoAccessed = objectAccessHook.access(wrappedCdo);
        GlobalId globalId = globalIdFactory.createId(wrappedCdoAccessed, owner);
        return new CdoWrapper(wrappedCdoAccessed, globalId, typeMapper.getJaversManagedType(wrappedCdoAccessed.getClass()));
    }

    @Override
    public String typeDesc() {
        return "live";
    }
}
