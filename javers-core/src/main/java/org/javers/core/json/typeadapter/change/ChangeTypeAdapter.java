package org.javers.core.json.typeadapter.change;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.*;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.HashMap;
import java.util.Map;

class ChangeTypeAdapter<T extends Change> extends JsonTypeAdapterTemplate<T> {

    private static final String CHANGE_TYPE_FIELD = "changeType";
    private static final String AFFECTED_CDO_ID_FIELD = "globalId";
    private static final String PROPERTY_FIELD = "property";
    private static final String COMMIT_METADATA = "commitMetadata";

    private final Map<String, Class<? extends Change>> changeTypeMap;
    protected final TypeMapper typeMapper;

    public ChangeTypeAdapter(TypeMapper typeMapper) {
        this.changeTypeMap = new HashMap<>();
        this.typeMapper = typeMapper;
        initEntry(ValueChange.class);
        initEntry(ReferenceChange.class);
        initEntry(NewObject.class);
        initEntry(ObjectRemoved.class);
        initEntry(MapChange.class);
        initEntry(ListChange.class);
        initEntry(ArrayChange.class);
        initEntry(SetChange.class);
    }

    protected CommitMetadata deserializeCommitMetadata(JsonObject jsonObject, JsonDeserializationContext context) {
        return context.deserialize(jsonObject.get(COMMIT_METADATA), CommitMetadata.class);
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
        GlobalId id = deserializeAffectedCdoId(jsonObject, context);
        String propertyName = jsonObject.get(PROPERTY_FIELD).getAsString();

        ManagedType managedType = typeMapper.getJaversManagedType(id);
        return new PropertyChangeStub(id, managedType.getProperty(propertyName));
    }

    protected GlobalId deserializeAffectedCdoId(JsonObject jsonObject, JsonDeserializationContext context) {
        return context.deserialize(jsonObject.get(AFFECTED_CDO_ID_FIELD), GlobalId.class);
    }

    protected JsonObject createJsonObject(T change, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(CHANGE_TYPE_FIELD, encode(change.getClass()));
        jsonObject.add(AFFECTED_CDO_ID_FIELD, context.serialize(change.getAffectedGlobalId()));

        if (change.getCommitMetadata().isPresent()) {
            jsonObject.add(COMMIT_METADATA, context.serialize(change.getCommitMetadata().get()));
        }

        if (change instanceof PropertyChange) {
            jsonObject.addProperty(PROPERTY_FIELD, ((PropertyChange) change).getPropertyName());
        }
        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return Change.class;
    }

    protected class PropertyChangeStub{
        GlobalId id;
        JaversProperty property;

        PropertyChangeStub(GlobalId id, JaversProperty property) {
            this.id = id;
            this.property = property;
        }

        String getPropertyName(){
            return property.getName();
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
