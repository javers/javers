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

        this.onlyOnLeft = difference(leftGraph.flatten(), rightGraph.flatten());
        this.onlyOnRight = difference(rightGraph.flatten(), leftGraph.flatten());
    }

    //for initial
    public GraphPair(ObjectGraph currentGraph) {
        this.leftGraph = new ObjectGraph(){
            public Set<ObjectNode> flatten() {
                return  Collections.EMPTY_SET;
            }
        };

        this.rightGraph = currentGraph;

        this.onlyOnLeft = Collections.EMPTY_SET;
        this.onlyOnRight = rightGraph.flatten();
    }

    public Set<ObjectNode> getOnlyOnLeft() {
        return onlyOnLeft;
    }

    public Set<ObjectNode> getOnlyOnRight() {
        return onlyOnRight;
    }

    public Set<ObjectNode> getLeftNodeSet() {
        return leftGraph.flatten();
    }

    public Set<ObjectNode> getRightNodeSet() {
        return rightGraph.flatten();
    }

}
