package org.javers.model.visitors;

public interface Visitable<VISITOR> {

  void accept(VISITOR visitor);
}
