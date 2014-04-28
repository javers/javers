package org.javers.repository.api;

import org.javers.common.collections.Optional;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.InstanceId;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class JaversExtendedRepository implements JaversRepository {
    private final JaversRepository delegate;
    private final GlobalIdFactory globalIdFactory;

    public JaversExtendedRepository(JaversRepository delegate, GlobalIdFactory globalIdFactory) {
        this.delegate = delegate;
        this.globalIdFactory = globalIdFactory;
    }

    /**
     *  @throws JaversException ENTITY_NOT_MAPPED if given javaClass is NOT mapped to Entity
     */
    public List<CdoSnapshot> getStateHistory(Object localId, Class entityClass, int limit){
        Validate.argumentsAreNotNull(localId, entityClass);

        InstanceId instanceId = globalIdFactory.createFromId(localId, entityClass);

        return delegate.getStateHistory(instanceId,limit);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalCdoId globalId, int limit) {
        return delegate.getStateHistory(globalId, limit);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalCdoId globalId) {
        return delegate.getLatest(globalId);
    }

    @Override
    public void persist(Commit commit) {
        delegate.persist(commit);

    }

    @Override
    public CommitId getHeadId() {
        return delegate.getHeadId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {

    }
}
