package org.javers.spring.mongodb;

import com.mongodb.client.ClientSession;
import org.javers.common.validation.Validate;
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
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

class TransactionalMongoRepository implements JaversRepository, ConfigurationAware {
    private final MongoRepository delegate;

    private final TransactionTemplate transactionTemplate;

    TransactionalMongoRepository(MongoRepository delegate, MongoTransactionManager txManager) {
        this.delegate = delegate;
        this.transactionTemplate = new TransactionTemplate(txManager);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        return supplyInTransaction(session -> delegate.getStateHistory(globalId, queryParams, session));
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        return supplyInTransaction(session -> delegate.getValueObjectStateHistory(ownerEntity, path, queryParams, session));
    }

    @Override
    public List<CdoSnapshot> getStateHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {
        return supplyInTransaction(session -> delegate.getStateHistory(givenClasses, queryParams, session));
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        return supplyInTransaction(session -> delegate.getLatest(globalId, session));
    }

    @Override
    public List<CdoSnapshot> getLatest(Collection<GlobalId> globalIds) {
        return supplyInTransaction(session -> getLatest(globalIds, session));
    }

    private List<CdoSnapshot> getLatest(Collection<GlobalId> globalIds, ClientSession clientSession) {
        Validate.argumentsAreNotNull(globalIds);

        return globalIds.stream()
            .map(globalId -> delegate.getLatest(globalId, clientSession))
            .flatMap(Optional::stream)
            .collect(Collectors.toList());
    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        return supplyInTransaction(session -> delegate.getSnapshots(queryParams, session));
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return supplyInTransaction(session -> delegate.getSnapshots(snapshotIdentifiers, session));
    }

    @Override
    public void persist(Commit commit) {
        doInTransaction(session -> delegate.persist(commit, session));
    }

    @Override
    public CommitId getHeadId() {
        // Don't delegate within a transaction because only actually persisted commits should be taken into account.
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

    private void doInTransaction(final Consumer<ClientSession> consumer) {
        supplyInTransaction(session -> {
            consumer.accept(session);
            return null;
        });
    }

    private <T> T supplyInTransaction(final Function<ClientSession, T> supplier) {
        return transactionTemplate.execute(status -> {
            ClientSession session = ClientSessionExtractor.getFrom((DefaultTransactionStatus)status);
            return supplier.apply(session);
        });
    }
}
