package org.javers.core.graph;

import org.javers.common.string.ShaDigest;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.snapshot.SnapshotFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author bartosz.walacik
 */
class ObjectHasher {
    private final SnapshotFactory snapshotFactory;
    private final JsonConverter jsonConverter;

    ObjectHasher(SnapshotFactory snapshotFactory, JsonConverter jsonConverter) {
        this.snapshotFactory = snapshotFactory;
        this.jsonConverter = jsonConverter;
    }

    String hash(List<LiveCdo> objects) {
        String jsonState = objects.stream().map(cdo -> snapshotFactory.createSnapshotStateNoRefs(cdo))
                        .map(state -> jsonConverter.toJson(state))
                        .collect(Collectors.joining( "\n" ));
        return ShaDigest.longDigest(jsonState);
    }
}
