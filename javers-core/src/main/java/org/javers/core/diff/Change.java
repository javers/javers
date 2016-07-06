package org.javers.core.diff;

import org.javers.common.collections.Optional;
import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;

import java.io.Serializable;
import java.util.Objects;

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
public abstract class Change implements Serializable {

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
     * Affected domain object local Id (value under @Id property)
     */
    public Object getAffectedLocalId() {
        if (affectedCdoId instanceof InstanceId){
            return ((InstanceId) affectedCdoId).getCdoId();
        }
        return null;
    }

    /**
     * Affected domain object (Cdo).
     * Depending on concrete Change type,
     * it could be new Object, removed Object or new version of changed Object.
     * <br/><br/>
     *
     * <b>Optional</b> - available only for freshly generated diff.
     * Not available for Changes read from JaversRepository
     */
    public Optional<Object> getAffectedObject() {
        return affectedCdo;
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

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Change) {
            Change that = (Change) obj;
            return Objects.equals(this.affectedCdoId, that.affectedCdoId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.affectedCdoId);
    }
}
