package org.javers.core.model;

import org.javers.core.metamodel.annotation.Id;

public class DummyClassWithCyclicReference {
    DummyClassWithCyclicReference parent;

    @Id
    String name;
}
