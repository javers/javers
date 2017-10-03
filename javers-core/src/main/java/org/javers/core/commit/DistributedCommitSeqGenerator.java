package org.javers.core.commit;

import java.util.UUID;

/**
 * Generates commitId with random majorId and minorId set to 0.
 * Can be used in distributed applications.
 *
 * @deprecated
 * @author bartosz.walacik
 */
@Deprecated
class DistributedCommitSeqGenerator {
    CommitId nextId() {
        return new CommitId(Math.abs(UUID.randomUUID().getLeastSignificantBits()),0);
    }
}
