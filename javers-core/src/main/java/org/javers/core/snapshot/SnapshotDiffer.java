package org.javers.core.snapshot;

import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Loads snapshots of given instance from javersRepository,
 * then reconstructs diffs sequence by comparing snapshots pairs
 *
 * @author bartosz walacik
 */
public class SnapshotDiffer {

    private final JaversExtendedRepository javersExtendedRepository;
    private final GraphShadowFactory graphShadowFactory;
    private final DiffFactory diffFactory;

    public SnapshotDiffer(JaversExtendedRepository javersExtendedRepository, GraphShadowFactory graphShadowFactory, DiffFactory diffFactory) {
        this.javersExtendedRepository = javersExtendedRepository;
        this.graphShadowFactory = graphShadowFactory;
        this.diffFactory = diffFactory;
    }

    /**
     * Changes (diff sequence) of given entity instance, in reverse chronological order
     *
     * @throws JaversException ENTITY_EXPECTED if given javaClass is NOT mapped to Entity
     */
    public List<Change> getChangeHistory(Object localId, Class entityClass, int limit){
        Validate.argumentsAreNotNull(localId, entityClass);

        List<CdoSnapshot> snapshots = javersExtendedRepository.getStateHistory(localId, entityClass, limit);

        if (snapshots.size() < 2){
            return Collections.EMPTY_LIST;
        }

        List<Change> result = new ArrayList<>();
        for (int i = 0; i<snapshots.size()-1; i++){
            result.addAll(compare(snapshots.get(i+1), snapshots.get(i)));
        }
        return result;
    }

    private List<Change> compare(CdoSnapshot oldVer, CdoSnapshot newVer){
        ShadowGraph oldGraph = graphShadowFactory.createFromSnapshot(oldVer);
        ShadowGraph newGraph = graphShadowFactory.createFromSnapshot(newVer);

        Diff diff = diffFactory.create(oldGraph, newGraph);
        return diff.getChanges();
    }

}
