package org.javers.repository.inmemory;

import com.google.gson.JsonElement;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStoreWithSerialization implements CdoSnapshotStore {
    private Map<GlobalId, LinkedList<JsonElement>> snapshots = new ConcurrentHashMap<>();

    private final JsonConverter jsonConverter;

    public InMemoryStoreWithSerialization(JsonConverter jsonConverter, JsonConverter jsonConverter1) {
        this.jsonConverter = jsonConverter1;
    }


    @Override
    public boolean contains(GlobalId globalId) {
        return snapshots.containsKey(globalId);
    }

    @Override
    public List<CdoSnapshot> load(GlobalId globalId) {
        return deserialize(snapshots.get(globalId));
    }

    @Override
    public synchronized void persist(CdoSnapshot snapshot) {
        LinkedList<JsonElement> states = snapshots.get(snapshot.getGlobalId());
        if (states == null){
            states = new LinkedList<>();
            snapshots.put(snapshot.getGlobalId(), states);
        }
        states.push(serialize(snapshot));
    }

    @Override
    public List<CdoSnapshot> loadAll() {
        LinkedList<CdoSnapshot> all = new LinkedList<>();
        for (LinkedList<JsonElement> snapshotsList : snapshots.values()) {
            all.addAll(deserialize(snapshotsList));
        }
        return all;
    }

    private CdoSnapshot deserialize(JsonElement element){
        return jsonConverter.fromJson(element, CdoSnapshot.class);
    }

    private List<CdoSnapshot> deserialize(List<JsonElement> elements){
        List<CdoSnapshot> result = new ArrayList<>();
        for (JsonElement e : elements){
            result.add(deserialize(e));
        }
        return result;
    }

    private JsonElement serialize(CdoSnapshot snapshot){
        return jsonConverter.toJsonElement(snapshot);
    }
}
