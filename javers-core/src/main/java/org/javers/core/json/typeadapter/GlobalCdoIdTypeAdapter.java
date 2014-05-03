package org.javers.core.json.typeadapter;

import com.google.gson.*;
import org.javers.common.collections.Lists;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClass;
import org.javers.core.metamodel.property.ValueObject;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class GlobalCdoIdTypeAdapter implements JsonTypeAdapter<GlobalCdoId> {

    private static final String ENTITY_FIELD = "entity";
    private static final String CDO_ID_FIELD = "cdoId";
    private static final String OWNER_ID_FIELD = "ownerId";
    private static final String VALUE_OBJECT_FIELD = "valueObject";
    private static final String FRAGMENT_FIELD = "fragment";

    private final GlobalIdFactory globalIdFactory;

    public GlobalCdoIdTypeAdapter(GlobalIdFactory globalIdFactory) {
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public GlobalCdoId fromJson(JsonElement json, JsonDeserializationContext context) {
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
        Class valueObjectClass = parseClass(jsonObject, VALUE_OBJECT_FIELD);
        return globalIdFactory.createFromClass(valueObjectClass);
    }

    private ValueObjectId parseValueObjectId(JsonObject jsonObject, JsonDeserializationContext context) {
        Class valueObjectClass = parseClass(jsonObject, VALUE_OBJECT_FIELD);
        String fragment = jsonObject.get(FRAGMENT_FIELD).getAsString();
        InstanceId ownerId = context.deserialize(jsonObject.get(OWNER_ID_FIELD), InstanceId.class);

        return globalIdFactory.createFromPath(ownerId, valueObjectClass, fragment);
    }

    private InstanceId parseInstanceId(JsonObject jsonObject, JsonDeserializationContext context) {
        Entity entity = parseManagedClass(jsonObject, ENTITY_FIELD, Entity.class);

        JsonElement cdoIdElement = jsonObject.get(CDO_ID_FIELD);
        Object cdoId = context.deserialize(cdoIdElement, entity.getIdProperty().getType());

        jsonObject.get(ENTITY_FIELD).getAsString();

        return globalIdFactory.createFromId(cdoId,entity);
    }

    @Override
    public JsonElement toJson(GlobalCdoId globalCdoId, JsonSerializationContext context) {
        if (globalCdoId == null) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();

        //managedClass
        if (globalCdoId.getCdoClass() instanceof Entity) {
            jsonObject.addProperty(ENTITY_FIELD, globalCdoId.getCdoClass().getName());
        } else {
            jsonObject.addProperty(VALUE_OBJECT_FIELD, globalCdoId.getCdoClass().getName());
        }

        //cdoId
        if (globalCdoId.getCdoId() != null) {
            jsonObject.add(CDO_ID_FIELD, context.serialize(globalCdoId.getCdoId()));
        }

        //owningId & fragment
        if (globalCdoId instanceof ValueObjectId) {
            ValueObjectId valueObjectId = (ValueObjectId) globalCdoId;

            jsonObject.add(OWNER_ID_FIELD, context.serialize(valueObjectId.getOwnerId()));
            jsonObject.addProperty(FRAGMENT_FIELD, valueObjectId.getFragment());
        }

        return jsonObject;
    }

    @Override
    public List<Class> getValueTypes() {
        return (List) Lists.immutableListOf(GlobalCdoId.class,
                                            InstanceId.class,
                                            UnboundedValueObjectId.class,
                                            ValueObjectId.class);
    }

    private <T extends ManagedClass> T parseManagedClass(JsonObject object, String fieldName, Class<T> expectedType){
        return globalIdFactory.getManagedClass(parseClass(object,fieldName),expectedType);
    }

    private Class parseClass(JsonObject object, String fieldName){

        String className = object.get(fieldName).getAsString();
        try {
            return this.getClass().forName(className);
        }
        catch (ClassNotFoundException e){
            throw new JaversException(JaversExceptionCode.CLASS_NOT_FOUND, className);
        }
    }
}
