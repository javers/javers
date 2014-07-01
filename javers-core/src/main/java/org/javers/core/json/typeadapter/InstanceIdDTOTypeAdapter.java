package org.javers.core.json.typeadapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;
import org.javers.common.collections.Lists;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.InstanceId;

import java.util.List;

public class InstanceIdDTOTypeAdapter implements JsonTypeAdapter<InstanceId.InstanceIdDTO>  {

    private GlobalIdFactory globalIdFactory;

    public InstanceIdDTOTypeAdapter(GlobalIdFactory globalIdFactory) {
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public InstanceId.InstanceIdDTO fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement toJson(InstanceId.InstanceIdDTO dtoId, JsonSerializationContext context) {
        if (dtoId == null) {
            return JsonNull.INSTANCE;
        }

        return context.serialize(globalIdFactory.createFromId(dtoId.getCdoId(), dtoId.getEntity()));
    }

    @Override
    public List<Class> getValueTypes() {
        return (List) Lists.immutableListOf(InstanceId.InstanceIdDTO.class);
    }
}
