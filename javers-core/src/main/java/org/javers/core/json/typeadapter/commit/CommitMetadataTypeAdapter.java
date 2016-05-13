package org.javers.core.json.typeadapter.commit;

import com.google.gson.*;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.joda.time.LocalDateTime;

import java.util.*;

class CommitMetadataTypeAdapter extends JsonTypeAdapterTemplate<CommitMetadata> {

    static final String AUTHOR = "author";
    static final String PROPERTIES = "properties";
    static final String COMMIT_DATE = "commitDate";
    static final String COMMIT_ID = "id";
    private static final String PROPERTY_KEY_FIELD = "key";
    private static final String PROPERTY_VALUE_FIELD = "value";

    @Override
    public Class getValueType() {
        return CommitMetadata.class;
    }

    @Override
    public CommitMetadata fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        String author = jsonObject.get(AUTHOR).getAsString();
        List<HashMap<String, String>> propertiesObjectList = context.deserialize(jsonObject.get(PROPERTIES), List.class);
        if (propertiesObjectList == null) propertiesObjectList = Collections.emptyList();
        Map<String, String> properties = toPropertiesMap(propertiesObjectList);
        LocalDateTime commitDate = context.deserialize(jsonObject.get(COMMIT_DATE), LocalDateTime.class);
        CommitId id = context.deserialize(jsonObject.get(COMMIT_ID), CommitId.class);
        return new CommitMetadata(author, properties, commitDate, id);
    }

    @Override
    public JsonElement toJson(CommitMetadata commitMetadata, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(AUTHOR, commitMetadata.getAuthor());
        ArrayList<JsonObject> propertiesObjectList = toJsonObjects(commitMetadata.getProperties());
        jsonObject.add(PROPERTIES, context.serialize(propertiesObjectList));
        jsonObject.add(COMMIT_DATE, context.serialize(commitMetadata.getCommitDate(), LocalDateTime.class));
        jsonObject.add(COMMIT_ID, context.serialize(commitMetadata.getId()));
        return jsonObject;
    }

    private Map<String, String> toPropertiesMap(List<HashMap<String, String>> propertiesObjectList) {
        Map<String, String> properties = new HashMap<>();
        for (Map<String, String> property : propertiesObjectList) {
            properties.put(property.get(PROPERTY_KEY_FIELD), property.get(PROPERTY_VALUE_FIELD));
        }
        return properties;
    }

    private ArrayList<JsonObject> toJsonObjects(Map<String, String> properties) {
        ArrayList<JsonObject> propertiesObjectList = new ArrayList<>();
        for (Map.Entry<String, String> metadata : properties.entrySet()) {
            JsonObject propertyObject = new JsonObject();
            propertyObject.add(PROPERTY_KEY_FIELD, new JsonPrimitive(metadata.getKey()));
            propertyObject.add(PROPERTY_VALUE_FIELD, new JsonPrimitive(metadata.getValue()));
            propertiesObjectList.add(propertyObject);
        }
        return propertiesObjectList;
    }
}
