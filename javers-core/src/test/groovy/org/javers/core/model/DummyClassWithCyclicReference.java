package org.javers.core.model;

import org.javers.core.metamodel.annotation.Id;

public class DummyClassWithCyclicReference {
    public DummyClassWithCyclicReference2 child;

    @Id
    String name;
}
