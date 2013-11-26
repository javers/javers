package org.javers.model.visitors;

public interface Visitor<VISITABLE> {

    void visit(VISITABLE object);
}
