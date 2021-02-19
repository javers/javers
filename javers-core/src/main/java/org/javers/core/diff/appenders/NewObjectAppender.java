package org.javers.core.diff.appenders;

import org.javers.core.diff.Change;
import org.javers.core.diff.GraphPair;
import org.javers.core.diff.changetype.NewObject;

import java.util.Set;
import java.util.stream.Collectors;

class NewObjectAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(GraphPair graphPair) {
        return (Set)graphPair.getOnlyOnRight().stream()
                .map(input -> new NewObject(input.getGlobalId(), input.wrappedCdo(), graphPair.getCommitMetadata()))
                .collect(Collectors.toSet());
    }
}
