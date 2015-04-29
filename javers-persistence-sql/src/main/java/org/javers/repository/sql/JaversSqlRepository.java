package org.javers.repository.sql;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.clazz.Entity;
import org.javers.core.metamodel.clazz.ManagedClass;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.sql.finders.CdoSnapshotFinder;
import org.javers.repository.sql.reposiotries.CdoSnapshotRepository;
import org.javers.repository.sql.reposiotries.CommitMetadataRepository;
import org.javers.repository.sql.reposiotries.GlobalIdRepository;
import org.javers.repository.sql.schema.JaversSchemaManager;

import java.util.List;

public class JaversSqlRepository implements JaversRepository {

    private final CommitMetadataRepository commitRepository;
    private final GlobalIdRepository globalIdRepository;
    private final CdoSnapshotRepository cdoSnapshotRepository;
    private final CdoSnapshotFinder finder;
    private final JaversSchemaManager schemaManager;

    public JaversSqlRepository(CommitMetadataRepository commitRepository, GlobalIdRepository globalIdRepository, CdoSnapshotRepository cdoSnapshotRepository, CdoSnapshotFinder finder, JaversSchemaManager schemaManager) {
        this.commitRepository = commitRepository;
        this.globalIdRepository = globalIdRepository;
        this.cdoSnapshotRepository = cdoSnapshotRepository;
        this.finder = finder;
        this.schemaManager = schemaManager;
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        return finder.getLatest(globalId);
    }

    @Override
    public void persist(Commit commit) {
        Optional<Long> primaryKey = commitRepository.getCommitPrimaryKey(commit);

        if (primaryKey.isPresent()) {
            throw new JaversException(JaversExceptionCode.CANT_SAVE_ALREADY_PERSISTED_COMMIT);
        }

        long commitPk = commitRepository.save(commit.getAuthor(), commit.getCommitDate(), commit.getId());
        cdoSnapshotRepository.save(commitPk, commit.getSnapshots());
    }

    @Override
    public CommitId getHeadId() {
        return commitRepository.getCommitHeadId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        //TODO dependency injection
        globalIdRepository.setJsonConverter(jsonConverter);
        cdoSnapshotRepository.setJsonConverter(jsonConverter);
        finder.setJsonConverter(jsonConverter);
        commitRepository.setJsonConverter(jsonConverter);
    }

    @Override
    public void ensureSchema() {
        schemaManager.ensureSchema();
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, int limit) {
        return finder.getStateHistory(globalId, Optional.<String>empty(), limit);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(GlobalId globalId, String propertyName, int limit) {
        return finder.getStateHistory(globalId, Optional.of(propertyName), limit);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(ManagedClass givenClass, int limit) {
        return finder.getStateHistory(givenClass, Optional.<String>empty(), limit);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(ManagedClass givenClass, String propertyName, int limit) {
        return finder.getStateHistory(givenClass, Optional.of(propertyName), limit);
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(Entity ownerEntity, String path, int limit) {
        return finder.getVOStateHistory(ownerEntity, path, limit);
    }
}
