package org.javers.core.json.typeadapter;

import org.javers.core.diff.changetype.NewObject;

public class NewObjectTypeAdapter extends ChangeTypeAdapter<NewObject> {

    @Override
    public Class getValueType() {
        return NewObject.class;
    }
}
