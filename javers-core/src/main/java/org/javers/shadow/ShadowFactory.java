package org.javers.shadow;

import com.google.gson.JsonObject;
import org.javers.common.validation.Validate;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.ManagedType;

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
        return jsonConverter.fromJson(assembleStateToJsonNode(cdoSnapshot, referenceResolver),
                cdoSnapshot.getManagedType().getBaseJavaClass());
    }

    private JsonObject assembleStateToJsonNode(CdoSnapshot cdoSnapshot, Function<GlobalId, CdoSnapshot> referenceResolver) {
        JsonObject jsonElement = (JsonObject)jsonConverter.toJsonElement(cdoSnapshot.getState());

        System.out.println("raw jsonElement: " + jsonElement.toString());

        return resolveOrNullReferences(cdoSnapshot, jsonElement, referenceResolver);
    }

    private JsonObject resolveOrNullReferences(CdoSnapshot cdoSnapshot, JsonObject jsonElement, Function<GlobalId, CdoSnapshot> referenceResolver){

        cdoSnapshot.getManagedType().forEachProperty( property -> {
            if (property.getType() instanceof ManagedType && !cdoSnapshot.isNull(property)) {

                GlobalId refId = (GlobalId) cdoSnapshot.getPropertyValue(property);

                System.out.println("resolving " + property+", ref:" + refId);

                CdoSnapshot ref = referenceResolver.apply(refId);
                if (ref == null) { //nullify unavailable reference
                    jsonElement.remove(property.getName());
                }
                else {
                    //recursion here
                    jsonElement.add(property.getName(), assembleStateToJsonNode(ref, referenceResolver));
                }
            }
        });

        return jsonElement;
    }
}
