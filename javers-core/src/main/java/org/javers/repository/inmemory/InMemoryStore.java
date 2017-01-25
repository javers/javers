package org.javers.repository.inmemory;

import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class InMemoryStore implements CdoSnapshotStore {
    private Map<GlobalId, LinkedList<CdoSnapshot>> snapshots = new ConcurrentHashMap<>();

    @Override
    public boolean contains(GlobalId globalId) {
        return snapshots.containsKey(globalId);
    }

    @Override
    public List<CdoSnapshot> load(GlobalId globalId) {
        return snapshots.get(globalId);
    }

    @Override
    public synchronized void persist(CdoSnapshot snapshot) {
        LinkedList<CdoSnapshot> states = snapshots.get(snapshot.getGlobalId());
        if (states == null){
            states = new LinkedList<>();
            snapshots.put(snapshot.getGlobalId(), states);
        }
        states.push(snapshot);
    }

    @Override
    public LinkedList<CdoSnapshot> loadAll() {
        LinkedList<CdoSnapshot> all = new LinkedList<>();
        for (LinkedList<CdoSnapshot> snapshotsList : snapshots.values()) {
            all.addAll(snapshotsList);
        }
        return all;
    }
}
