package org.javers.core.diff;

import org.javers.common.collections.Optional;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.common.patterns.visitors.Visitable;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalCdoId;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.argumentsAreNotNull;
import static org.javers.common.validation.Validate.conditionFulfilled;

/**
 * Change represents <b>atomic</b> difference between two objects.
 * <br/><br/>
 *
 * There are several change types: {@link ValueChange}, {@link org.javers.core.diff.changetype.ReferenceChange}, ...
 * For complete list see inheritance hierarchy.
 * <br/><br/>
 *
 * Change is a <i>Value Object</i> and typically can not exists without
 * owning {@link org.javers.core.diff.Diff}. For more information see {@link org.javers.core.diff.Diff} javadoc.
 *
 * @author bartosz walacik
 */
public abstract class Change implements Visitable<ChangeVisitor> {
    //private Diff parent;

    private Optional<CommitMetadata> commitMetadata;
    private final GlobalCdoId affectedCdoId;

    private transient Optional<Object> affectedCdo;

    protected Change(GlobalCdoId affectedCdoId) {
        argumentsAreNotNull(affectedCdoId);
        this.affectedCdoId = affectedCdoId;
        this.commitMetadata = Optional.empty();
    }

    protected Change(GlobalCdoId affectedCdoId, CommitMetadata commitMetadata) {
        argumentsAreNotNull(affectedCdoId, commitMetadata);
        this.affectedCdoId = affectedCdoId;
        this.commitMetadata = Optional.of(commitMetadata);
    }


    //TODO protected
    public void bindToCommit(CommitMetadata commitMetadata) {
        argumentIsNotNull(commitMetadata);

        this.commitMetadata = Optional.of(commitMetadata);
    }

    /**
     * Affected Cdo Id
     */
    public GlobalCdoId getAffectedCdoId() {
        return affectedCdoId;
    }

    /**
     * Affected Cdo, depending on concrete Change type,
     * it could be new Object, removed Object or new version of changed Object
     * <br/>
     *
     * <b>Transient</b> reference - available only for freshly generated diff
     *
     * @throws JaversException AFFECTED_CDO_IS_NOT_AVAILABLE
     */
    public Object getAffectedCdo() {
        if (affectedCdo == null || affectedCdo.isEmpty()){
            throw new JaversException(JaversExceptionCode.AFFECTED_CDO_IS_NOT_AVAILABLE);
        }
        return affectedCdo.get();
    }

    protected void setAffectedCdo(Optional<Object> affectedCdo) {
        argumentIsNotNull(affectedCdo);
        conditionFulfilled(this.affectedCdo == null, "affectedCdo already set");
        this.affectedCdo = affectedCdo;
    }

    @Override
    public void accept(ChangeVisitor changeVisitor) {
        changeVisitor.visit(this);
    }

    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }
}
