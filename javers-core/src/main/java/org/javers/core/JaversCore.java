package org.javers.core;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.changelog.ChangeListTraverser;
import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitFactory;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.*;
import org.javers.repository.api.JaversExtendedRepository;
import org.javers.repository.jql.GlobalIdDTO;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.javers.common.exception.JaversExceptionCode.COMMITTING_TOP_LEVEL_VALUES_NOT_SUPPORTED;
import static org.javers.common.validation.Validate.argumentsAreNotNull;
import static org.javers.repository.jql.InstanceIdDTO.instanceId;

/**
 * core JaVers instance
 *
 * @author bartosz walacik
 */
class JaversCore implements Javers {
    private static final Logger logger = LoggerFactory.getLogger(Javers.class);

    private final DiffFactory diffFactory;
    private final TypeMapper typeMapper;
    private final JsonConverter jsonConverter;
    private final CommitFactory commitFactory;
    private final JaversExtendedRepository repository;
    private final QueryRunner queryRunner;
    private final GlobalIdFactory globalIdFactory;

    JaversCore(DiffFactory diffFactory, TypeMapper typeMapper, JsonConverter jsonConverter, CommitFactory commitFactory, JaversExtendedRepository repository, QueryRunner queryRunner, GlobalIdFactory globalIdFactory) {
        this.diffFactory = diffFactory;
        this.typeMapper = typeMapper;
        this.jsonConverter = jsonConverter;
        this.commitFactory = commitFactory;
        this.repository = repository;
        this.queryRunner = queryRunner;
        this.globalIdFactory = globalIdFactory;
    }

    @Override
    public Commit commit(String author, Object currentVersion) {
        return commit(author, Collections.<String, String>emptyMap(), currentVersion);
    }

    @Override
    public Commit commit(String author, Map<String, String> properties, Object currentVersion) {
        argumentsAreNotNull(author, properties, currentVersion);

        JaversType jType = typeMapper.getJaversType(currentVersion.getClass());
        if (jType instanceof ValueType || jType instanceof PrimitiveType){
            throw new JaversException(COMMITTING_TOP_LEVEL_VALUES_NOT_SUPPORTED,
                jType.getClass().getSimpleName(), currentVersion.getClass().getSimpleName());
        }

        Commit commit = commitFactory.create(author, properties, currentVersion);

        repository.persist(commit);
        logger.info(commit.toString());
        return commit;
    }

    @Override
    public Commit commitShallowDelete(String author, Object deleted) {
        return commitShallowDelete(author, Collections.<String, String>emptyMap(), deleted);
    }

    @Override
    public Commit commitShallowDelete(String author, Map<String, String> properties, Object deleted) {
        argumentsAreNotNull(author, properties, deleted);

        Commit commit = commitFactory.createTerminal(author, properties, deleted);

        repository.persist(commit);
        logger.info(commit.toString());
        return commit;
    }

    @Override
    public Commit commitShallowDeleteById(String author, GlobalIdDTO globalId) {
        return  commitShallowDeleteById(author, Collections.<String, String>emptyMap(), globalId);
    }


    @Override
    public Commit commitShallowDeleteById(String author, Map<String, String> properties, GlobalIdDTO globalId) {
        argumentsAreNotNull(author, properties, globalId);

        Commit commit = commitFactory.createTerminalByGlobalId(author, properties, globalIdFactory.createFromDto(globalId));

        repository.persist(commit);
        logger.info(commit.toString());
        return commit;
    }

    @Override
    public Diff compare(Object oldVersion, Object currentVersion) {
        argumentsAreNotNull(oldVersion, currentVersion);

        return diffFactory.compare(oldVersion, currentVersion);
    }

    @Override
    public Diff initial(Object newDomainObject) {
        return diffFactory.initial(newDomainObject);
    }

    @Override
    public List<CdoSnapshot> findSnapshots(JqlQuery query){
        return queryRunner.queryForSnapshots(query);
    }

    @Override
    public List<Change> findChanges(JqlQuery query){
        return queryRunner.queryForChanges(query);
    }

    @Override
    public Optional<CdoSnapshot> getLatestSnapshot(Object localId, Class entityClass) {
        Validate.argumentsAreNotNull(localId, entityClass);
        return queryRunner.runQueryForLatestSnapshot(instanceId(localId, entityClass));
    }

    @Override
    public JsonConverter getJsonConverter() {
        return jsonConverter;
    }

    @Override
    public <T> T processChangeList(List<Change> changes, ChangeProcessor<T> changeProcessor){
        argumentsAreNotNull(changes, changeProcessor);

        ChangeListTraverser.traverse(changes, changeProcessor);
        return changeProcessor.result();
    }

    @Override
    public IdBuilder idBuilder() {
        return new IdBuilder(globalIdFactory);
    }

    @Override
    public <T extends JaversType> T getTypeMapping(Type clientsType) {
        return (T) typeMapper.getJaversType(clientsType);
    }

    @Override
    public <T> Diff compareCollections(Collection<T> oldVersion, Collection<T> currentVersion, Class<T> itemClass) {
        return diffFactory.compareCollections(oldVersion, currentVersion, itemClass);
    }

    @Override
    public Property getProperty(PropertyChange propertyChange) {
        ManagedType managedType = typeMapper.getJaversManagedType(propertyChange.getAffectedGlobalId());
        return managedType.getProperty(propertyChange.getPropertyName());
    }
}
