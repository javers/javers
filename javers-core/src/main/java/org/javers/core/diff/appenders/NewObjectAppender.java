package org.javers.core.diff.appenders;

import org.javers.common.collections.Function;
import org.javers.common.collections.Sets;
import org.javers.core.diff.Change;
import org.javers.core.diff.GraphPair;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.graph.ObjectNode;

import java.util.Set;

public class NewObjectAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(GraphPair graphPair) {
        return Sets.transform(graphPair.getOnlyOnRight(), new Function<ObjectNode, Change>() {
            @Override
            public NewObject apply(ObjectNode input) {
                return new NewObject(input.getGlobalId(),input.wrappedCdo());
            }
        });
    }
}
