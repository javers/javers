package org.javers.core.json.typeadapter;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoSnapshotBuilder;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.PropertyScanner;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.Map;

import static org.javers.core.metamodel.object.CdoSnapshotBuilder.cdoSnapshot;

public class CdoSnapshotTypeAdapter extends JsonTypeAdapterTemplate<CdoSnapshot> {

    public static final String GLOBAL_CDO_ID = "globalCdoId";
    public static final String COMMIT_ID = "commitId";
    public static final String STATE = "state";
    private final PropertyScanner propertyScanner;

    private TypeMapper typeMapper;

    public CdoSnapshotTypeAdapter(TypeMapper typeMapper, PropertyScanner propertyScanner) {
        this.typeMapper = typeMapper;
        this.propertyScanner = propertyScanner;
    }

    @Override
    public Class getValueType() {
        return CdoSnapshot.class;
    }

    @Override
    public CdoSnapshot fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;

        CommitId commitId = parseCommitId(jsonObject);
        GlobalCdoId cdoId = context.deserialize(jsonObject.get(GLOBAL_CDO_ID), GlobalCdoId.class);

        CdoSnapshotBuilder cdoSnapshotBuilder = cdoSnapshot(cdoId);
        cdoSnapshotBuilder.withCommitId(commitId);

        JsonObject state = jsonObject.get(STATE).getAsJsonObject();

        for (Property property : propertyScanner.scan(cdoId.getCdoClass().getSourceClass())) {
            cdoSnapshotBuilder.withPropertyValue(property, context.deserialize(state.get(property.getName()), property.getType()));
        }

        return cdoSnapshotBuilder.build();
    }

    private CommitId parseCommitId(JsonObject json) {
        String majorDotMinor = json.get(COMMIT_ID).getAsString();

        String[] strings = majorDotMinor.split("\\.");

        if (strings.length != 2) {
            throw new RuntimeException("cannot parse");
        }

        long major = Long.parseLong(strings[0]);
        int minor = Integer.parseInt(strings[1]);

        return new CommitId(major, minor);
    }

    @Override
    public JsonElement toJson(CdoSnapshot snapshot, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty(COMMIT_ID, snapshot.getCommitId().value());
        jsonObject.add(GLOBAL_CDO_ID, context.serialize(snapshot.getGlobalId()));
        jsonObject.add(STATE, getState(snapshot, context));

        return jsonObject;
    }

    private JsonElement getState(CdoSnapshot snapshot, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Map.Entry<Property, Object> entry : snapshot.getState().entrySet()) {

            if (typeMapper.isPrimitiveOrValue(entry.getKey().getType())) {
                appendPrimitiveOrValue(jsonObject, entry);
            } else {// entity, value object, collection
                jsonObject.add(entry.getKey().getName(), context.serialize(entry.getValue()));
            }
        }

        return jsonObject;
    }

    private void appendPrimitiveOrValue(JsonObject jsonObject, Map.Entry<Property, Object> entry) {
        String keyName = entry.getKey().getName();
        Object value = entry.getValue();

        if (value instanceof String) {
            jsonObject.addProperty(keyName, (String) value);
        } else if (value instanceof Number) {
            jsonObject.addProperty(keyName, (Number) value);
        } else if (value instanceof Boolean) {
            jsonObject.addProperty(keyName, (Boolean) value);
        } else if (value instanceof Character) {
            jsonObject.addProperty(keyName, (Character) value);
        } else {
            throw new JaversException(JaversExceptionCode.CLASS_NOT_FOUND);
        }
    }
}
