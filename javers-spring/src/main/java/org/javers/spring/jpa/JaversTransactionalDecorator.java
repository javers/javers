package org.javers.spring.jpa;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.IdBuilder;
import org.javers.core.Javers;
import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.Commit;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.type.JaversType;
import org.javers.repository.jql.GlobalIdDTO;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.sql.JaversSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Transactional wrapper for core JaVers instance.
 * Provides integration with Spring JPA TransactionManager
 *
 * @author bartosz walacik
 */
class JaversTransactionalDecorator implements Javers {
    private final Javers delegate;
    private final JaversSqlRepository javersSqlRepository;

    @Autowired
    protected PlatformTransactionManager txManager;

    JaversTransactionalDecorator(Javers delegate, JaversSqlRepository javersSqlRepository) {
        this.delegate = delegate;
        this.javersSqlRepository = javersSqlRepository;
    }

    @Override
    @Transactional
    public Commit commit(String author, Object currentVersion) {
        return delegate.commit(author, currentVersion);
    }

    @Override
    @Transactional
    public Commit commitShallowDelete(String author, Object deleted) {
        return delegate.commitShallowDelete(author, deleted);
    }

    @Override
    @Transactional
    public Commit commitShallowDeleteById(String author, GlobalIdDTO globalId) {
        return delegate.commitShallowDeleteById(author, globalId);
    }

    @Override
    public Diff compare(Object oldVersion, Object currentVersion) {
        return delegate.compare(oldVersion, currentVersion);
    }

    @Override
    public Diff initial(Object newDomainObject) {
        return delegate.initial(newDomainObject);
    }

    @Override
    public String toJson(Diff diff) {
        return delegate.toJson(diff);
    }

    @Override
    @Transactional
    @Deprecated
    public List<CdoSnapshot> getStateHistory(GlobalIdDTO globalId, int limit) {
        return delegate.getStateHistory(globalId, limit);
    }

    @Override
    @Transactional
    @Deprecated
    public Optional<CdoSnapshot> getLatestSnapshot(GlobalIdDTO globalId) {
        return delegate.getLatestSnapshot(globalId);
    }

    @Override
    @Transactional
    @Deprecated
    public List<Change> getChangeHistory(GlobalIdDTO globalId, int limit) {
        return delegate.getChangeHistory(globalId, limit);
    }

    @Transactional
    @Override
    public Optional<CdoSnapshot> getLatestSnapshot(Object localId, Class entityClass) {
        return delegate.getLatestSnapshot(localId, entityClass);
    }

    @Transactional
    @Override
    public List<CdoSnapshot> findSnapshots(JqlQuery query) {
        return delegate.findSnapshots(query);
    }

    @Transactional
    @Override
    public List<Change> findChanges(JqlQuery query) {
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
    public <T extends JaversType> T getTypeMapping(Class<?> clientsClass) {
        return delegate.getTypeMapping(clientsClass);
    }

    @Override
    public IdBuilder idBuilder() {
        return delegate.idBuilder();
    }

    @PostConstruct
    public void ensureSchema() {
        Validate.argumentIsNotNull(txManager,"TransactionManager is null");
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                javersSqlRepository.ensureSchema();
            }
        });
    }
}
