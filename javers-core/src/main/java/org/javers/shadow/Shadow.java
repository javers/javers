package org.javers.shadow;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;

/**
 * //TODO javadoc
 *
 * @author bartosz.walacik
 */
public class Shadow {
    private final CommitMetadata commitMetadata;
    private final Object it;

    Shadow(CommitMetadata commitMetadata, Object shadow) {
        Validate.argumentsAreNotNull(commitMetadata, shadow);
        this.commitMetadata = commitMetadata;
        this.it = shadow;
    }

    public CommitMetadata getCommitMetadata() {
        return commitMetadata;
    }

    public Object get() {
        return it;
    }
}
