package org.javers.core.snapshot;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.graph.LiveGraph;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.api.JaversExtendedRepository;
import org.javers.repository.api.JaversRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Decomposes given live objects graph into a flat list of object Snapshots.
 * Resulting structure can be easily serialized and persisted.
 *
 * @author bartosz walacik
 */
public class GraphSnapshotFactory {

    private final SnapshotFactory snapshotFactory;
    private final JaversExtendedRepository javersRepository;

    public GraphSnapshotFactory(SnapshotFactory snapshotFactory, JaversExtendedRepository javersRepository) {
        this.snapshotFactory = snapshotFactory;
        this.javersRepository = javersRepository;
    }

    /**
     *
     * @param currentVersion outcome from {@link ObjectGraphBuilder#buildGraph(Object)}
     */
    public List<CdoSnapshot> create(LiveGraph currentVersion){
        Validate.argumentIsNotNull(currentVersion);

        return doSnapshotsAndReuse(currentVersion.flatten());
    }

    private List<CdoSnapshot> doSnapshotsAndReuse(Set<ObjectNode> currentVersion){
        List<CdoSnapshot> reused = new ArrayList<>();

        for (ObjectNode node : currentVersion) {
            CdoSnapshot fresh = snapshotFactory.create(node);

            Optional<CdoSnapshot> existing = javersRepository.getLatest(fresh.getGlobalId());
            if (existing.isEmpty()) {
                reused.add(fresh);
                continue;
            }

            if (!existing.get().stateEquals(fresh)) {
                reused.add(fresh);
            }
        }

        return reused;
    }
}
