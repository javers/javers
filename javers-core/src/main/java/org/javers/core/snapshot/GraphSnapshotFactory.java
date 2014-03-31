package org.javers.core.snapshot;

import org.javers.common.collections.Multimap;
import org.javers.common.validation.Validate;
import org.javers.core.graph.GraphVisitor;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.ValueObjectSetId;

/**
 * Decomposes given object graph into flat list of object Snapshots.
 * Resulting structure can be easily serialized and persisted.
 * <br/>
 * In fact, due to case of {@link ValueObjectSetId}, resulting structure
 * is a Multimap of GlobalCdoId -> Set&lt;CdoSnapshot&gt;
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
    public Multimap<GlobalCdoId, CdoSnapshot> create(ObjectNode node){
        Validate.argumentIsNotNull(node);
        SnapshotVisitor visitor = new SnapshotVisitor();
        node.accept(visitor);

        return visitor.getOutput();
    }

    private class SnapshotVisitor extends GraphVisitor{
        private final Multimap<GlobalCdoId, CdoSnapshot> output = new Multimap<>();

        @Override
        protected void visitOnce(ObjectNode node) {
            CdoSnapshot thisNode = snapshotFactory.create(node);
            output.put(node.getGlobalCdoId(), thisNode);
        }

        public Multimap<GlobalCdoId, CdoSnapshot> getOutput() {
            return output;
        }
    }
}
