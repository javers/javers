package org.javers.core.snapshot;

import org.javers.common.collections.Optional;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.graph.CdoFactory;
import org.javers.core.metamodel.object.*;
import org.javers.repository.api.JaversRepository;

/**
 * @author bartosz walacik
 */
@Deprecated
public class CdoSnapshotRepoFactory implements CdoFactory {

    private final JaversRepository javersRepository;

    public CdoSnapshotRepoFactory(JaversRepository javersRepository) {
        this.javersRepository = javersRepository;
    }


    /**
     * @param target live Cdo or globalId
     */
    @Override
    public Cdo create(Object target, OwnerContext owner) {

       // if (target instanceof CdoSnapshot){
       //    return (CdoSnapshot) target;
       // }

        GlobalCdoId globalId = (GlobalCdoId) target;
        Optional<CdoSnapshot> snapshot =  javersRepository.getLatest(globalId);

        if (snapshot.isEmpty()){
            throw new JaversException(JaversExceptionCode.SNAPSHOT_NOT_FOUND, globalId);
        }

        return snapshot.get();
    }

    @Override
    public String typeDesc() {
        return "snapshot";
    }
}
