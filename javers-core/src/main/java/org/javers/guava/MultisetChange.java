package org.javers.guava;

import org.javers.core.diff.changetype.container.CollectionChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ValueAddOrRemove;
import org.javers.core.metamodel.object.GlobalId;

import java.util.List;

import static org.javers.common.validation.Validate.conditionFulfilled;

/**
 * @author akrystian
 */
public class MultisetChange extends CollectionChange{

    public MultisetChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes) {
        super(affectedCdoId, propertyName, changes);
        for (ContainerElementChange change: changes){
            conditionFulfilled(change instanceof ValueAddOrRemove, "MultisetChange constructor failed, expected ValueAddOrRemove");
            conditionFulfilled(change.getIndex() == null, "MultisetChange constructor failed, expected empty change.index");
        }
    }
}
