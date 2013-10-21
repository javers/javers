package org.javers.core.diff.appenders;

import java.util.Set;

import org.javers.model.domain.Change;
import org.javers.model.domain.Diff;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.object.graph.ObjectNode;

public abstract class ChangeSetAppender {

  public void append(Diff currentDiff, Set<ObjectNode> previousGraph, Set<ObjectNode> currentGraph) {
    for (Change change : getChangeSet(currentDiff, previousGraph, currentGraph)) {
      currentDiff.addChange(change);
    }
  }

  protected GlobalCdoId createFromObjectNode(ObjectNode objectNode) {
    return new GlobalCdoId(objectNode.getEntity(), objectNode.getCdoId());
  }

  protected abstract Set<Change> getChangeSet(Diff diff, Set<ObjectNode> previousGraph, Set<ObjectNode> currentGraph);

}
