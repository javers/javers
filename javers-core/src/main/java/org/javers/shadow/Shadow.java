package org.javers.shadow;

import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.repository.api.JaversRepository;

/**
 * Shadow is a historical version of a domain object restored
 * from a snapshot loaded from {@link JaversRepository}.
 * <br/><br/>
 *
 * Shadows use the same types as domain objects.
 * For example, a Shadow of a Person object is an instance of Person.class.
 * <br/><br/>
 *
 * Shadows class is a thin wrapper for a Shadow object and {@link CommitMetadata}
 *
 * @param <T> type of a domain object
 * @author bartosz.walacik
 */
public class Shadow<T> {
    private final CommitMetadata commitMetadata;
    private final CdoSnapshot cdoSnapshot;
    private final T it;

    Shadow(CommitMetadata commitMetadata, CdoSnapshot cdoSnapshot, T shadow) {
        Validate.argumentsAreNotNull(commitMetadata, shadow);
        this.commitMetadata = commitMetadata;
        this.cdoSnapshot = cdoSnapshot;
        this.it = shadow;
    }

    public CommitMetadata getCommitMetadata() {
        return commitMetadata;
    }

    public CommitId getCommitId() {
        return commitMetadata.getId();
    }

    public CdoSnapshot getCdoSnapshot() { return cdoSnapshot; }

    /**
     * Shadow object per se
     */
    public T get() {
        return it;
    }

    @Override
    public String toString() {
        return "Shadow{" +
                "it=" + it +
                ", commitMetadata=" + commitMetadata +
                '}';
    }
}
