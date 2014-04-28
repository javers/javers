package org.javers.core.json.typeadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.property.Entity;

import java.lang.reflect.Type;

public class GlobalCdoIdAdapter implements JsonTypeAdapter<GlobalCdoId> {

    public static final Type[] SUPPORTED = { InstanceId.class,
            UnboundedValueObjectId.class, ValueObjectId.class };

    private final GlobalIdFactory globalIdFactory;

    public GlobalCdoIdAdapter(GlobalIdFactory globalIdFactory) {
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public GlobalCdoId fromJson(JsonElement json, JsonDeserializationContext context) {

        JsonObject idAsObject = json.getAsJsonObject();
        GlobalCdoId id;

        if (idAsObject.has("entity")) {
            id = instanceIdFromJson(idAsObject, context);
        }
        else {
            id = valueObjectIdFromJson(idAsObject, context);
        }
        return id;
    }

    private GlobalCdoId valueObjectIdFromJson(JsonObject idAsObject, JsonDeserializationContext context) {
        InstanceId ownerId = instanceIdFromJson(idAsObject.getAsJsonObject("ownerId"), context);

        Class clazz = null;

        try {
            clazz = Class.forName(idAsObject.getAsJsonPrimitive("valueObject").getAsString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String fragmet = idAsObject.getAsJsonPrimitive("fragment").getAsString();

        return globalIdFactory.createFromPath(ownerId, clazz, fragmet);
    }

    private InstanceId instanceIdFromJson(JsonObject idAsObject, JsonDeserializationContext context) {
        Object id = context.deserialize(idAsObject.get("cdoId"), Object.class);

        Class clazz = null;

        try {
            clazz = Class.forName(idAsObject.getAsJsonPrimitive("entity").getAsString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return globalIdFactory.createFromId(id, clazz);
    }

    @Override
    public JsonElement toJson(GlobalCdoId globalCdoId, JsonSerializationContext context) {
        if (globalCdoId == null) {
            return JsonNull.INSTANCE;
        }
        JsonObject jsonObject = new JsonObject();

        //managedClass
        if (globalCdoId.getCdoClass() instanceof Entity) {
            jsonObject.addProperty("entity", globalCdoId.getCdoClass().getName());
        } else {
            jsonObject.addProperty("valueObject", globalCdoId.getCdoClass().getName());
        }

        //cdoId
        if (globalCdoId.getCdoId()!=null){
            jsonObject.add("cdoId", context.serialize(globalCdoId.getCdoId()));
        }

        //owningId & fragment
        if(globalCdoId instanceof ValueObjectId) {
            ValueObjectId valueObjectId = (ValueObjectId)globalCdoId;
            jsonObject.add("ownerId", toJson(valueObjectId.getOwnerId(), context));
            jsonObject.addProperty("fragment", valueObjectId.getFragment());
        }

        return jsonObject;
    }

    @Override
    public Class getValueType() {
        return GlobalCdoId.class;
    }
}
