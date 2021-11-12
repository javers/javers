package org.javers.spring.transactions;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.common.validation.Validate;
import org.javers.core.Changes;
import org.javers.core.CoreConfiguration;
import org.javers.core.Javers;
import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.Commit;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.JaversType;
import org.javers.repository.jql.GlobalIdDTO;
import org.javers.repository.jql.JqlQuery;
import org.javers.shadow.Shadow;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

/**
 * Transactional wrapper for core JaVers instance.
 * Provides integration with Spring JPA TransactionManager
 *
 * @author bartosz walacik
 */
public abstract class JaversTransactionalDecorator implements Javers {
    private final Javers delegate;

    protected JaversTransactionalDecorator(Javers delegate) {
        Validate.argumentsAreNotNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public CompletableFuture<Commit> commitAsync(String author, Object currentVersion, Map<String, String> commitProperties, Executor executor) {
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED,
                "javers.commitAsync() is not available for SQL");
    }

    @Override
    public CompletableFuture<Commit> commitAsync(String author, Object currentVersion, Executor executor) {
        throw new JaversException(JaversExceptionCode.NOT_IMPLEMENTED,
                "javers.commitAsync() is not available for SQL");
    }

    @Override
    @Transactional
    public Commit commit(String author, Object currentVersion) {
        return delegate.commit(author, currentVersion);
    }

    @Override
    @Transactional
    public Commit commit(String author, Object currentVersion, Map<String, String> commitProperties) {
        return delegate.commit(author, currentVersion, commitProperties);
    }

    @Override
    @Transactional
    public Commit commitShallowDelete(String author, Object deleted) {
        return delegate.commitShallowDelete(author, deleted);
    }

    @Override
    @Transactional
    public Commit commitShallowDelete(String author, Object deleted, Map<String, String> properties) {
        return delegate.commitShallowDelete(author, deleted, properties);
    }

    @Override
    @Transactional
    public Commit commitShallowDeleteById(String author, GlobalIdDTO globalId) {
        return delegate.commitShallowDeleteById(author, globalId);
    }

    @Override
    @Transactional
    public Commit commitShallowDeleteById(String author, GlobalIdDTO globalId, Map<String, String> properties) {
        return delegate.commitShallowDeleteById(author, globalId, properties);
    }

    @Override
    public Diff compare(Object oldVersion, Object currentVersion) {
        return delegate.compare(oldVersion, currentVersion);
    }

    @Override
    public <T> Diff compareCollections(Collection<T> oldVersion, Collection<T> currentVersion, Class<T> itemClass) {
        return delegate.compareCollections(oldVersion, currentVersion, itemClass);
    }

    @Override
    public Diff initial(Object newDomainObject) {
        return delegate.initial(newDomainObject);
    }

    @Transactional
    @Override
    public Optional<CdoSnapshot> getLatestSnapshot(Object localId, Class entityClass) {
        return delegate.getLatestSnapshot(localId, entityClass);
    }

    @Transactional
    @Override
    public Optional<CdoSnapshot> getHistoricalSnapshot(Object localId, Class entity, LocalDateTime effectiveDate) {
        return delegate.getHistoricalSnapshot(localId, entity, effectiveDate);
    }

    @Transactional
    @Override
    public <T> List<Shadow<T>> findShadows(JqlQuery query) {
        return delegate.findShadows(query);
    }

    @Transactional
    @Override
    public <T> Stream<Shadow<T>> findShadowsAndStream(JqlQuery query) {
        return delegate.findShadowsAndStream(query);
    }

    @Transactional
    @Override
    public List<CdoSnapshot> findSnapshots(JqlQuery query) {
        return delegate.findSnapshots(query);
    }

    @Transactional
    @Override
    public Changes findChanges(JqlQuery query) {
        return delegate.findChanges(query);
    }

    @Override
    public JsonConverter getJsonConverter() {
        return delegate.getJsonConverter();
    }

    @Override
    public <T> T processChangeList(List<Change> changes, ChangeProcessor<T> changeProcessor) {
        return delegate.processChangeList(changes, changeProcessor);
    }

    @Override
    public <T extends JaversType> T getTypeMapping(Type clientsType) {
        return delegate.getTypeMapping(clientsType);
    }

    @Override
    public CoreConfiguration getCoreConfiguration() {
        return delegate.getCoreConfiguration();
    }

    @Override
    public Property getProperty(PropertyChange propertyChange) {
        return delegate.getProperty(propertyChange);
    }
}

