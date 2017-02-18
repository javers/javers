package org.javers.shadow;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.TypeMapper;
import java.util.function.Function;

/**
 * @author bartosz.walacik
 */
public class ShadowFactory {

    private final JsonConverter jsonConverter;
    private final TypeMapper typeMapper;

    public ShadowFactory(JsonConverter jsonConverter, TypeMapper typeMapper) {
        this.jsonConverter = jsonConverter;
        this.typeMapper = typeMapper;
    }

    Object createShadow(CdoSnapshot cdoSnapshot) {
        return createShadow(cdoSnapshot, id -> null);
    }

    Object createShadow(CdoSnapshot cdoSnapshot, Function<GlobalId, CdoSnapshot> referenceResolver) {
        ShadowGraphBuilder builder = new ShadowGraphBuilder(jsonConverter, referenceResolver, typeMapper);
        return builder.buildDeepShadow(cdoSnapshot);
    }
}
