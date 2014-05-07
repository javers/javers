package org.javers.core.json.typeadapter.change;

import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class ArrayChangeTypeAdapter extends ContainerChangeTypeAdapter<ArrayChange> {

    public ArrayChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    protected ContainerChange newInstance(PropertyChangeStub stub, List<ContainerElementChange> changes) {
        return new ArrayChange(stub.id, stub.property, changes);
    }

    @Override
    public Class getValueType() {
        return ArrayChange.class;
    }
}


