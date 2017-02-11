package org.javers.shadow;

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
        ShadowGraphBuilder builder = new ShadowGraphBuilder(jsonConverter, referenceResolver);
        return builder.buildDeepShadow(cdoSnapshot);
    }
}
