package org.javers.shadow

import org.javers.core.metamodel.annotation.Id

class ImmutableEntity {
    @Id
    private final int id

    int getId() {
        return id
    }
}
