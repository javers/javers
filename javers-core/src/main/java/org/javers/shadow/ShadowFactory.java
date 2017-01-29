package org.javers.shadow;

import com.google.gson.JsonElement;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;

/**
 * @author bartosz.walacik
 */
public class ShadowFactory {

    private final JsonConverter jsonConverter;

    public ShadowFactory(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    Object createShadow(CdoSnapshot cdoSnapshot) {
        return jsonConverter.fromJson(assembleJsonNode(cdoSnapshot), cdoSnapshot.getManagedType().getBaseJavaClass());
    }

    private JsonElement assembleJsonNode(CdoSnapshot cdoSnapshot) {
        return jsonConverter.toJsonElement(cdoSnapshot.getState());
    }
}
