package org.javers.repository.sql;

import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
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
import org.javers.repository.sql.schema.SchemaNameAware;
import org.polyjdbc.core.PolyJDBC;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class JaversSqlRepository implements JaversRepository {

    private final PolyJDBC polyJDBC;
    private final CommitMetadataRepository commitRepository;
    private final GlobalIdRepository globalIdRepository;
    private final CdoSnapshotRepository cdoSnapshotRepository;
    private final CdoSnapshotFinder finder;
    private SchemaNameAware schemaManager;
    private final SqlRepositoryConfiguration sqlRepositoryConfiguration;

    public JaversSqlRepository(PolyJDBC polyJDBC, CommitMetadataRepository commitRepository, GlobalIdRepository globalIdRepository,
                               CdoSnapshotRepository cdoSnapshotRepository, CdoSnapshotFinder finder, JaversSchemaManager schemaManager,
                               SqlRepositoryConfiguration sqlRepositoryConfiguration) {
        this.polyJDBC = polyJDBC;
        this.commitRepository = commitRepository;
        this.globalIdRepository = globalIdRepository;
        this.cdoSnapshotRepository = cdoSnapshotRepository;
        this.finder = finder;
        this.schemaManager = schemaManager;
        this.sqlRepositoryConfiguration = sqlRepositoryConfiguration;
    }

    public JaversSqlRepository withSchemaManager(SchemaNameAware schemaManager){
        this.schemaManager = schemaManager;
        return this;
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        return finder.getLatest(globalId);
    }

    @Override
    public List<CdoSnapshot> getSnapshots(QueryParams queryParams) {
        return finder.getSnapshots(queryParams);
    }

    @Override
    public List<CdoSnapshot> getSnapshots(Collection<SnapshotIdentifier> snapshotIdentifiers) {
        return finder.getSnapshots(snapshotIdentifiers);
    }

    @Override
    public void persist(Commit commit) {
        if (commitRepository.isPersisted(commit)) {
            throw new JaversException(JaversExceptionCode.CANT_SAVE_ALREADY_PERSISTED_COMMIT, commit.getId());
        }

        long commitPk = commitRepository.save(commit.getAuthor(), commit.getProperties(), commit.getCommitDate(), commit.getId());
        cdoSnapshotRepository.save(commitPk, commit.getSnapshots());
    }

    @Override
    public CommitId getHeadId() {
        return commitRepository.getCommitHeadId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        globalIdRepository.setJsonConverter(jsonConverter);
        cdoSnapshotRepository.setJsonConverter(jsonConverter);
        finder.setJsonConverter(jsonConverter);
    }

    @Override
    public void ensureSchema() {
        schemaManager.ensureSchema();
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        return finder.getStateHistory(globalId, queryParams);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(Set<ManagedType> givenClasses, QueryParams queryParams) {
        return finder.getStateHistory(givenClasses, queryParams);
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        return finder.getVOStateHistory(ownerEntity, path, queryParams);
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
        polyJDBC.resetKeyGeneratorCache();
    }
}
