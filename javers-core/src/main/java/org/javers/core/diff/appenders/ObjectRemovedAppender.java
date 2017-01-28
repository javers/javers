package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.Change;
import org.javers.core.diff.GraphPair;
import org.javers.core.diff.changetype.ObjectRemoved;

import java.util.Set;

class ObjectRemovedAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(GraphPair graphPair) {
        return Sets.transform(graphPair.getOnlyOnLeft(), input ->
                new ObjectRemoved(input.getGlobalId(), input.wrappedCdo())
        );
    }
}
