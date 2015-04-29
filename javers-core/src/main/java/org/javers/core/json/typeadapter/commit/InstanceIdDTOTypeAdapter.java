package org.javers.core.json.typeadapter.commit;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonSerializationContext;
import org.javers.common.collections.Lists;
import org.javers.core.json.JsonTypeAdapter;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.repository.jql.InstanceIdDTO;

import java.util.List;

class InstanceIdDTOTypeAdapter implements JsonTypeAdapter<InstanceIdDTO>  {

    private GlobalIdFactory globalIdFactory;

    public InstanceIdDTOTypeAdapter(GlobalIdFactory globalIdFactory) {
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public InstanceIdDTO fromJson(JsonElement json, JsonDeserializationContext jsonDeserializationContext) {
        throw new UnsupportedOperationException();
    }

    @Override
    public JsonElement toJson(InstanceIdDTO dtoId, JsonSerializationContext context) {
        if (dtoId == null) {
            return JsonNull.INSTANCE;
        }

        return context.serialize(globalIdFactory.createFromId(dtoId.getCdoId(), dtoId.getEntity()));
    }

    @Override
    public List<Class> getValueTypes() {
        return (List) Lists.immutableListOf(InstanceIdDTO.class);
    }
}
