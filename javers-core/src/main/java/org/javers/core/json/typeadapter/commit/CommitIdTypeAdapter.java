package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonTypeAdapterTemplate;

class CommitIdTypeAdapter extends JsonTypeAdapterTemplate<CommitId> {

    @Override
    public Class getValueType() {
        return CommitId.class;
    }

    @Override
    public CommitId fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        String majorDotMinor = json.getAsString();
        return CommitId.valueOf(majorDotMinor);
    }

    @Override
    public JsonElement toJson(CommitId commitId, JsonSerializationContext context) {
        return new JsonPrimitive(commitId.value());
    }
}
