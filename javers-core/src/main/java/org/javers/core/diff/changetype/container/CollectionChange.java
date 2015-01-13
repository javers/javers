package org.javers.core.diff.changetype.container;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import java.util.List;

/**
 * @author bartosz walacik
 */
public abstract class CollectionChange extends ContainerChange {
    public CollectionChange(GlobalId affectedCdoId, Property property, List<ContainerElementChange> changes) {
        super(affectedCdoId, property, changes);
    }
}
