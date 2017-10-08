package org.javers.spring.jpa;

import java.time.LocalDateTime;
import java.util.Optional;
import org.javers.common.validation.Validate;
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
import org.javers.repository.sql.JaversSqlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.*;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Transactional wrapper for core JaVers instance.
 * Provides integration with Spring JPA TransactionManager
 *
 * @author bartosz walacik
 */
public class JaversTransactionalDecorator implements Javers {
    private static final Logger logger = LoggerFactory.getLogger(JaversTransactionalDecorator.class);

    private final Javers delegate;
    private final JaversSqlRepository javersSqlRepository;

    private final PlatformTransactionManager txManager;

    JaversTransactionalDecorator(Javers delegate, JaversSqlRepository javersSqlRepository, PlatformTransactionManager txManager) {
        Validate.argumentsAreNotNull(delegate, javersSqlRepository, txManager);
        this.delegate = delegate;
        this.javersSqlRepository = javersSqlRepository;
        this.txManager = txManager;
    }

    @Override
    @Transactional
    public Commit commit(String author, Object currentVersion) {
        registerRollbackListener();
        return delegate.commit(author, currentVersion);
    }

    @Override
    @Transactional
    public Commit commit(String author, Object currentVersion, Map<String, String> commitProperties) {
        registerRollbackListener();
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
    public <T extends JaversType> T getTypeMapping(Type clientsType) {
        return delegate.getTypeMapping(clientsType);
    }

    @PostConstruct
    public void ensureSchema() {
        TransactionTemplate tmpl = new TransactionTemplate(txManager);
        tmpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                javersSqlRepository.ensureSchema();
            }
        });
    }

    @Override
    public Property getProperty(PropertyChange propertyChange) {
        return delegate.getProperty(propertyChange);
    }

    private void registerRollbackListener() {
        if (javersSqlRepository.getConfiguration().isGlobalIdCacheDisabled()) {
            return;
        }
        if(TransactionSynchronizationManager.isSynchronizationActive() &&
           TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter(){
                @Override
                public void afterCompletion(int status) {
                    if (TransactionSynchronization.STATUS_ROLLED_BACK == status) {
                        logger.info("evicting javersSqlRepository local cache due to transaction rollback");
                        javersSqlRepository.evictCache();
                    }
                }
            });
        }
    }
}
