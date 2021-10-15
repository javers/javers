package org.javers.core.json.typeadapter.change;

import java.util.HashSet;
import java.util.List;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.PropertyChangeMetadata;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.metamodel.type.TypeMapper;

/**
 * @author bartosz walacik
 */
class SetChangeTypeAdapter extends ContainerChangeTypeAdapter<SetChange> {

    public SetChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    protected ContainerChange newInstance(PropertyChangeMetadata metadata, List<ContainerElementChange> changes,List<Object> leftValue,List<Object> rightValue) {
        return new SetChange(metadata, changes, new Atomic(new HashSet<>(leftValue)), new Atomic(new HashSet<>(rightValue)));
    }

    @Override
    public Class getValueType() {
        return SetChange.class;
    }
}
