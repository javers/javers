package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class ArrayChange extends ContainerChange {

    public ArrayChange(GlobalCdoId affectedCdoId, Property property, List<ContainerValueChange> changes) {
        super(affectedCdoId, property, changes);
    }
}
