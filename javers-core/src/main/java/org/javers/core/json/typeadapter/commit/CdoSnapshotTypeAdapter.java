package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonTypeAdapterTemplate;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoSnapshotBuilder;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import static org.javers.core.metamodel.object.CdoSnapshotBuilder.cdoSnapshot;

/**
 * @author pawel szymczyk
 */
class CdoSnapshotTypeAdapter extends JsonTypeAdapterTemplate<CdoSnapshot> {

    public static final String GLOBAL_CDO_ID = "globalId";
    public static final String COMMIT_METADATA = "commitMetadata";
    public static final String STATE_NAME = "state";
    public static final String INITIAL_NAME_LEGACY = "initial";
    public static final String TYPE_NAME = "type";

    private TypeMapper typeMapper;

    public CdoSnapshotTypeAdapter(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public Class getValueType() {
        return CdoSnapshot.class;
    }

    @Override
    public CdoSnapshot fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;

        CommitMetadata commitMetadata = context.deserialize(((JsonObject) json).get(COMMIT_METADATA), CommitMetadata.class);

        GlobalId cdoId = context.deserialize(jsonObject.get(GLOBAL_CDO_ID), GlobalId.class);

        CdoSnapshotBuilder cdoSnapshotBuilder = cdoSnapshot(cdoId, commitMetadata);
        cdoSnapshotBuilder.withCommitMetadata(commitMetadata);

        deserializeType(jsonObject,cdoSnapshotBuilder);

        JsonObject state = jsonObject.get(STATE_NAME).getAsJsonObject();

        for (Property property : cdoId.getCdoClass().getProperties()) {
            cdoSnapshotBuilder.withPropertyValue(property, decodeValue(state, context, property));
        }

        return cdoSnapshotBuilder.build();
    }

    private void deserializeType(JsonObject jsonObject, CdoSnapshotBuilder cdoSnapshotBuilder){
        JsonElement initial = jsonObject.get(INITIAL_NAME_LEGACY);
        if (initial != null){ //for legacy JSON's
            cdoSnapshotBuilder.withInitial(initial.getAsBoolean());
            return;
        }

        JsonElement type = jsonObject.get(TYPE_NAME);
        if (type != null) {
            cdoSnapshotBuilder.withType(SnapshotType.valueOf(type.getAsString()));
        }
    }

    private Object decodeValue(JsonObject state, final JsonDeserializationContext context, Property property) {
        if (isFullyParametrizedCollection(property)) {
            return decodeCollection(state, context, property);
        } else if (property.getType().isArray()) {
            return decodeArray(state, context, property);
        } else if (isFullyParametrizedMap(property)) {
            return decodeMap(state, context, property);
        }

        return contextDeserialize(context, state.get(property.getName()), property.getType());
    }

    private boolean isFullyParametrizedMap(Property property) {
        JaversType javersType = typeMapper.getPropertyType(property);
        return javersType instanceof MapType && ((MapType)javersType).isFullyParametrized();
    }

    private boolean isFullyParametrizedCollection(Property property) {
        JaversType javersType = typeMapper.getPropertyType(property);
        return javersType instanceof CollectionType && ((CollectionType)javersType).isFullyParametrized();
    }

    private Collection decodeCollection(JsonObject state, JsonDeserializationContext context, Property property) {
        JsonArray collectionAsJsonObject = (JsonArray) state.get(property.getName());

        if (collectionAsJsonObject == null) {
            return null;
        }

        CollectionType collectionType = typeMapper.getPropertyType(property);

        Collection result = newInstanceOf(collectionType);

        Iterator<JsonElement> iterator = collectionAsJsonObject.iterator();
        while (iterator.hasNext()) {
            result.add(contextDeserialize(context, iterator.next(), collectionType.getItemClass()));
        }

        return result;
    }

    private Collection newInstanceOf(CollectionType collectionType) {
        if (collectionType instanceof SetType) {
            return new HashSet();
        } else { //ListType
            return new ArrayList();
        }
    }

    private Object decodeArray(JsonObject state, JsonDeserializationContext context, Property property) {
        JsonArray mapAsJsonObject = (JsonArray) state.get(property.getName());

        if (mapAsJsonObject == null) {
            return null;
        }

        ArrayType arrayType = typeMapper.getPropertyType(property);

        Object result = Array.newInstance(arrayType.getItemClass(), mapAsJsonObject.size());

        Iterator<JsonElement> iterator = mapAsJsonObject.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Array.set(result, i, contextDeserialize(context, iterator.next(), arrayType.getItemClass()));
            i++;
        }

        return result;
    }

    private Map decodeMap(JsonObject state, JsonDeserializationContext context, Property property) {
        JsonObject mapAsJsonObject = (JsonObject) state.get(property.getName());

        if (mapAsJsonObject == null) {
            return null;
        }

        MapType mapType = typeMapper.getPropertyType(property);

        Map result = new HashMap();

        for (Map.Entry<String, JsonElement> entry : mapAsJsonObject.entrySet()) {
            result.put(entry.getKey(), contextDeserialize(context, entry.getValue(), mapType.getValueClass()));
        }

        return result;
    }

    private Object contextDeserialize(JsonDeserializationContext context, JsonElement element, Class clazz) {
        return context.deserialize(element, typeMapper.getDehydratedType(clazz));
    }

    @Override
    public JsonElement toJson(CdoSnapshot snapshot, JsonSerializationContext context) {

        JsonObject jsonObject = new JsonObject();

        jsonObject.add(COMMIT_METADATA, context.serialize(snapshot.getCommitMetadata()));
        jsonObject.add(GLOBAL_CDO_ID, context.serialize(snapshot.getGlobalId()));
        jsonObject.add(STATE_NAME, getState(snapshot, context));
        jsonObject.add(TYPE_NAME, context.serialize(snapshot.getType().name()));

        return jsonObject;
    }

    private JsonElement getState(CdoSnapshot snapshot, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        for (Property property : snapshot.getProperties()) {
           jsonObject.add(property.getName(), context.serialize(snapshot.getPropertyValue(property)));
        }

        return jsonObject;
    }

}
