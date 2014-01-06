package org.javers.core.diff.appenders;

import java.util.Set;

import org.javers.common.collections.Function;
import org.javers.common.collections.Sets;
import org.javers.core.diff.Change;
import org.javers.core.diff.changetype.NewObject;
import org.javers.model.object.graph.ObjectNode;

public class NewObjectAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(Set<ObjectNode> leftGraph, Set<ObjectNode> rightGraph) {
        Set<ObjectNode> newObjectNodes = Sets.difference(rightGraph, leftGraph);
        return Sets.transform(newObjectNodes, new Function<ObjectNode, Change>() {
            @Override
            public NewObject apply(ObjectNode input) {
                return new NewObject(input.getGlobalCdoId(),input.getCdo().getWrappedCdo());
            }
        });
    }
}
