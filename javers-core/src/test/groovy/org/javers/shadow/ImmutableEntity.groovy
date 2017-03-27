package org.javers.shadow

import org.javers.core.metamodel.annotation.Id

class ImmutableEntity {
    @Id
    private final int id

    private final ImmutableEntity reference

    ImmutableEntity(int id) {
        this(id, null)
    }

    ImmutableEntity(int id, ImmutableEntity reference) {
        this.id = id
        this.reference = reference
    }

    @Id
    int getId() {
        id
    }

    ImmutableEntity getReference() {
        reference
    }
}
