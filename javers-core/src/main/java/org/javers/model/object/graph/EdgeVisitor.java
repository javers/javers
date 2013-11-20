package org.javers.model.object.graph;

import org.javers.model.visitors.Visitor;

@Deprecated
public interface EdgeVisitor extends Visitor {

    void visit(SingleEdge edge);

    void visit(ObjectNode objectNodee);

    void visit(MultiEdge edge);
}
