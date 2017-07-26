package org.javers.core;

import org.javers.repository.api.JaversRepository;

/**
 * @author bartosz.walacik
 */
public enum CommitIdGenerator {
    /**
     * Generates neat sequence of commitId numbers. Based on {@link JaversRepository#getHeadId()}.
     * <br/>
     * Should not be used in distributed applications.
     */
    SYNCHRONIZED_SEQUENCE,

    /**
     * Fast algorithm based on UUID. For distributed applications.
     * @deprecated
     */
    @Deprecated
    RANDOM
}
