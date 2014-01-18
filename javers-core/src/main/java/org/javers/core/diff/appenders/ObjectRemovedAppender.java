package org.javers.core.diff.appenders;

import java.util.HashSet;
import java.util.Set;

import org.javers.common.collections.Function;
import org.javers.common.collections.Sets;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.model.object.graph.Fake;
import org.javers.model.object.graph.ObjectNode;

public class ObjectRemovedAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(Set<ObjectNode> leftGraph, Set<ObjectNode> rightGraph) {
        Set<ObjectNode> removedObjectNodes = Sets.difference(leftGraph, rightGraph);
        Set<Change> changes = new HashSet<>();

            for (ObjectNode node : removedObjectNodes) {
                if (node.getClass() == Fake.class) {
                    continue;
                }
                changes.add(new ObjectRemoved(node.getGlobalCdoId(), node.getCdo().getWrappedCdo()));
            }

        return changes;
    }
}
