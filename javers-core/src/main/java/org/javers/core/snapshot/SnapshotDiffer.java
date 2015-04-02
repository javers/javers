package org.javers.core.snapshot;

import org.javers.common.collections.Optional;
import org.javers.common.collections.Sets;
import org.javers.common.validation.Validate;
import org.javers.core.diff.Change;
import org.javers.core.diff.Diff;
import org.javers.core.diff.DiffFactory;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.CdoSnapshotBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Loads snapshots of given instance from javersRepository,
 * then reconstructs diffs sequence by comparing snapshots pairs
 *
 * @author bartosz walacik
 */
public class SnapshotDiffer {

    private final DiffFactory diffFactory;

    public SnapshotDiffer(DiffFactory diffFactory) {
        this.diffFactory = diffFactory;
    }

    /**
     * Changes (diff sequence) of given managed class instance, in reverse chronological order
     */
    public List<Change> calculateDiffs(List<CdoSnapshot> snapshots) {
        Validate.argumentsAreNotNull(snapshots);

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

        addNewObjectChangesIfInitial(result, snapshots.get(snapshots.size() - 1));

        return result;
    }

    private void addObjectRemovedIfTerminal(List<Change> changes, CdoSnapshot last) {
         if (last.isTerminal()){
             changes.add(new ObjectRemoved(last.getGlobalId(), Optional.empty(), last.getCommitMetadata()));
         }
    }

    private void addNewObjectChangesIfInitial(List<Change> changes, CdoSnapshot first) {
        if (first.isInitial()){
            //add initial values to change history (with null at left)
            //switching off this feature may be added to JQL in the future
            CdoSnapshot empty = CdoSnapshotBuilder.emptyCopyOf(first);
            Diff diff = diffFactory.create(fromSnapshot(empty),
                    fromSnapshot(first), Optional.of(first.getCommitMetadata()));
            changes.addAll(diff.getChanges());

            //add NewObject change at the bottom of the change list
            changes.add(new NewObject(first.getGlobalId(), Optional.empty(), first.getCommitMetadata()));
        }
    }

    private List<Change> compare(CdoSnapshot oldVer, CdoSnapshot newVer){
        Diff diff = diffFactory.create(fromSnapshot(oldVer),
                fromSnapshot(newVer), Optional.of(newVer.getCommitMetadata()));
        return diff.getChanges();
    }

    private ShadowGraph fromSnapshot(CdoSnapshot snapshot){
        return new ShadowGraph(Sets.asSet(new ObjectNode(snapshot)));
    }
}
