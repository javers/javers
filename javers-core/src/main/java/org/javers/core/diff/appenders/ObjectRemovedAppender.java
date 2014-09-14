package org.javers.core.diff.appenders;

import org.javers.common.collections.Function;
import org.javers.common.collections.Sets;
import org.javers.core.diff.Change;
import org.javers.core.diff.GraphPair;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.graph.ObjectNode;

import java.util.Set;

public class ObjectRemovedAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(GraphPair graphPair) {
        return Sets.transform(graphPair.getOnlyOnLeft(), new Function<ObjectNode, Change>() {
            @Override
            public ObjectRemoved apply(ObjectNode input) {
                return new ObjectRemoved(input.getGlobalId(), input.wrappedCdo());
            }
        });
    }
}
