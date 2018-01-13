package org.javers.core.snapshot;

import org.javers.common.string.ShaDigest;
import org.javers.core.graph.LiveCdoFactory;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshotState;
import org.javers.core.metamodel.object.LiveCdo;

/**
 * @author bartosz.walacik
 */
public class ObjectHasher {
    private final SnapshotFactory snapshotFactory;
    private final JsonConverter jsonConverter;
    private final LiveCdoFactory liveCdoFactory;

    public ObjectHasher(SnapshotFactory snapshotFactory, JsonConverter jsonConverter, LiveCdoFactory liveCdoFactory) {
        this.snapshotFactory = snapshotFactory;
        this.jsonConverter = jsonConverter;
        this.liveCdoFactory = liveCdoFactory;
    }

    public String hash(Object object) {
        LiveCdo cdo = liveCdoFactory.create(object, null);

        CdoSnapshotState state = snapshotFactory.createSnapshotState(cdo);

        String jsonState = jsonConverter.toJson(state);

        return ShaDigest.longDigest(jsonState);
    }
}
