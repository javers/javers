package org.javers.core.snapshot;

import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.type.TypeMapper;


/**
 * Restores objects graph snapshot
 *
 * @author bartosz walacik
 */
public class GraphShadowFactory {
    private final CdoSnapshotRepoFactory cdoSnapshotRepoFactory ;
    private final TypeMapper typeMapper;

    public GraphShadowFactory(CdoSnapshotRepoFactory cdoSnapshotRepoFactory, TypeMapper typeMapper) {
        this.cdoSnapshotRepoFactory = cdoSnapshotRepoFactory;
        this.typeMapper = typeMapper;
    }

    public ObjectNode createLatestShadow(Object currentVersion){
        Validate.argumentIsNotNull(currentVersion);

        ObjectGraphBuilder builder = new ObjectGraphBuilder(typeMapper, cdoSnapshotRepoFactory);

        return builder.buildGraph(currentVersion);
    }
}
