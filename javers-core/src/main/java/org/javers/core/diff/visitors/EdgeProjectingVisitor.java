package org.javers.core.diff.visitors;

import org.javers.model.object.graph.Edge;
import org.javers.model.object.graph.EdgeVisitor;
import org.javers.model.object.graph.MultiEdge;
import org.javers.model.object.graph.SingleEdge;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Maciej Zasada
 */
public class EdgeProjectingVisitor implements EdgeVisitor {
    private Map<SingleEdge, SingleEdge> singleEdgesProjection = new HashMap<>();
    private Map<MultiEdge, MultiEdge> multiEdgesProjection = new HashMap<>();

    public EdgeProjectingVisitor visit(Collection<Edge> leftEdges, Collection<Edge> rightEdges) {
        for (Edge edge : leftEdges) {
            edge.accept(this);
        }
        for (Edge edge : rightEdges) {
            edge.accept(this);
        }
        return this;
    }

    @Override
    public void visit(SingleEdge edge) {
        if (singleEdgesProjection.containsKey(edge)) {
            singleEdgesProjection.put(edge, edge);
        } else {
            singleEdgesProjection.put(edge, null);
        }
    }

    @Override
    public void visit(MultiEdge edge) {
        if (multiEdgesProjection.containsKey(edge)) {
            multiEdgesProjection.put(edge, edge);
        } else {
            multiEdgesProjection.put(edge, null);
        }
    }

    public Map<SingleEdge, SingleEdge> getSingleEdgesProjection() {
        return Collections.unmodifiableMap(singleEdgesProjection);
    }

    public Map<MultiEdge, MultiEdge> getMultiEdgesProjection() {
        return Collections.unmodifiableMap(multiEdgesProjection);
    }
}