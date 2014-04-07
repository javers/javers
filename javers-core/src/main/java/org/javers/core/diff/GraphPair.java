package org.javers.core.diff;

import org.javers.core.graph.ObjectNode;

import java.util.Collections;
import java.util.Set;

import static org.javers.common.collections.Sets.difference;

/**
 * @author bartosz walacik
 */
public class GraphPair {

    private final Set<ObjectNode> leftNodeSet;
    private final Set<ObjectNode> rightNodeSet;

    private final Set<ObjectNode> onlyOnLeft;
    private final Set<ObjectNode> onlyOnRight;

    private final DFSGraphToSetConverter graphToSetConverter = new DFSGraphToSetConverter();

    public GraphPair(ObjectNode leftRoot, ObjectNode rightRoot) {
        this.leftNodeSet =  flatten(leftRoot);
        this.rightNodeSet = flatten(rightRoot);

        this.onlyOnLeft = difference(leftNodeSet, rightNodeSet);
        this.onlyOnRight = difference(rightNodeSet, leftNodeSet);
    }

    //for initial
    public GraphPair(ObjectNode root) {
        this.leftNodeSet = Collections.EMPTY_SET;
        this.rightNodeSet = flatten(root);

        this.onlyOnLeft = Collections.EMPTY_SET;
        this.onlyOnRight = difference(rightNodeSet, leftNodeSet);
    }

    public Set<ObjectNode> getOnlyOnLeft() {
        return onlyOnLeft;
    }

    public Set<ObjectNode> getOnlyOnRight() {
        return onlyOnRight;
    }

    public Set<ObjectNode> getLeftNodeSet() {
        return leftNodeSet;
    }

    public Set<ObjectNode> getRightNodeSet() {
        return rightNodeSet;
    }

    private Set<ObjectNode> flatten(ObjectNode leftRoot) {
        return graphToSetConverter.convertFromGraph(leftRoot);
    }
}
