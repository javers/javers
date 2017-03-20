package org.javers.shadow

import org.javers.core.metamodel.annotation.Id

class ImmutableEntity {
    @Id
    private final int id

    private final ImmutableEntity entityRef

    ImmutableEntity(int id, ImmutableEntity entityRef) {
        this.id = id
        this.entityRef = entityRef
    }

    int getId() {
        return id
    }

    ImmutableEntity getEntityRef() {
        return entityRef
    }
}
