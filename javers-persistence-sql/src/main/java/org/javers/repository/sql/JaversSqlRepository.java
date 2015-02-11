package org.javers.repository.sql;

import org.javers.common.collections.Optional;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.*;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.sql.domain.CommitMetadataRepository;
import org.javers.repository.sql.domain.GlobalIdRepository;
import org.javers.repository.sql.domain.CdoSnapshotRepository;
import org.javers.repository.sql.finders.CdoSnapshotFinder;
import org.slf4j.Logger;

import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class JaversSqlRepository implements JaversRepository {

    private static final Logger logger = getLogger(JaversSqlRepository.class);
    
    private final CommitMetadataRepository commitRepository;
    private final GlobalIdRepository globalIdRepository;
    private final CdoSnapshotRepository cdoSnapshotRepository;
    private final CdoSnapshotFinder finder;

    public JaversSqlRepository(CommitMetadataRepository commitRepository,
                               GlobalIdRepository globalIdRepository,
                               CdoSnapshotRepository cdoSnapshotRepository, 
                               CdoSnapshotFinder finder) {
        this.commitRepository = commitRepository;
        this.globalIdRepository = globalIdRepository;
        this.cdoSnapshotRepository = cdoSnapshotRepository;
        this.finder = finder;
    }

    @Override
    public List<CdoSnapshot> getStateHistory(GlobalId globalId, int limit) {
        return finder.getStateHistory(globalId, globalId.getCdoClass().getName(), limit);
    }

    @Override
    public Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        return finder.getLatest(globalId);
    }

    @Override
    public void persist(Commit commit) {
        Optional<Long> primaryKey = commitRepository.find(commit);
        
        if (primaryKey.isPresent()) {
            logger.error("Cannot save already persisted commit");
            return;
        }
        
        long commitMetadataPk = commitRepository.save(commit.getAuthor(), commit.getCommitDate(), commit.getId());

        for (CdoSnapshot cdoSnapshot: commit.getSnapshots()) {
            long globalIdPk = globalIdRepository.save(cdoSnapshot.getGlobalId());
            cdoSnapshotRepository.save(globalIdPk, commitMetadataPk, cdoSnapshot);
        }
    }

    @Override
    public CommitId getHeadId() {
        return commitRepository.getHeadId();
    }

    @Override
    public void setJsonConverter(JsonConverter jsonConverter) {
        //TODO dependency injection
        globalIdRepository.setJsonConverter(jsonConverter);
        cdoSnapshotRepository.setJsonConverter(jsonConverter);
        finder.setJsonConverter(jsonConverter);
        commitRepository.setJsonConverter(jsonConverter);
    }
}
