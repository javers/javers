package org.javers.shadow;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.repository.api.JaversRepository;

/**
 * Shadow is a historical version of your domain object restored
 * from a snapshot (that was loaded from {@link JaversRepository})
 * <br/><br/>
 *
 * Shadows use the same types as domain objects.
 * For example, a Shadow of a Person object is an instance of Person.class.
 *
 * <br/><br/>
 * Shadows class is a thin wrapper for a shadow object and {@link CommitMetadata}
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

    /**
     * @return Shadow
     */
    public Object get() {
        return it;
    }
}
