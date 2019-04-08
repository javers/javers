package org.javers.core.diff;

import static org.javers.common.collections.Sets.difference;

import java.util.Collections;
import java.util.Set;
import org.javers.core.graph.ObjectNode;

/**
 * @author bartosz walacik
 */
public class GraphPair {

    private final ObjectGraph leftGraph;
    private final ObjectGraph rightGraph;

    private final Set<ObjectNode> onlyOnLeft;
    private final Set<ObjectNode> onlyOnRight;

    public GraphPair(ObjectGraph leftGraph, ObjectGraph rightGraph) {
        this.leftGraph = leftGraph;
        this.rightGraph = rightGraph;

        this.onlyOnLeft = difference(leftGraph.nodes(), rightGraph.nodes());
        this.onlyOnRight = difference(rightGraph.nodes(), leftGraph.nodes());
    }

    private GraphPair(final ObjectGraph leftGraph, final ObjectGraph rightGraph, final Set<ObjectNode> leftNodes, final Set<ObjectNode> rightNodes) {
        this.leftGraph = leftGraph;
        this.rightGraph = rightGraph;
        this.onlyOnLeft = leftNodes;
        this.onlyOnRight = rightNodes;
    }

    public static GraphPair getInsertGraphPair(final ObjectGraph currentGraph){
        return new GraphPair(new EmptyGraph(), currentGraph, Collections.emptySet(), currentGraph.nodes());
    }

    public static GraphPair getDeleteGraphPair(final ObjectGraph previousGraph) {
        return new GraphPair(previousGraph, new EmptyGraph(), previousGraph.nodes(), Collections.emptySet());
    }

    private static class EmptyGraph extends ObjectGraph {

        EmptyGraph() {
            super(Collections.emptySet());
        }
    }

    public Set<ObjectNode> getOnlyOnLeft() {
        return onlyOnLeft;
    }

    public Set<ObjectNode> getOnlyOnRight() {
        return onlyOnRight;
    }

    public Set<ObjectNode> getLeftNodeSet() {
        return leftGraph.nodes();
    }

    public Set<ObjectNode> getRightNodeSet() {
        return rightGraph.nodes();
    }

}
