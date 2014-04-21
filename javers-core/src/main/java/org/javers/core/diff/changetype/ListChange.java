package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class ListChange extends ContainerChange {

    public ListChange(GlobalCdoId affectedCdoId, Property property, List<ContainerValueChange> changes) {
        super(affectedCdoId, property, changes);
    }
}
