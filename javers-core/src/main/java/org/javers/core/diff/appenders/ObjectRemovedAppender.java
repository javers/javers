package org.javers.core.diff.appenders;

import org.javers.core.diff.Change;
import org.javers.core.diff.GraphPair;
import org.javers.core.diff.changetype.ObjectRemoved;
import org.javers.core.metamodel.type.ValueObjectType;

import java.util.Set;
import java.util.stream.Collectors;

class ObjectRemovedAppender implements NodeChangeAppender {

    @Override
    public Set<Change> getChangeSet(GraphPair graphPair) {
        return (Set)graphPair.getOnlyOnLeft().stream()
                .filter(it -> !(it.getManagedType() instanceof ValueObjectType) )
                .map(input -> new ObjectRemoved(input.getGlobalId(), input.wrappedCdo(), graphPair.getCommitMetadata()))
                .collect(Collectors.toSet());
    }
}
