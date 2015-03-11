package org.javers.core.diff;

import org.javers.common.collections.Optional;
import org.javers.common.exception.JaversException;
import org.javers.common.exception.JaversExceptionCode;
import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalId;

import static org.javers.common.string.ToStringBuilder.addFirstField;
import static org.javers.common.validation.Validate.*;

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

    /**
     * Affected domain object GlobalId
     */
    public GlobalId getAffectedGlobalId() {
        return affectedCdoId;
    }

    /**
     * use {@link #getAffectedGlobalId()},
     * left for backward compatibility
     */
    @Deprecated
    public GlobalId getAffectedCdoId() {
        return affectedCdoId;
    }

    /**
     * Affected domain object local Id (value under @Id property)
     */
    public Object getAffectedLocalId() {
        return affectedCdoId.getCdoId();
    }

    /**
     * Affected domain object (Cdo).
     * Depending on concrete Change type,
     * it could be new Object, removed Object or new version of changed Object
     * <br> <br>
     *
     * <b>Optional</b> reference - available only for freshly generated diff
     */
    public Optional<Object> getAffectedObject() {
        return affectedCdo;
    }

    /**
     * use {@link #getAffectedObject()},
     * left for backward compatibility
     */
    @Deprecated
    public Object getAffectedCdo() {
        if (affectedCdo == null || affectedCdo.isEmpty()) {
            throw new JaversException(JaversExceptionCode.AFFECTED_CDO_IS_NOT_AVAILABLE);
        }
        return affectedCdo.get();
    }

    /**
     * Empty if change is calculated by {@link Javers#compare(Object, Object)}
     */
    public Optional<CommitMetadata> getCommitMetadata() {
        return commitMetadata;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" +fieldsToString() +"}";
    }

    protected String fieldsToString(){
        return addFirstField("globalId", getAffectedGlobalId());
    }

    protected void setAffectedCdo(Optional<Object> affectedCdo) {
        argumentIsNotNull(affectedCdo);
        conditionFulfilled(this.affectedCdo == null, "affectedCdo already set");
        this.affectedCdo = affectedCdo;
    }

    /**
     * //TODO reduce visibility to protected
     */
    public void bindToCommit(CommitMetadata commitMetadata) {
        argumentIsNotNull(commitMetadata);

        if (this.commitMetadata.isPresent()) {
            throw new IllegalStateException("Change should be effectively immutable");
        }

        this.commitMetadata = Optional.of(commitMetadata);
    }
}
