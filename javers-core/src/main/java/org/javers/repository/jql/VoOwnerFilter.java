package org.javers.repository.jql;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.EntityType;

/**
 * @author bartosz.walacik
 */
class VoOwnerFilter extends Filter {

    private final EntityType ownerEntity;
    private final String path;

    public VoOwnerFilter(EntityType ownerEntity, String path) {
        this.ownerEntity = ownerEntity;
        this.path = path;
    }

    public EntityType getOwnerEntity() {
        return ownerEntity;
    }

    public String getPath() {
        return path;
    }

    @Override
    boolean matches(GlobalId globalId) {
      return false; //TODO
    }
}
