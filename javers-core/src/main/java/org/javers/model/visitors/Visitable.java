package org.javers.model.visitors;

public interface Visitable<V extends Visitor> {

  void accept(V visitor);
}
