package org.javers.common.patterns.visitors;

public interface Visitor<VISITABLE> {

    void visit(VISITABLE object);
}
