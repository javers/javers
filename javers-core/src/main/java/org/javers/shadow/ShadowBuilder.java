package org.javers.shadow;

import com.google.gson.JsonObject;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.ManagedType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Stateful builder
 *
 * @author bartosz.walacik
 */
class ShadowBuilder {
    private final JsonConverter jsonConverter;
    private final Function<GlobalId, CdoSnapshot> referenceResolver;
    private boolean built = false;
    private Map<GlobalId, JsonObject> builtNodes = new HashMap<>();

    ShadowBuilder(JsonConverter jsonConverter, Function<GlobalId, CdoSnapshot> referenceResolver) {
        this.jsonConverter = jsonConverter;
        this.referenceResolver = referenceResolver;
    }

    Object buildDeepShadow(CdoSnapshot cdoSnapshot) {
        switchToBuilt();

        return jsonConverter.fromJson(assembleStateToJsonNode(cdoSnapshot),
                cdoSnapshot.getManagedType().getBaseJavaClass());
    }

    private void switchToBuilt() {
        if (built) {
            throw new IllegalStateException("already built");
        }
        built = true;
    }

    private JsonObject assembleStateToJsonNode(CdoSnapshot cdoSnapshot) {
        JsonObject jsonElement = (JsonObject)jsonConverter.toJsonElement(cdoSnapshot.getState());

        builtNodes.put(cdoSnapshot.getGlobalId(), jsonElement);

        return resolveOrNullReferences(cdoSnapshot, jsonElement);
    }

    private JsonObject resolveOrNullReferences(CdoSnapshot cdoSnapshot, JsonObject jsonElement){

        cdoSnapshot.getManagedType().forEachProperty( property -> {
            if (property.getType() instanceof ManagedType && !cdoSnapshot.isNull(property)) {

                GlobalId refId = (GlobalId) cdoSnapshot.getPropertyValue(property);

                CdoSnapshot ref = referenceResolver.apply(refId);
                if (ref == null) { //nullify unavailable reference
                    jsonElement.remove(property.getName());
                }
                else if (!builtNodes.containsKey(refId)){
                    //recursion here
                    jsonElement.add(property.getName(), assembleStateToJsonNode(ref));
                }
                else { //circular references are not supported by Gson, so nullify for now
                    jsonElement.remove(property.getName());
                }
            }
        });

        return jsonElement;
    }
}
