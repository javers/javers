package org.javers.core.diff.appenders;

import java.util.Set;

import org.javers.common.collections.Function;
import org.javers.common.collections.Sets;
import org.javers.model.domain.Change;
import org.javers.model.domain.Diff;
import org.javers.model.domain.changeType.NewObject;
import org.javers.model.object.graph.ObjectNode;

public class NewObjectAppender extends ChangeSetAppender {

  @Override
  public Set<Change> getChangeSet(final Diff diff, Set<ObjectNode> previousGraph, Set<ObjectNode> currentGraph) {
    Set<ObjectNode> newObjectNodes = Sets.difference(currentGraph, previousGraph);
    return Sets.transform(newObjectNodes, new Function<ObjectNode, Change>() {
      @Override
      public NewObject apply(ObjectNode input) {
        return new NewObject(input.getGlobalCdoId(), diff);
      }
    });
  }
}
