package org.javers.core.json.typeadapter;

import com.google.gson.JsonArray;
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
public class CdoSnapshotTypeAdapter extends JsonTypeAdapterTemplate<CdoSnapshot> {

    public static final String GLOBAL_CDO_ID = "globalCdoId";
    public static final String COMMIT_ID = "commitId";
    public static final String STATE = "state";

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

        CommitId commitId = parseCommitId(jsonObject);
        GlobalCdoId cdoId = context.deserialize(jsonObject.get(GLOBAL_CDO_ID), GlobalCdoId.class);

        CdoSnapshotBuilder cdoSnapshotBuilder = cdoSnapshot(cdoId);
        cdoSnapshotBuilder.withCommitId(commitId);

        JsonObject state = jsonObject.get(STATE).getAsJsonObject();

        for (Property property : cdoId.getCdoClass().getProperties()) {
            cdoSnapshotBuilder.withPropertyValue(property, decodeValue(state, context, property));
        }

        return cdoSnapshotBuilder.build();
    }

    private CommitId parseCommitId(JsonObject json) {
        String majorDotMinor = json.get(COMMIT_ID).getAsString();

        String[] strings = majorDotMinor.split("\\.");

        if (strings.length != 2) {
            throw new JaversException(JaversExceptionCode.CANNOT_PARSE_COMMIT_ID, majorDotMinor);
        }

        long major = Long.parseLong(strings[0]);
        int minor = Integer.parseInt(strings[1]);

        return new CommitId(major, minor);
    }

    private Object decodeValue(JsonObject state, final JsonDeserializationContext context, Property property) {

        if (isFullyParametrizedCollection(property)) {
            return decodeCollection(state, context, property);
        } else if (property.getType().isArray()) {
            return decodeArray(state, context, property);
        } else if (isFullyParametrizedMap(property)) {
            return decodeMap(state, context, property);
        }

        return context.deserialize(state.get(property.getName()), property.getType());
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
            result.add(context.deserialize(iterator.next(), collectionType.getItemClass()));
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
            Array.set(result, i, context.deserialize(iterator.next(), arrayType.getItemClass()));
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
            result.put(entry.getKey(), context.deserialize(entry.getValue(), mapType.getValueClass()));
        }

        return result;
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

        for (Property property : snapshot.getProperties()) {
           jsonObject.add(property.getName(), context.serialize(snapshot.getPropertyValue(property)));
        }

        return jsonObject;
    }

}
