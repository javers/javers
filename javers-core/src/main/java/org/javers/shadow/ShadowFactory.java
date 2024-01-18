package org.javers.shadow;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.TypeMapper;

import java.util.function.BiFunction;
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

    public Shadow createShadow(CdoSnapshot cdoSnapshot, CommitMetadata rootContext, BiFunction<CommitMetadata, GlobalId, CdoSnapshot> referenceResolver) {
        ShadowGraphBuilder builder = new ShadowGraphBuilder(jsonConverter, referenceResolver, typeMapper, rootContext);
        return new Shadow(rootContext, cdoSnapshot, builder.buildDeepShadow(cdoSnapshot));
    }

    Object createShadow(CdoSnapshot cdoSnapshot) {
        return createShadow(cdoSnapshot, cdoSnapshot.getCommitMetadata(), (source, target) -> null).get();
    }

    Object createShadow(CdoSnapshot cdoSnapshot, BiFunction<CommitMetadata, GlobalId, CdoSnapshot> referenceResolver) {
        return createShadow(cdoSnapshot, cdoSnapshot.getCommitMetadata(), referenceResolver).get();
    }
}
