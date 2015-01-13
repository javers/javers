package org.javers.core.diff.changetype.container;

import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public final class ArrayChange extends ContainerChange {

    public ArrayChange(GlobalId affectedCdoId, Property property, List<ContainerElementChange> changes) {
        super(affectedCdoId, property, changes);
    }
}
