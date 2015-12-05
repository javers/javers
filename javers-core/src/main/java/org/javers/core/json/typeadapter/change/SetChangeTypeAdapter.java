package org.javers.core.json.typeadapter.change;

import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

/**
 * @author bartosz walacik
 */
class SetChangeTypeAdapter extends ContainerChangeTypeAdapter<SetChange> {

    public SetChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    protected ContainerChange newInstance(PropertyChangeStub stub, List<ContainerElementChange> changes) {
        return new SetChange(stub.id, stub.getPropertyName(), changes);
    }

    @Override
    public Class getValueType() {
        return SetChange.class;
    }
}