package org.javers.core.diff;

import org.javers.common.collections.Optional;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.common.patterns.visitors.Visitable;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalId;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;
import static org.javers.common.validation.Validate.conditionFulfilled;

/**
 * Change represents <b>atomic</b> difference between two objects.
 * <br><br>
 *
 * There are several change types: {@link ValueChange}, {@link ReferenceChange}, ...
 * For complete list see inheritance hierarchy.
 * <br><br>
 *
 * Change is a <i>Value Object</i> and typically can not exists without
 * owning {@link Diff}. For more information see {@link Diff} javadoc.
 *
 * @author bartosz walacik
 */
public abstract class Change {

    private Optional<CommitMetadata> commitMetadata;
    private final GlobalId affectedCdoId;

    private transient Optional<Object> affectedCdo;

    protected Change(GlobalId affectedCdoId) {
        argumentsAreNotNull(affectedCdoId);
        this.affectedCdoId = affectedCdoId;
        this.commitMetadata = Optional.empty();
    }

    protected Change(GlobalId affectedCdoId, CommitMetadata commitMetadata) {
        argumentsAreNotNull(affectedCdoId, commitMetadata);
        this.affectedCdoId = affectedCdoId;
        this.commitMetadata = Optional.of(commitMetadata);
    }

    public void bindToCommit(CommitMetadata commitMetadata) {
        argumentIsNotNull(commitMetadata);

        if (this.commitMetadata.isPresent()) {
            throw new IllegalStateException("Change should be effectively immutable");
        }

        this.commitMetadata = Optional.of(commitMetadata);
    }

    /**
     * Affected Cdo Id
     */
    public GlobalId getAffectedCdoId() {
        return affectedCdoId;
    }

    /**
     * Affected Cdo, depending on concrete Change type,
     * it could be new Object, removed Object or new version of changed Object
     * <br> <br>
     *
     * <b>Transient</b> reference - available only for freshly generated diff
     *
     * @throws JaversException AFFECTED_CDO_IS_NOT_AVAILABLE
     */
    public Object getAffectedCdo() {
        if (affectedCdo == null || affectedCdo.isEmpty()) {
            throw new JaversException(JaversExceptionCode.AFFECTED_CDO_IS_NOT_AVAILABLE);
        }
        return affectedCdo.get();
    }

    protected void setAffectedCdo(Optional<Object> affectedCdo) {
        argumentIsNotNull(affectedCdo);
        conditionFulfilled(this.affectedCdo == null, "affectedCdo already set");
        this.affectedCdo = affectedCdo;
    }

    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }
}
