package org.javers.core.json;

import com.google.gson.*;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.json.typeadapter.commit.CdoSnapshotStateDeserializer;
import org.javers.core.metamodel.object.CdoSnapshotState;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.TypeMapper;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.lang.reflect.Type;

/**
 * Javers is meant to support various persistence stores for
 * any kind of client's data, hence we use JSON format to serialize/deserialize client's domain objects.
 * <br><br>
 *
 * Javers uses <a href='http://sites.google.com/site/gson/'>Gson</a>
 * library which provides neat and pretty JSON representation for well known Java types.
 * <br><br>
 *
 * But sometimes Gson's default JSON representation isn't appropriate for your domain model.
 * This is often the case when dealing with Values like Date or Money.
 * <br><br>
 *
 * If so, you can easily customize Javers serialization/deserialization behaviour
 * by providing plugins for each of your custom type.
 * Javers accepts several kind of plugins:
 * <ul>
 *     <li> {@link BasicStringTypeAdapter} -
 *           extend it if you need to represent Value as single String and don't want to deal with JSON API.
 *           For concrete class example see {@link org.javers.java8support.LocalDateTimeTypeAdapter}.
 *     <li> {@link JsonTypeAdapter} - use it if you need full control over JSON conversion
 *     <li> native Gson {@link TypeAdapter}
 *     <li> native Gson {@link JsonSerializer}
 *     <li> native Gson {@link JsonDeserializer}
 * </ul>
 *
 * Javers provides JsonTypeAdapters for some well known Values like Joda {@link LocalDateTime},
 * Joda {@link LocalDate}, java.time.LocalDate, java.time.LocalDateTime
 *
 * @author bartosz walacik
 */
public class JsonConverter {
    private Gson gson;
    private final CdoSnapshotStateDeserializer stateDeserializer;
    private final TypeMapper typeMapper;
    private final GlobalIdFactory globalIdFactory;

    JsonConverter(TypeMapper typeMapper, GlobalIdFactory globalIdFactory, Gson gson) {
        Validate.argumentsAreNotNull(typeMapper, gson);
        this.gson = gson;

        JsonDeserializationContext deserializationContext =  new JsonDeserializationContext() {
            @SuppressWarnings("unchecked")
            public <T> T deserialize(JsonElement json, Type typeOfT) throws JsonParseException {
                return (T) fromJson(json, typeOfT);
            }
        };
        this.stateDeserializer = new CdoSnapshotStateDeserializer(typeMapper, deserializationContext);
        this.typeMapper = typeMapper;
        this.globalIdFactory = globalIdFactory;
    }

    public String toJson(Object value) {
        return gson.toJson(value);
    }

    public JsonElement toJsonElement(Object value) {
        return gson.toJsonTree(value);
    }

    public <T> T fromJson(String json, Class<T> expectedType){
        return gson.fromJson(json, expectedType);
    }

    public Object fromJson(String json, Type expectedType) {
        return gson.fromJson(json, expectedType);
    }

    public Object fromJson(JsonElement json, Type expectedType) {
        return gson.fromJson(json, expectedType);
    }

    public GlobalId fromDto(GlobalIdRawDTO globalIdDTO) {
        Validate.argumentIsNotNull(globalIdDTO);

        if (globalIdDTO.isInstanceId()){
            EntityType entity = typeMapper.getJaversManagedType(globalIdDTO.getTypeName(), EntityType.class);
            Object cdoId = fromJson(globalIdDTO.getLocalIdJSON(), entity.getIdProperty().getType());
            return globalIdFactory.createInstanceId(cdoId, entity);
        } else if (globalIdDTO.isValueObjectId()){
            GlobalId ownerId = fromDto(globalIdDTO.getOwnerId());
            return globalIdFactory.createValueObjectIdFromPath(ownerId, globalIdDTO.getFragment());
        } else {
            return new UnboundedValueObjectId(globalIdDTO.getTypeName());
        }
    }

    public ManagedType getManagedType(GlobalId globalId){
        Validate.argumentsAreNotNull(globalId);
        return typeMapper.getJaversManagedType(globalId);
    }

    public CdoSnapshotState snapshotStateFromJson(String json, GlobalId globalId){
        Validate.argumentsAreNotNull(json, globalId);
        JsonElement stateElement = fromJson(json, JsonElement.class);

        ManagedType managedType = typeMapper.getJaversManagedType(globalId);
        return stateDeserializer.deserialize(stateElement, managedType);
    }
}
