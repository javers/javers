package org.javers.core;

import org.javers.core.commit.CommitMetadata;
import org.javers.repository.api.JaversRepository;

import java.util.Comparator;

/**
 * @author bartosz.walacik
 */
public enum CommitIdGenerator {
    /**
     * Generates neat sequence of commitId numbers. Based on {@link JaversRepository#getHeadId()}.
     * <br/>
     * Should not be used in distributed applications.
     */
    SYNCHRONIZED_SEQUENCE {
        public Comparator<CommitMetadata> getComparator() {
            return Comparator.comparing(CommitMetadata::getId);
        }
    },

    /**
     * Fast algorithm based on UUID. For distributed applications.
     * @deprecated
     */
    @Deprecated
    RANDOM {
        public Comparator<CommitMetadata> getComparator() {
            return Comparator.comparing(CommitMetadata::getCommitDate);
        }
    },

    /**
     * Provided by user
     */
    CUSTOM {
        public Comparator<CommitMetadata> getComparator() {
            return Comparator.comparing(CommitMetadata::getCommitDate);
        }
    };

    public abstract Comparator<CommitMetadata> getComparator();
}
