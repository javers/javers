package org.javers.core.snapshot;

import org.javers.common.validation.Validate;
import org.javers.core.graph.GraphVisitor;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;

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

    public GraphSnapshotFactory(SnapshotFactory snapshotFactory) {
        this.snapshotFactory = snapshotFactory;
    }

    /**
     *
     * @param currentVersion graph 'root', outcome from {@link ObjectGraphBuilder#buildGraph(Object)}
     */
    public List<CdoSnapshot> create(ObjectNode currentVersion){
        Validate.argumentIsNotNull(currentVersion);
        SnapshotVisitor visitor = new SnapshotVisitor();
        currentVersion.accept(visitor);

        return visitor.output;
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
