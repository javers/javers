package org.javers.core.json.typeadapter.commit;

import com.google.gson.*;
import org.javers.common.collections.Lists;
import org.javers.core.json.JsonConverter;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.clazz.Entity;
import org.javers.core.metamodel.type.TypeMapper;
import org.slf4j.Logger;

import java.util.List;
import static org.slf4j.LoggerFactory.getLogger;

/**
 * @author bartosz walacik
 */
class GlobalIdTypeAdapter implements JsonTypeAdapter<GlobalId> {
    private static final Logger logger = getLogger(GlobalIdTypeAdapter.class);

    private static final String ENTITY_FIELD = "entity";
    private static final String CDO_ID_FIELD = "cdoId";
    private static final String OWNER_ID_FIELD = "ownerId";
    private static final String VALUE_OBJECT_FIELD = "valueObject";
    private static final String FRAGMENT_FIELD = "fragment";

    private final GlobalIdFactory globalIdFactory;
    private final TypeMapper typeMapper;

    public GlobalIdTypeAdapter(GlobalIdFactory globalIdFactory, TypeMapper typeMapper) {
        this.globalIdFactory = globalIdFactory;
        this.typeMapper = typeMapper;
    }

    @Override
    public GlobalId fromJson(JsonElement json, JsonDeserializationContext context) {
        JsonObject jsonObject = (JsonObject) json;

        logger.debug("deserializing "+json);

        if (jsonObject.get(ENTITY_FIELD) != null) {
            return parseInstanceId(jsonObject, context);
        } else if (jsonObject.get(OWNER_ID_FIELD) != null) {
            return parseValueObjectId(jsonObject, context);
        } else {
            return parseUnboundedValueObject(jsonObject);
        }
    }

    private UnboundedValueObjectId parseUnboundedValueObject(JsonObject jsonObject){
        Class valueObjectClass = parseClass(jsonObject, VALUE_OBJECT_FIELD);
        return globalIdFactory.createFromClass(valueObjectClass);
    }

    private ValueObjectId parseValueObjectId(JsonObject jsonObject, JsonDeserializationContext context) {
        Class valueObjectClass = parseClass(jsonObject, VALUE_OBJECT_FIELD);
        String fragment = jsonObject.get(FRAGMENT_FIELD).getAsString();
        GlobalId ownerId = context.deserialize(jsonObject.get(OWNER_ID_FIELD), GlobalId.class);

        return globalIdFactory.createFromPath(ownerId, valueObjectClass, fragment);
    }

    private InstanceId parseInstanceId(JsonObject jsonObject, JsonDeserializationContext context) {
        Entity entity = parseEntity(jsonObject, ENTITY_FIELD);

        JsonElement cdoIdElement = jsonObject.get(CDO_ID_FIELD);
        Object cdoId = context.deserialize(cdoIdElement, entity.getIdProperty().getType());

        return globalIdFactory.createFromId(cdoId, entity);
    }

    @Override
    public JsonElement toJson(GlobalId globalId, JsonSerializationContext context) {
        if (globalId == null) {
            return JsonNull.INSTANCE;
        }

        logger.debug("serializing "+globalId.getClass().getSimpleName()+":"+globalId);
        JsonObject jsonObject = new JsonObject();

        //managedClass
        if (globalId.getCdoClass() instanceof Entity) {
            jsonObject.addProperty(ENTITY_FIELD, globalId.getCdoClass().getName());
        } else {
            jsonObject.addProperty(VALUE_OBJECT_FIELD, globalId.getCdoClass().getName());
        }

        //cdoId
        if (globalId.getCdoId() != null) {
            jsonObject.add(CDO_ID_FIELD, context.serialize(globalId.getCdoId()));
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

    private Entity parseEntity(JsonObject object, String fieldName){
        return typeMapper.getManagedClass(parseClass(object, fieldName), Entity.class);
    }

    private Class parseClass(JsonObject object, String fieldName) {
        String className = object.get(fieldName).getAsString();
        return JsonConverter.parseClass(className);
    }
}
