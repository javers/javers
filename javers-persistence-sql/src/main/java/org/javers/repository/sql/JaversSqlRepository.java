package org.javers.repository.sql;

import org.javers.common.collections.Optional;
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

import java.util.Collection;
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
        commitRepository.setJsonConverter(jsonConverter);
        finder.setJsonConverter(jsonConverter);
    }

    @Override
    public void ensureSchema() {
        schemaManager.ensureSchema();
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, QueryParams queryParams) {
        return finder.getStateHistory(globalId, Optional.<String>empty(), queryParams);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(GlobalId globalId, String propertyName, QueryParams queryParams) {
        return finder.getStateHistory(globalId, Optional.of(propertyName), queryParams);
    }

    @Override
    public List<CdoSnapshot> getStateHistory(ManagedType givenClass, QueryParams queryParams) {
        return finder.getStateHistory(givenClass, Optional.<String>empty(), queryParams);
    }

    @Override
    public List<CdoSnapshot> getPropertyStateHistory(ManagedType givenClass, String propertyName, QueryParams queryParams) {
        return finder.getStateHistory(givenClass, Optional.of(propertyName), queryParams);
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(EntityType ownerEntity, String path, QueryParams queryParams) {
        return finder.getVOStateHistory(ownerEntity, path, queryParams);
    }
}
