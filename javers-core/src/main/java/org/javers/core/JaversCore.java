package org.javers.core;

import org.javers.common.collections.Optional;
import org.javers.common.validation.Validate;
import org.javers.core.changelog.ChangeListTraverser;
import org.javers.core.changelog.ChangeProcessor;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitFactory;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.repository.api.JaversExtendedRepository;
import org.javers.repository.jql.GlobalIdDTO;
import org.javers.repository.jql.JqlQuery;
import org.javers.repository.jql.QueryBuilder;
import org.javers.repository.jql.QueryRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        argumentsAreNotNull(author, currentVersion);

        Commit commit = commitFactory.create(author, currentVersion);

        repository.persist(commit);
        logger.info(commit.toString());
        return commit;
    }

    @Override
    public Commit commitShallowDelete(String author, Object deleted) {
        argumentsAreNotNull(author, deleted);

        Commit commit = commitFactory.createTerminal(author, deleted);

        repository.persist(commit);
        logger.info(commit.toString());
        return commit;
    }

    @Override
    public Commit commitShallowDeleteById(String author, GlobalIdDTO globalId) {
        argumentsAreNotNull(author, globalId);

        Commit commit = commitFactory.createTerminalByGlobalId(author, globalIdFactory.createFromDto(globalId));

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
    public String toJson(Diff diff) {
        return jsonConverter.toJson(diff);
    }

    @Override
    public List<CdoSnapshot> findSnapshots(JqlQuery query){
        return queryRunner.queryForSnapshots(query);
    }

    @Override
    public List<Change> findChanges(JqlQuery query){
        return queryRunner.queryForChanges(query);
    }

    /**
     * TODO: deprecate
     */
    @Deprecated
    @Override
    public List<CdoSnapshot> getStateHistory(GlobalIdDTO globalId, int limit) {
        return queryRunner.queryForSnapshots(QueryBuilder.byGlobalIdDTO(globalId).limit(limit).build());
    }

    /**
     * TODO: deprecate
     */
    @Deprecated
    @Override
    public List<Change> getChangeHistory(GlobalIdDTO globalId, int limit) {
        return queryRunner.queryForChanges(QueryBuilder.byGlobalIdDTO(globalId).limit(limit).build());
    }

    @Override
    public Optional<CdoSnapshot> getLatestSnapshot(Object localId, Class entityClass) {
        Validate.argumentsAreNotNull(localId, entityClass);
        return queryRunner.runQueryForLatestSnapshot(instanceId(localId, entityClass));
    }

    @Override
    @Deprecated
    public Optional<CdoSnapshot> getLatestSnapshot(GlobalIdDTO globalId){
        Validate.argumentIsNotNull(globalId);
        return queryRunner.runQueryForLatestSnapshot(globalId);
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
    public <T extends JaversType> T getTypeMapping(Class<?> clientsClass) {
        return (T) typeMapper.getJaversType(clientsClass);
    }
}
