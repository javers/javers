package org.javers.model.domain;

import org.javers.model.mapping.Property;

import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Change represents atomic difference between two client's domain objects
 *
 * @author bartosz walacik
 */
public abstract class Change {
    private final GlobalCdoId globalCdoId;

    protected Change(GlobalCdoId globalCdoId) {
        argumentIsNotNull(globalCdoId);
        this.globalCdoId = globalCdoId;
    }

    /**
     * Affected object
     */
    public GlobalCdoId getGlobalCdoId() {
        return globalCdoId;
    }
}
