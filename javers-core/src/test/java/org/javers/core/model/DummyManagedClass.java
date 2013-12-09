package org.javers.core.model;

import javax.persistence.Id;

public class DummyManagedClass {

    @Id
    private int getId() {
        return 0;
    }
}
