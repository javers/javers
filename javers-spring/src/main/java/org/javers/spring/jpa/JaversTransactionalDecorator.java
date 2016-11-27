package org.javers.spring.jpa;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.IdBuilder;
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
import org.javers.repository.sql.JaversSqlRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

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
    private final Javers delegate;
    private final JaversSqlRepository javersSqlRepository;

	@Value("${javers.auto.schema.create:true}")
	private boolean autoSchemaCreate = true;

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

    @Override
    public IdBuilder idBuilder() {
        return delegate.idBuilder();
    }

    @PostConstruct
    public void ensureSchema() {
		if(autoSchemaCreate){
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

    @Override
    public Property getProperty(PropertyChange propertyChange) {
        return delegate.getProperty(propertyChange);
    }
}
