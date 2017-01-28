package org.javers.core.changelog;

import java.util.Optional;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.*;
import org.javers.core.diff.changetype.container.ArrayChange;
import org.javers.core.diff.changetype.container.ContainerChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.diff.changetype.container.SetChange;
import org.javers.core.diff.changetype.map.MapChange;
import org.javers.core.metamodel.object.GlobalId;

import java.util.List;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * @author bartosz walacik
 */
public class ChangeListTraverser {

    public static void traverse(List<Change> changes, ChangeProcessor renderer) {
        argumentIsNotNull(changes);
        argumentIsNotNull(renderer);

        Optional<CommitMetadata> lastCommit = Optional.empty();
        GlobalId lastGlobalId = null;

        renderer.beforeChangeList();

        for (Change change : changes){
            if (change.getCommitMetadata().isPresent() && !change.getCommitMetadata().equals(lastCommit)) {
                renderer.onCommit(change.getCommitMetadata().get());
                lastGlobalId = null;
            }

            if (!change.getAffectedGlobalId().equals(lastGlobalId) &&
                !(change instanceof NewObject || change instanceof ObjectRemoved)) {
                renderer.onAffectedObject(change.getAffectedGlobalId());
            }

            renderer.beforeChange(change);

            if (change instanceof NewObject){
                renderer.onNewObject((NewObject) change);
            }

            if (change instanceof ObjectRemoved){
                renderer.onObjectRemoved((ObjectRemoved) change);
            }

            if (change instanceof PropertyChange){
                renderer.onPropertyChange((PropertyChange)change);
            }

            if (change instanceof ContainerChange){
                renderer.onContainerChange((ContainerChange) change);
            }

            if (change instanceof ValueChange){
                renderer.onValueChange((ValueChange) change);
            }

            if (change instanceof ReferenceChange){
                renderer.onReferenceChange((ReferenceChange) change);
            }

            if (change instanceof ListChange){
                renderer.onListChange((ListChange) change);
            }

            if (change instanceof MapChange){
                renderer.onMapChange((MapChange) change);
            }

            if (change instanceof SetChange){
                renderer.onSetChange((SetChange) change);
            }

            if (change instanceof ArrayChange){
                renderer.onArrayChange((ArrayChange) change);
            }

            renderer.afterChange(change);

            lastCommit = change.getCommitMetadata();
            lastGlobalId = change.getAffectedGlobalId();
        }

        renderer.afterChangeList();
    }
}
