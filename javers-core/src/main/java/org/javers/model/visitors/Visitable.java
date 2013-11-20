package org.javers.model.visitors;

public interface Visitable {

  void accept(Visitor visitor);
}
