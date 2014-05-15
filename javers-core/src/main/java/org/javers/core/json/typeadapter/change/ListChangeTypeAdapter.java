package org.javers.core.json.typeadapter.change;

import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class ListChangeTypeAdapter extends ContainerChangeTypeAdapter<ListChange> {

    public ListChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    protected ContainerChange newInstance(PropertyChangeStub stub, List<ContainerElementChange> changes) {
        return new ListChange(stub.id, stub.property, changes);
    }

    @Override
    public Class getValueType() {
        return ListChange.class;
    }
}
