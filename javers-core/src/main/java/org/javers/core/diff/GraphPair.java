package org.javers.core.diff;

import org.javers.core.graph.ObjectNode;
import java.util.Collections;
import java.util.Set;
import static org.javers.common.collections.Sets.difference;

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

    //for initial
    public GraphPair(ObjectGraph currentGraph) {
        this.leftGraph = new EmptyGraph();

        this.rightGraph = currentGraph;

        this.onlyOnLeft = Collections.emptySet();
        this.onlyOnRight = rightGraph.nodes();
    }

    private class EmptyGraph extends ObjectGraph {
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
