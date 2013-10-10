package org.javers.model.object.graph;

import org.javers.model.visitors.Visitor;

public interface EdgeVisitor extends Visitor {

    void visit(SingleEdge edge);

    void visit(MultiEdge edge);
}
