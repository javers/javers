package org.javers.core.snapshot;

import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.graph.CdoFactory;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.repository.api.JaversRepository;

/**
 * @author bartosz walacik
 */
public class CdoSnapshotRepoFactory implements CdoFactory {

    private final JaversRepository javersRepository;

    public CdoSnapshotRepoFactory(JaversRepository javersRepository) {
        this.javersRepository = javersRepository;
    }

    @Override
    public Cdo create(Object target, GlobalCdoId globalId) {
        CdoSnapshot snapshot =  javersRepository.getLatest(globalId);

        if (snapshot == null){
            throw new JaversException(JaversExceptionCode.SNAPSHOT_NOT_FOUND, globalId);
        }

        return snapshot;
    }

}
