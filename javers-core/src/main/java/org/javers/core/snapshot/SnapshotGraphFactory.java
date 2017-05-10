package org.javers.core.snapshot;

import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectNode;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.repository.api.JaversExtendedRepository;

import java.util.*;
import java.util.stream.Collectors;


/**
 * Builds SnapshotGraph from latest snapshots loaded from javersRepository
 */
public class SnapshotGraphFactory {
    private final JaversExtendedRepository javersRepository;

    SnapshotGraphFactory(JaversExtendedRepository javersRepository) {
        this.javersRepository = javersRepository;
    }

    public SnapshotGraph createLatest(Set<GlobalId> globalIds){
        Validate.argumentIsNotNull(globalIds);

        Set<ObjectNode> snapshotNodes = globalIds.stream()
            .map(javersRepository::getLatest)
            .filter(Optional::isPresent).map(Optional::get)
            .map(ObjectNode::new)
            .collect(Collectors.toSet());

        return new SnapshotGraph(snapshotNodes);
    }
}
