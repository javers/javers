package org.javers.core.json.typeadapter.change;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ContainerElementChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;
import java.util.Optional;

/**
 * @author bartosz walacik
 */
class SetChangeTypeAdapter extends ContainerChangeTypeAdapter<SetChange> {

    public SetChangeTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    protected ContainerChange newInstance(PropertyChangeStub stub, List<ContainerElementChange> changes, CommitMetadata commitMetadata) {
        return new SetChange(stub.id, stub.getPropertyName(), changes, Optional.ofNullable(commitMetadata));
    }

    @Override
    public Class getValueType() {
        return SetChange.class;
    }
}