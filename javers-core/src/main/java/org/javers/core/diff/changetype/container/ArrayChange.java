package org.javers.core.diff.changetype.container;

import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class ArrayChange extends ContainerChange {

    public ArrayChange(GlobalCdoId affectedCdoId, Property property, List<ContainerElementChange> changes) {
        super(affectedCdoId, property, changes);
    }
}
