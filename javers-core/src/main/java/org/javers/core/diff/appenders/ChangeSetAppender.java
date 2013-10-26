package org.javers.core.diff.appenders;

import java.util.Set;

import org.javers.model.domain.Change;
import org.javers.model.domain.Diff;
import org.javers.model.domain.GlobalCdoId;
import org.javers.model.object.graph.ObjectNode;

public abstract class ChangeSetAppender {

  public void append(Diff currentDiff, Set<ObjectNode> previousGraph, Set<ObjectNode> currentGraph) {
    for (Change change : getChangeSet(previousGraph, currentGraph)) {
      currentDiff.addChange(change);
    }
  }

  protected abstract Set<Change> getChangeSet(Set<ObjectNode> previousGraph, Set<ObjectNode> currentGraph);

}
