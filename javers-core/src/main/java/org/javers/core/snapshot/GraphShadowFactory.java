package org.javers.core.snapshot;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectGraphBuilder;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.repository.api.JaversRepository;


/**
 * Restores objects graph snapshot
 *
 * @author bartosz walacik
 */
public class GraphShadowFactory {
    private final CdoSnapshotRepoFactory cdoSnapshotRepoFactory ;
    private final TypeMapper typeMapper;
    private final JaversRepository javersRepository;
    private final GlobalIdFactory globalIdFactory;

    public GraphShadowFactory(CdoSnapshotRepoFactory cdoSnapshotRepoFactory, TypeMapper typeMapper, JaversRepository javersRepository, GlobalIdFactory globalIdFactory) {
        this.cdoSnapshotRepoFactory = cdoSnapshotRepoFactory;
        this.typeMapper = typeMapper;
        this.javersRepository = javersRepository;
        this.globalIdFactory = globalIdFactory;
    }

    /**
     * @return shadow graph or {@link Optional#EMPTY} if currentVersion is new,
     *         so absent in repository
     */
    public Optional<ObjectNode> createLatestShadow(Object currentVersion){
        Validate.argumentIsNotNull(currentVersion);

        GlobalCdoId rootId = globalIdFactory.createId(currentVersion);

        Optional<CdoSnapshot> rootSnapshot = javersRepository.getLatest(rootId);

        if (rootSnapshot.isEmpty()){
            return Optional.empty();
        }

        ObjectGraphBuilder builder = new ObjectGraphBuilder(typeMapper, cdoSnapshotRepoFactory);
        return Optional.of(builder.buildGraph(rootSnapshot.get()));
    }
}
