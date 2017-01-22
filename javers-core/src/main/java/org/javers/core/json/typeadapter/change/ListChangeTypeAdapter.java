package org.javers.core.json.typeadapter.change;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

import static java.util.Optional.ofNullable;

/**
 * @author bartosz walacik
 */
class ListChangeTypeAdapter extends ContainerChangeTypeAdapter<ListChange> {

    public ListChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    protected ContainerChange newInstance(PropertyChangeStub stub, List<ContainerElementChange> changes, CommitMetadata commitMetadata) {
        return new ListChange(stub.id, stub.getPropertyName(), changes, ofNullable(commitMetadata));
    }

    @Override
    public Class getValueType() {
        return ListChange.class;
    }
}
