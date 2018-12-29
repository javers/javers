package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonTypeAdapterTemplate;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Map;

class CommitMetadataTypeAdapter extends JsonTypeAdapterTemplate<CommitMetadata> {

    static final String AUTHOR = "author";
    static final String PROPERTIES = "properties";
    static final String COMMIT_DATE = "commitDate";
    static final String COMMIT_DATE_INSTANT = "commitDateInstant";
    static final String COMMIT_ID = "id";

    @Override
    public Class getValueType() {
        return CommitMetadata.class;
    }

    @Override
    public CommitMetadata fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        String author = jsonObject.get(AUTHOR).getAsString();
        Map<String, String> properties = CommitPropertiesConverter.fromJson(jsonObject.get(PROPERTIES));
        LocalDateTime commitDate = context.deserialize(jsonObject.get(COMMIT_DATE), LocalDateTime.class);
        Instant commitDateInstant = context.deserialize(jsonObject.get(COMMIT_DATE_INSTANT), Instant.class);
        CommitId id = context.deserialize(jsonObject.get(COMMIT_ID), CommitId.class);
        return new CommitMetadata(author, properties, commitDate, commitDateInstant, id);
    }

    @Override
    public JsonElement toJson(CommitMetadata commitMetadata, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(AUTHOR, commitMetadata.getAuthor());
        jsonObject.add(PROPERTIES, CommitPropertiesConverter.toJson(commitMetadata.getProperties()));
        jsonObject.add(COMMIT_DATE, context.serialize(commitMetadata.getCommitDate(), LocalDateTime.class));
        jsonObject.add(COMMIT_DATE_INSTANT, context.serialize(commitMetadata.getCommitDateInstant(), Instant.class));
        jsonObject.add(COMMIT_ID, context.serialize(commitMetadata.getId()));
        return jsonObject;
    }
}
