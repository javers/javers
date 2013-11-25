package org.javers.core.diff.visitors;

import org.javers.model.object.graph.*;

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

    private void visit(SingleEdge edge) {
        if (singleEdgesProjection.containsKey(edge)) {
            singleEdgesProjection.put(edge, edge);
        } else {
            singleEdgesProjection.put(edge, null);
        }
    }

    private void visit(MultiEdge edge) {
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

    @Override
    public void visit(Object object) {
        if(object instanceof SingleEdge) {
            visit((SingleEdge)object);
        } else if (object instanceof MultiEdge) {
            visit((MultiEdge)object);
        }
    }
}