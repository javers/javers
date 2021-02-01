package org.javers.core.diff.appenders;

import org.javers.core.CoreConfiguration;
import org.javers.core.diff.Change;
import org.javers.core.diff.GraphPair;
import org.javers.core.diff.changetype.NewObject;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

class NewObjectAppender implements NodeChangeAppender {

    private final CoreConfiguration coreConfiguration;

    public NewObjectAppender(CoreConfiguration coreConfiguration) {
        this.coreConfiguration = coreConfiguration;
    }

    @Override
    public Set<Change> getChangeSet(GraphPair graphPair) {
        if (!coreConfiguration.isNewObjectChanges()) {
            return Collections.emptySet();
        }

        return (Set)graphPair.getOnlyOnRight().stream()
                .map(input -> new NewObject(input.getGlobalId(), input.wrappedCdo(), graphPair.getCommitMetadata()))
                .collect(Collectors.toSet());
    }
}
