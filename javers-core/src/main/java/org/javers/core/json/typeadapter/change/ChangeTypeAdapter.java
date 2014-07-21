package org.javers.core.json.typeadapter.change;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.*;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.property.Property;

import java.util.HashMap;
import java.util.Map;

public class ChangeTypeAdapter<T extends Change> extends JsonTypeAdapterTemplate<T> {

    private static final String CHANGE_TYPE_FIELD = "changeType";
    private static final String AFFECTED_CDO_ID_FIELD = "globalCdoId";
    private static final String PROPERTY_FIELD = "property";
    private static final String COMMIT_METADATA = "commitMetadata";

    private Map<String, Class<? extends Change>> changeTypeMap;

    public ChangeTypeAdapter() {
        this.changeTypeMap = new HashMap<>();
        initEntry(ValueChange.class);
        initEntry(ReferenceChange.class);
        initEntry(NewObject.class);
        initEntry(ObjectRemoved.class);
        initEntry(MapChange.class);
        initEntry(ListChange.class);
        initEntry(ArrayChange.class);
        initEntry(SetChange.class);
    }

    public T appendCommitMetadata(JsonObject jsonObject, JsonDeserializationContext context, T change) {
        if (jsonObject.has(COMMIT_METADATA)) {
            CommitMetadata commitMetadata = context.deserialize(jsonObject.get(COMMIT_METADATA), CommitMetadata.class);
            change.bindToCommit(commitMetadata);
        }

        return change;
    }

    @Override
    public T fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;
        String changeTypeField = jsonObject.get(CHANGE_TYPE_FIELD).getAsString();
        Class<? extends Change> changeType = decode(changeTypeField);

        return context.deserialize(json, changeType);
    }

    @Override
    public JsonElement toJson(T change, JsonSerializationContext context) {
        return createJsonObject(change, context);
    }

    protected PropertyChangeStub deserializeStub(JsonObject jsonObject, JsonDeserializationContext context) {
        GlobalCdoId id = deserializeAffectedCdoId(jsonObject, context);
        Property property = deserializeProperty(jsonObject, id);

        return new PropertyChangeStub(id, property);
    }

    protected GlobalCdoId deserializeAffectedCdoId(JsonObject jsonObject, JsonDeserializationContext context) {
        return context.deserialize(jsonObject.get(AFFECTED_CDO_ID_FIELD), GlobalCdoId.class);
    }

    private Property deserializeProperty(JsonObject jsonObject, GlobalCdoId id){
        String propertyName = jsonObject.get(PROPERTY_FIELD).getAsString();

        return id.getCdoClass().getProperty(propertyName);
    }

    protected JsonObject createJsonObject(T change, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CHANGE_TYPE_FIELD, encode(change.getClass()));
        jsonObject.add(AFFECTED_CDO_ID_FIELD, context.serialize(change.getAffectedCdoId()));

        if (change.getCommitMetadata().isPresent()) {
            jsonObject.add(COMMIT_METADATA, context.serialize(change.getCommitMetadata().get()));
        }

        if (change instanceof PropertyChange) {
            jsonObject.addProperty(PROPERTY_FIELD, ((PropertyChange) change).getProperty().getName());
        }
        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return Change.class;
    }

    protected class PropertyChangeStub{
        GlobalCdoId id;
        Property property;

        PropertyChangeStub(GlobalCdoId id, Property property) {
            this.id = id;
            this.property = property;
        }
    }

    private void initEntry(Class<? extends Change> valueChangeClass) {
        changeTypeMap.put(encode(valueChangeClass), valueChangeClass);
    }

    private String encode(Class<? extends Change> valueChangeClass) {
        return valueChangeClass.getSimpleName();
    }

    private Class<? extends Change> decode(String changeType){
        if (!changeTypeMap.containsKey(changeType)) {
            throw new JaversException(JaversExceptionCode.MALFORMED_CHANGE_TYPE_FIELD, changeType);
        }
        return changeTypeMap.get(changeType);
    }
}
