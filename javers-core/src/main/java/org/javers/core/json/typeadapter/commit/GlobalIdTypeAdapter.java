package org.javers.core.json.typeadapter.commit;

import com.google.gson.*;
import org.javers.common.collections.Lists;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.List;

/**
 * @author bartosz walacik
 */
class GlobalIdTypeAdapter implements JsonTypeAdapter<GlobalId> {
    static final String ENTITY_FIELD = "entity";
    static final String CDO_ID_FIELD = "cdoId";
    static final String OWNER_ID_FIELD = "ownerId";
    static final String VALUE_OBJECT_FIELD = "valueObject";
    static final String FRAGMENT_FIELD = "fragment";

    private final TypeMapper typeMapper;

    public GlobalIdTypeAdapter(TypeMapper typeMapper) {
        this.typeMapper = typeMapper;
    }

    @Override
    public GlobalId fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;

        if (jsonObject.get(ENTITY_FIELD) != null) {
            return parseInstanceId(jsonObject, context);
        } else if (jsonObject.get(OWNER_ID_FIELD) != null) {
            return parseValueObjectId(jsonObject, context);
        } else {
            return parseUnboundedValueObject(jsonObject);
        }
    }

    private UnboundedValueObjectId parseUnboundedValueObject(JsonObject jsonObject){
        String typeName = jsonObject.get(VALUE_OBJECT_FIELD).getAsString();
        return new UnboundedValueObjectId(typeName);
    }

    private ValueObjectId parseValueObjectId(JsonObject jsonObject, JsonDeserializationContext context) {
        String typeName = jsonObject.get(VALUE_OBJECT_FIELD).getAsString();
        String fragment = jsonObject.get(FRAGMENT_FIELD).getAsString();
        GlobalId ownerId = context.deserialize(jsonObject.get(OWNER_ID_FIELD), GlobalId.class);

        return new ValueObjectId(typeName, ownerId, fragment);
    }

    private InstanceId parseInstanceId(JsonObject jsonObject, JsonDeserializationContext context) {

        EntityType entity = parseEntity(jsonObject);

        JsonElement cdoIdElement = jsonObject.get(CDO_ID_FIELD);
        Object cdoId = context.deserialize(cdoIdElement, entity.getIdProperty().getGenericType());

        return entity.createIdFromLocalId(cdoId);
    }

    @Override
    public JsonElement toJson(GlobalId globalId, JsonSerializationContext context) {
        if (globalId == null) {
            return JsonNull.INSTANCE;
        }

        JsonObject jsonObject = new JsonObject();

        //managedClass
        if (globalId instanceof InstanceId) {
            jsonObject.addProperty(ENTITY_FIELD, globalId.getTypeName());
            jsonObject.add(CDO_ID_FIELD, context.serialize(((InstanceId)globalId).getCdoId()));
        } else {
            jsonObject.addProperty(VALUE_OBJECT_FIELD, globalId.getTypeName());
        }

        //owningId & fragment
        if (globalId instanceof ValueObjectId) {
            ValueObjectId valueObjectId = (ValueObjectId) globalId;

            jsonObject.add(OWNER_ID_FIELD, context.serialize(valueObjectId.getOwnerId()));
            jsonObject.addProperty(FRAGMENT_FIELD, valueObjectId.getFragment());
        }

        return jsonObject;
    }

    @Override
    public List<Class> getValueTypes() {
        return (List) Lists.immutableListOf(GlobalId.class,
                InstanceId.class,
                UnboundedValueObjectId.class,
                ValueObjectId.class);
    }

    private EntityType parseEntity(JsonObject object){
        String entityName = object.get(ENTITY_FIELD).getAsString();
        return typeMapper.getJaversManagedType(entityName, EntityType.class);
    }
}
