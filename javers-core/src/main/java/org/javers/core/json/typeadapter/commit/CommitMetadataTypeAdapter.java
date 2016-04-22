package org.javers.core.json.typeadapter.commit;

import com.google.gson.*;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.joda.time.LocalDateTime;

import java.util.Collections;
import java.util.Map;

public class CommitMetadataTypeAdapter extends JsonTypeAdapterTemplate<CommitMetadata> {

    static final String AUTHOR = "author";
    static final String PROPERTIES = "properties";
    static final String COMMIT_DATE = "commitDate";
    static final String COMMIT_ID = "id";

    @Override
    public Class getValueType() {
        return CommitMetadata.class;
    }

    @Override
    public CommitMetadata fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        String author = jsonObject.get(AUTHOR).getAsString();
        Map<String, String> properties = context.deserialize(jsonObject.get(PROPERTIES), Map.class);
        if (properties == null) properties = Collections.emptyMap();
        LocalDateTime commitDate = context.deserialize(jsonObject.get(COMMIT_DATE), LocalDateTime.class);
        CommitId id = context.deserialize(jsonObject.get(COMMIT_ID), CommitId.class);
        return new CommitMetadata(author, properties, commitDate, id);
    }

    @Override
    public JsonElement toJson(CommitMetadata commitMetadata, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(AUTHOR, commitMetadata.getAuthor());
        jsonObject.add(PROPERTIES, context.serialize(commitMetadata.getProperties()));
        jsonObject.add(COMMIT_DATE, context.serialize(commitMetadata.getCommitDate(), LocalDateTime.class));
        jsonObject.add(COMMIT_ID, context.serialize(commitMetadata.getId()));
        return jsonObject;
    }
}
