package org.javers.core.diff.changetype;

import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

import java.util.List;

/**
 * @author pawel szymczyk
 */
public class SetChange extends ContainerChange{

    public SetChange(GlobalCdoId affectedCdoId, Property property, List<ContainerValueChange> changes) {
        super(affectedCdoId, property, changes);
    }
}
