package org.javers.repository.sql;

import org.javers.common.validation.Validate;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.SnapshotIdentifier;
import org.javers.repository.sql.finders.CdoSnapshotFinder;
import org.javers.repository.sql.repositories.CdoSnapshotRepository;
import org.javers.repository.sql.repositories.CommitMetadataRepository;
import org.javers.repository.sql.repositories.GlobalIdRepository;
import org.javers.repository.sql.schema.JaversSchemaManager;
import org.javers.repository.sql.session.Session;
import org.javers.repository.sql.session.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static org.javers.repository.sql.session.Session.SQL_LOGGER_NAME;

public class JaversSqlRepository implements JaversRepository {
    private static final Logger logger = LoggerFactory.getLogger(SQL_LOGGER_NAME);

    private final SessionFactory sessionFactory;
    private final CommitMetadataRepository commitRepository;
    private final GlobalIdRepository globalIdRepository;
    private final CdoSnapshotRepository cdoSnapshotRepository;
    private final CdoSnapshotFinder finder;
    private final JaversSchemaManager schemaManager;

    private final SqlRepositoryConfiguration sqlRepositoryConfiguration;

    public JaversSqlRepository(SessionFactory sessionFactory,
                               CommitMetadataRepository commitRepository,
                               GlobalIdRepository globalIdRepository,
                               CdoSnapshotRepository cdoSnapshotRepository,
                               CdoSnapshotFinder finder,
                               JaversSchemaManager schemaManager,
                               SqlRepositoryConfiguration sqlRepositoryConfiguration) {
        this.sessionFactory = sessionFactory;
        this.commitRepository = commitRepository;
        this.globalIdRepository = globalIdRepository;
        this.cdoSnapshotRepository = cdoSnapshotRepository;
        this.finder = finder;
        this.schemaManager = schemaManager;
        this.sqlRepositoryConfiguration = sqlRepositoryConfiguration;
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        try(Session session = sessionFactory.create("get latest snapshot")) {
            return finder.getLatest(globalId, session, true);
        }
    }

    @Override
    public List<CdoSnapshot> getLatest(Collection<GlobalId> globalIds) {
        Validate.argumentIsNotNull(globalIds);
        try(Session session = sessionFactory.create("get latest snapshots")) {
            return globalIds.stream()
                    .map(id -> finder.getLatest(id, session, false))
                    .filter(it -> it.isPresent())
                    .map(it -> it.get())
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        try(Session session = sessionFactory.create("find snapshots")) {
            return finder.getSnapshots(queryParams, session);
        }
    }

    @Override
    public void persist(Commit commit) {
        try(Session session = sessionFactory.create("persist commit")) {
            long commitPk = commitRepository.save(commit.getAuthor(), commit.getProperties(), commit.getCommitDate(), commit.getCommitDateInstant(), commit.getId(), session);
            cdoSnapshotRepository.save(commitPk, commit.getSnapshots(), session);
        }
    }

    @Override
    public CommitId getHeadId() {
        try(Session session = sessionFactory.create("get head id")) {
            return commitRepository.getCommitHeadId(session);
        }
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        if (isEmpty(snapshotIdentifiers)) {
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.create("find snapshots by ids")) {
            return finder.getSnapshots(snapshotIdentifiers, session);
        }
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        try(Session session = sessionFactory.create("find snapshots by globalId")) {
            return finder.getStateHistory(globalId, queryParams, session);
        }
    }

    @Override
    public List<CdoSnapshot> getStateHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {
        if (isEmpty(givenClasses)) {
            return Collections.emptyList();
        }
        try(Session session = sessionFactory.create("find snapshots by type")) {
            return finder.getStateHistory(givenClasses, queryParams, session);
        }
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        try(Session session = sessionFactory.create("find VO snapshots by path")) {
            return finder.getVOStateHistory(ownerEntity, path, queryParams, session);
        }
    }

    /**
     * JaversSqlRepository uses the cache for GlobalId primary keys.
     * This cache is non-transactional.
     * <br/><br/>
     *
     * If a SQL transaction encounters errors and must be rolled back,
     * then cache modifications should be rolled back as well.
     * <br/><br/>
     *
     * JaVers does this automatically in <code>JaversTransactionalDecorator</code>
     * from <code>javers-spring</code> module.
     * If you are using <code>javers-spring-boot-starter-sql</code>
     * (or directly <code>javers-spring</code>) you don't need to call this method.
     *
     * @since 2.7.2
     */
    public void evictCache() {
        globalIdRepository.evictCache();
    }

    /**
     * @since 2.7.2
     */
    public int getGlobalIdPkCacheSize(){
        return globalIdRepository.getGlobalIdPkCacheSize();
    }

    /**
     * @since 2.7.2
     */
    public SqlRepositoryConfiguration getConfiguration() {
        return sqlRepositoryConfiguration;
    }

    /**
     * Clears the sequence allocation cache. It can be useful for testing.
     * See https://github.com/javers/javers/issues/532
     * @since 3.1.1
     */
    public void evictSequenceAllocationCache() {
        sessionFactory.resetKeyGeneratorCache();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        //TODO centralize to Session?
        globalIdRepository.setJsonConverter(jsonConverter);
        cdoSnapshotRepository.setJsonConverter(jsonConverter);
        finder.setJsonConverter(jsonConverter);
    }

    @Override
    public void ensureSchema() {
        if(sqlRepositoryConfiguration.isSchemaManagementEnabled()) {
            schemaManager.ensureSchema();
        }
    }

    private boolean isEmpty(Collection c) {
        return c == null || c.size() == 0;
    }
}
