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
 * Shadows objects are of the same type domain objects.
 * For example, a Shadow of a Person object is an instance of the Person class.
 * <br/><br/>
 *
 * Shadows class is a thin wrapper for a Shadow object and {@link CommitMetadata}
 *
 * @param <T> type of a domain object
 * @author bartosz.walacik
 */
public class Shadow<T> {
    private final CdoSnapshot cdoSnapshot;
    private final CommitMetadata rootCommitMetadata;
    private final T it;

    Shadow(CommitMetadata rootCommitMetadata, CdoSnapshot cdoSnapshot, T shadow) {
        Validate.argumentsAreNotNull(rootCommitMetadata, cdoSnapshot, shadow);
        this.cdoSnapshot = cdoSnapshot;
        this.rootCommitMetadata = rootCommitMetadata;
        this.it = shadow;
    }

    /**
     * Root commit metadata
     */
    public CommitMetadata getCommitMetadata() {
        return rootCommitMetadata;
    }

    /**
     * Root commit Id
     */
    public CommitId getCommitId() {
        return rootCommitMetadata.getId();
    }

    public CdoSnapshot getCdoSnapshot() {
        return cdoSnapshot;
    }

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
                ", commitMetadata=" + getCommitMetadata() +
                '}';
    }
}
