package org.javers.repository.jql;

import org.javers.common.string.ToStringBuilder;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.ValueObjectId;
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
        if (!(globalId instanceof ValueObjectId)) {
          return false;
        }

        ValueObjectId valueObjectId = (ValueObjectId) globalId;

        return valueObjectId.getOwnerId().getTypeName().equals(ownerEntity.getName())
            &&valueObjectId.getFragment().equals(path);
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this,
                "ownerEntity", ownerEntity,
                "path", path);
    }
}
