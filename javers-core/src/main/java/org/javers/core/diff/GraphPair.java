package org.javers.core.diff;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.graph.ObjectGraph;
import org.javers.core.graph.ObjectNode;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.javers.common.collections.Sets.difference;

/**
 * @author bartosz walacik
 */
public class GraphPair {

    private final ObjectGraph leftGraph;
    private final ObjectGraph rightGraph;

    private final Collection<ObjectNode> onlyOnLeft;
    private final Collection<ObjectNode> onlyOnRight;

    private final Optional<CommitMetadata> commitMetadata;

    GraphPair(ObjectGraph leftGraph, ObjectGraph rightGraph) {
        this(leftGraph, rightGraph, Optional.empty());
    }

    public GraphPair(ObjectGraph leftGraph, ObjectGraph rightGraph, Optional<CommitMetadata> commitMetadata) {
        this.leftGraph = leftGraph;
        this.rightGraph = rightGraph;

        Function<ObjectNode, Integer> hasher = objectNode -> objectNode.cdoHashCode();

        this.onlyOnLeft = difference(leftGraph.nodes(), rightGraph.nodes(), hasher);
        this.onlyOnRight = difference(rightGraph.nodes(), leftGraph.nodes(), hasher);

        this.commitMetadata = commitMetadata;
    }

    //for initial
    public GraphPair(ObjectGraph currentGraph) {
        this.leftGraph = new EmptyGraph();

        this.rightGraph = currentGraph;

        this.onlyOnLeft = Collections.emptySet();
        this.onlyOnRight = rightGraph.nodes();

        this.commitMetadata = Optional.empty();
    }

    public Collection<ObjectNode> getOnlyOnLeft() {
        return onlyOnLeft;
    }

    public Collection<ObjectNode> getOnlyOnRight() {
        return onlyOnRight;
    }

    public Set<ObjectNode> getLeftNodeSet() {
        return leftGraph.nodes();
    }

    public Set<ObjectNode> getRightNodeSet() {
        return rightGraph.nodes();
    }

    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }
}
