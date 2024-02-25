package org.javers.core.graph;

import java.util.List;
import java.util.stream.Collectors;

import org.javers.common.string.ShaDigest;
import org.javers.core.json.JsonConverter;
import org.javers.core.snapshot.SnapshotFactory;

public class SnapshotObjectHasher implements ObjectHasher {
	
	private final SnapshotFactory snapshotFactory;
    private final JsonConverter jsonConverter;

    SnapshotObjectHasher(SnapshotFactory snapshotFactory, JsonConverter jsonConverter) {
        this.snapshotFactory = snapshotFactory;
        this.jsonConverter = jsonConverter;
    }
    @Override
    public String hash(List<LiveCdo> objects) {
        String jsonState = objects.stream().map(cdo -> snapshotFactory.createSnapshotStateNoRefs(cdo))
                        .map(state -> jsonConverter.toJson(state))
                        .sorted()
                        .collect(Collectors.joining( "\n" ));
        return ShaDigest.longDigest(jsonState);
    }

}
