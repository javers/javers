package org.javers.model.object.graph.visitors;

import org.javers.model.object.graph.MultiEdge;
import org.javers.model.object.graph.SingleEdge;
import org.javers.model.visitors.Visitor;

public interface EdgeVisitor extends Visitor {

  void visit(SingleEdge edge);

  void visit(MultiEdge edge);
}
