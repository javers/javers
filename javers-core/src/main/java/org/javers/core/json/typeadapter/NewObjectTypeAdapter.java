package org.javers.core.json.typeadapter;

import org.javers.core.diff.changetype.NewObject;

public class NewObjectTypeAdapter extends AbstractChangeTypeAdapter<NewObject> {

    @Override
    public Class getValueType() {
        return NewObject.class;
    }
}
