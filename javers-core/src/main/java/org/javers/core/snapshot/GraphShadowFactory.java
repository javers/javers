package org.javers.core.snapshot;

import org.javers.common.validation.Validate;
import org.javers.core.graph.ObjectNode;
import org.javers.repository.api.JaversRepository;

/**
 * Restores objects graph snapshot
 *
 * @author bartosz walacik
 */
public class GraphShadowFactory {
    private final JaversRepository repository ;

    public GraphShadowFactory(JaversRepository repository) {
        this.repository = repository;
    }

    public ObjectNode createLatestShadow(Object currentVersion){
        Validate.argumentIsNotNull(currentVersion);
        return null;
    }
}
