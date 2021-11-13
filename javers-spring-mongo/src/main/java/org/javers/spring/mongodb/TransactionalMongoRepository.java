package org.javers.spring.mongodb;

import com.mongodb.client.ClientSession;
import org.javers.core.CoreConfiguration;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.repository.api.ConfigurationAware;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.SnapshotIdentifier;
import org.javers.repository.mongo.MongoRepository;
import org.springframework.data.mongodb.ClientSessionExtractor;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

class TransactionalMongoRepository implements JaversRepository, ConfigurationAware {
    private final MongoRepository delegate;

    private final TransactionTemplate transactionTemplate;

    TransactionalMongoRepository(MongoRepository delegate, MongoTransactionManager txManager) {
        this.delegate = delegate;
        this.transactionTemplate = new TransactionTemplate(txManager);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        return delegate.getStateHistory(globalId, queryParams);
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        return delegate.getValueObjectStateHistory(ownerEntity, path, queryParams);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {
        return delegate.getStateHistory(givenClasses, queryParams);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        return delegate.getLatest(globalId);
    }

    @Override
    public List<CdoSnapshot> getLatest(Collection<GlobalId> globalIds) {
        return delegate.getLatest(globalIds);
    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        return delegate.getSnapshots(queryParams);
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return delegate.getSnapshots(snapshotIdentifiers);
    }

    @Override
    public void persist(Commit commit) {
        transactionTemplate.execute(status -> {
            ClientSession session = ClientSessionExtractor.getFrom((DefaultTransactionStatus)status);
            delegate.persist(commit, session);
            return null;
        });
    }

    @Override
    public CommitId getHeadId() {
        return delegate.getHeadId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        delegate.setJsonConverter(jsonConverter);
    }

    @Override
    public void ensureSchema() {
        delegate.ensureSchema();
    }

    @Override
    public void setConfiguration(CoreConfiguration coreConfiguration) {
        delegate.setConfiguration(coreConfiguration);
    }
}
