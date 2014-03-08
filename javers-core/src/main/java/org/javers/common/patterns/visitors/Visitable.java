package org.javers.common.patterns.visitors;

public interface Visitable<VISITOR> {

  void accept(VISITOR visitor);
}
