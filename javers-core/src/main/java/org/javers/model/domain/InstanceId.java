package org.javers.model.domain;

import org.javers.model.mapping.Entity;

/**
 * Entity instance global identifier, consists of Entity reference and cdoId
 *
 * @author bartosz walacik
 */
public class InstanceId extends GlobalCdoId {
    /**
     *
     * @param cdoId see {@link GlobalCdoId#getCdoId()}
     */
    public InstanceId(Object cdoId, Entity entity) {
        super(cdoId, entity);
    }
}
