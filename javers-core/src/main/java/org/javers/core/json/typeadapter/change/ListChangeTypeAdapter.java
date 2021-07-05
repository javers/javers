package org.javers.core.json.typeadapter.change;

import java.util.List;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.PropertyChangeMetadata;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.type.TypeMapper;

/**
 * @author bartosz walacik
 */
class ListChangeTypeAdapter extends ContainerChangeTypeAdapter<ListChange> {

    public ListChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    protected ContainerChange newInstance(PropertyChangeMetadata metadata, List<ContainerElementChange> changes,List<Object> leftValue, List<Object> rightValue) {
        return new ListChange(metadata, changes,new Atomic(leftValue), new Atomic(rightValue));
    }

    @Override
    public Class getValueType() {
        return ListChange.class;
    }
}
