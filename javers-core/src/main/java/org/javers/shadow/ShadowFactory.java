package org.javers.shadow;

import com.google.gson.JsonElement;
import org.javers.common.validation.Validate;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;

import java.util.function.Function;

/**
 * @author bartosz.walacik
 */
public class ShadowFactory {

    private final JsonConverter jsonConverter;

    public ShadowFactory(JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    Object createShadow(CdoSnapshot cdoSnapshot) {
        return createShadow(cdoSnapshot, id -> null);
    }

    Object createShadow(CdoSnapshot cdoSnapshot, Function<GlobalId, CdoSnapshot> referenceResolver) {
        Validate.argumentsAreNotNull(cdoSnapshot, referenceResolver);
        return jsonConverter.fromJson(assembleJsonNode(cdoSnapshot, referenceResolver),
                cdoSnapshot.getManagedType().getBaseJavaClass());
    }

    private JsonElement assembleJsonNode(CdoSnapshot cdoSnapshot, Function<GlobalId, CdoSnapshot> referenceResolver) {
        JsonElement jsonElement = jsonConverter.toJsonElement(cdoSnapshot.getState());

        System.out.println("raw jsonElement: " + jsonElement.toString());

        return resolveOrNullReferences(cdoSnapshot, jsonElement, referenceResolver);
    }

    private JsonElement resolveOrNullReferences(CdoSnapshot cdoSnapshot, JsonElement rawElement, Function<GlobalId, CdoSnapshot> referenceResolver){
        return rawElement;
    }
}
