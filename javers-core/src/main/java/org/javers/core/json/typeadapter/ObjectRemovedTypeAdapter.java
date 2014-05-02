package org.javers.core.json.typeadapter;

import org.javers.core.diff.changetype.ObjectRemoved;

public class ObjectRemovedTypeAdapter extends AbstractChangeTypeAdapter<ObjectRemoved> {

    @Override
    public Class getValueType() {
        return ObjectRemoved.class;
    }
}
