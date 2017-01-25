package org.javers.repository.inmemory;

import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;

import java.util.List;

interface CdoSnapshotStore {

    boolean contains(GlobalId globalId);

    List<CdoSnapshot> load(GlobalId globalId);

    List<CdoSnapshot> loadAll();

    void persist(CdoSnapshot snapshot);
}
