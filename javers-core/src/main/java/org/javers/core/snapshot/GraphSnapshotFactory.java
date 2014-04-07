package org.javers.core.snapshot;

import org.javers.common.validation.Validate;
import org.javers.core.graph.GraphVisitor;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Decomposes given object graph into flat list of object Snapshots.
 * Resulting structure can be easily serialized and persisted.
 * <br/>
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
     * @param node graph 'root', outcome from {@link ObjectGraphBuilder#buildGraph(Object)}
     */
    public List<CdoSnapshot> create(ObjectNode node){
        Validate.argumentIsNotNull(node);
        SnapshotVisitor visitor = new SnapshotVisitor();
        node.accept(visitor);

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
