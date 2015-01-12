package org.javers.core.snapshot;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalIdDTO;
import org.javers.core.metamodel.object.InstanceIdDTO;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads snapshots of given instance from javersRepository,
 * then reconstructs diffs sequence by comparing snapshots pairs
 *
 * @author bartosz walacik
 */
class SnapshotDiffer {

    private final JaversExtendedRepository javersExtendedRepository;
    private final GraphShadowFactory graphShadowFactory;
    private final DiffFactory diffFactory;

    SnapshotDiffer(JaversExtendedRepository javersExtendedRepository, GraphShadowFactory graphShadowFactory, DiffFactory diffFactory) {
        this.javersExtendedRepository = javersExtendedRepository;
        this.graphShadowFactory = graphShadowFactory;
        this.diffFactory = diffFactory;
    }

    /**
     * Changes (diff sequence) of given entity instance, in reverse chronological order
     *
     * @throws JaversException ENTITY_EXPECTED if given javaClass is NOT mapped to Entity
     */
    List<Change> getChangeHistory(Object localId, Class entityClass, int limit){
       return getChangeHistory(InstanceIdDTO.instanceId(localId,entityClass),limit);
    }

    /**
     * Changes (diff sequence) of given managed class instance, in reverse chronological order
     */
    List<Change> getChangeHistory(GlobalIdDTO globalCdoId, int limit) {
        Validate.argumentsAreNotNull(globalCdoId);


        List<CdoSnapshot> snapshots = javersExtendedRepository.getStateHistory(globalCdoId, limit);
        List<Change> result = new ArrayList<>();

        addObjectRemovedIfTerminal(result, snapshots.get(0));

        //compare pair-by-pair
        if (snapshots.size() > 1){
            for (int i = 0; i<snapshots.size()-1; i++){
                CdoSnapshot prev = snapshots.get(i + 1);
                CdoSnapshot current = snapshots.get(i);

                if (current.isTerminal()){
                    continue;
                }
                result.addAll(compare(prev, current));
            }
        }

        addNewObjectIfInitial(result, snapshots.get(snapshots.size() - 1));

        return result;
    }

    private void addObjectRemovedIfTerminal(List<Change> changes, CdoSnapshot last) {
         if (last.isTerminal()){
             changes.add(new ObjectRemoved(last.getGlobalId(), Optional.empty(), last.getCommitMetadata()));
         }
    }

    private void addNewObjectIfInitial(List<Change> changes, CdoSnapshot first) {
        if (first.isInitial()){
            changes.add(new NewObject(first.getGlobalId(), Optional.empty(), first.getCommitMetadata()));
        }
    }

    private List<Change> compare(CdoSnapshot oldVer, CdoSnapshot newVer){
        CommitMetadata commitMetadata = newVer.getCommitMetadata();

        Diff diff = diffFactory.create(fromSnapshot(oldVer),
                fromSnapshot(newVer), Optional.of(commitMetadata));
        return diff.getChanges();
    }

    private ShadowGraph fromSnapshot(CdoSnapshot snapshot){
        return graphShadowFactory.createFromSnapshot(snapshot);
    }
}
