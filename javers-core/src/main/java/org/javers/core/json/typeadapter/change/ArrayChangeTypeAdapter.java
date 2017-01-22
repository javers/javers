package org.javers.core.json.typeadapter.change;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * @author bartosz walacik
 */
class ArrayChangeTypeAdapter extends ContainerChangeTypeAdapter<ArrayChange> {

    public ArrayChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    protected ContainerChange newInstance(PropertyChangeStub stub, List<ContainerElementChange> changes, CommitMetadata commitMetadata) {
        return new ArrayChange(stub.id, stub.getPropertyName(), changes, ofNullable(commitMetadata));
    }

    @Override
    public Class getValueType() {
        return ArrayChange.class;
    }
}


