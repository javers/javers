package org.javers.core.snapshot;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.graph.GraphVisitor;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.api.JaversRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Decomposes given live objects graph into a flat list of object Snapshots.
 * Resulting structure can be easily serialized and persisted.
 *
 * @author bartosz walacik
 */
public class GraphSnapshotFactory {

    private final SnapshotFactory snapshotFactory;
    private final JaversRepository javersRepository;

    public GraphSnapshotFactory(SnapshotFactory snapshotFactory, JaversRepository javersRepository) {
        this.snapshotFactory = snapshotFactory;
        this.javersRepository = javersRepository;
    }

    /**
     *
     * @param currentVersion graph 'root', outcome from {@link ObjectGraphBuilder#buildGraph(Object)}
     */
    public List<CdoSnapshot> create(ObjectNode currentVersion){
        Validate.argumentIsNotNull(currentVersion);
        SnapshotVisitor visitor = new SnapshotVisitor();
        currentVersion.accept(visitor);

        return reuse(visitor.output);
    }

    private List<CdoSnapshot> reuse(List<CdoSnapshot> freshSnapshots){
        List<CdoSnapshot> reused = new ArrayList<>();
        for (CdoSnapshot fresh : freshSnapshots){

            Optional<CdoSnapshot> existing = javersRepository.getLatest(fresh.getGlobalId());
            if (existing.isEmpty()){
                reused.add(fresh);
                continue;
            }

            if (!existing.get().stateEquals(fresh)){
                reused.add(fresh);
            }
        }

        return reused;
    }

    private class SnapshotVisitor extends GraphVisitor{
        final List<CdoSnapshot> output = new ArrayList<>();

        @Override
        public void visitOnce(ObjectNode node) {
            CdoSnapshot thisNode = snapshotFactory.create(node);
            output.add(thisNode);
        }
    }
}
