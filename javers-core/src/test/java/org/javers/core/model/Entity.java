package org.javers.core.model;

import org.javers.core.metamodel.annotation.Id;

/**
 * Created by Ian Agius
 */
public class Entity extends AbstractEntity<Long> {
    private Long id;

    public Entity(Long id) {
        this.id = id;
    }

    @Id
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
