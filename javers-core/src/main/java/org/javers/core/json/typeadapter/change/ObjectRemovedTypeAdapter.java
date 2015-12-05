package org.javers.core.json.typeadapter.change;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.javers.common.collections.Optional;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.metamodel.type.TypeMapper;

class ObjectRemovedTypeAdapter extends ChangeTypeAdapter<ObjectRemoved> {

    public ObjectRemovedTypeAdapter(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public ObjectRemoved fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;

        return appendCommitMetadata(jsonObject, context, new ObjectRemoved(deserializeAffectedCdoId(jsonObject,context),Optional.empty()));
    }

    @Override
    public Class getValueType() {
        return ObjectRemoved.class;
    }
}
