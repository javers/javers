package org.javers.core.diff.appenders;

import org.javers.common.collections.Sets;
import org.javers.core.diff.Change;
import org.javers.core.diff.GraphPair;
import org.javers.core.diff.changetype.NewObject;
import java.util.Set;

class NewObjectAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(GraphPair graphPair) {
        return Sets.transform(graphPair.getOnlyOnRight(), input ->
                new NewObject(input.getGlobalId(), input.wrappedCdo()));
    }
}
