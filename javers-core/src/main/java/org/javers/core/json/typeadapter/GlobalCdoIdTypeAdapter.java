package org.javers.core.json.typeadapter;

import com.google.gson.*;
import org.javers.common.collections.Lists;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.property.Entity;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class GlobalCdoIdTypeAdapter implements JsonTypeAdapter<GlobalCdoId> {
    @Override
    public GlobalCdoId fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        return null;
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
        if (globalCdoId.getCdoId() != null) {
            jsonObject.add("cdoId", context.serialize(globalCdoId.getCdoId()));
        }

        //owningId & fragment
        if (globalCdoId instanceof ValueObjectId) {
            ValueObjectId valueObjectId = (ValueObjectId) globalCdoId;

            jsonObject.add("ownerId", context.serialize(valueObjectId.getOwnerId()));



            jsonObject.addProperty("fragment", valueObjectId.getFragment());
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
}
