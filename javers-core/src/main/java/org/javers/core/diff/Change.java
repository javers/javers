package org.javers.core.diff;

import java.util.Optional;

import org.javers.common.string.ToStringBuilder;
import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.changetype.NewObject;
import org.javers.core.diff.changetype.ReferenceChange;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;

import java.io.Serializable;
import java.util.Objects;

import static org.javers.common.string.ToStringBuilder.addFirstField;
import static org.javers.common.validation.Validate.*;

/**
 * Change represents an <b>atomic</b> difference between two objects.
 * <br><br>
 *
 * There are several types fo change:
 * {@link ValueChange}, {@link ReferenceChange}, {@link ListChange}, {@link NewObject}, and so on.
 * See the inheritance hierarchy for the complete list.
 * <br><br>
 *
 * @see Diff
 * @author bartosz walacik
 */
public abstract class Change implements Serializable {

    private CommitMetadata commitMetadata; //optional, can't use Optional here, because it isn't Serializable
    private final GlobalId affectedCdoId;
    private transient Object affectedCdo;  //optional

    protected Change(GlobalId affectedCdoId) {
        this(affectedCdoId, Optional.empty());
    }

    protected Change(GlobalId affectedCdoId, Optional<Object> affectedCdo) {
        this(affectedCdoId, affectedCdo, Optional.empty());
    }

    protected Change(GlobalId affectedCdoId, Optional<Object> affectedCdo, Optional<CommitMetadata> commitMetadata) {
        argumentsAreNotNull(affectedCdoId);
        this.affectedCdoId = affectedCdoId;
        this.commitMetadata = null;
        affectedCdo.ifPresent(cdo -> this.affectedCdo = cdo);
        commitMetadata.ifPresent(meta -> this.commitMetadata = meta);
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
        return Optional.ofNullable(affectedCdo);
    }

    /**
     * Empty if change is calculated by {@link Javers#compare(Object, Object)}
     */
    public Optional<CommitMetadata> getCommitMetadata() {
        return Optional.ofNullable(commitMetadata);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{ " +fieldsToString() +" }";
    }

    protected String fieldsToString(){
        return "globalId:" + ToStringBuilder.format(getAffectedGlobalId());
    }

    void setAffectedCdo(Object affectedCdo) {
        argumentIsNotNull(affectedCdo);
        conditionFulfilled(this.affectedCdo == null, "affectedCdo already set");
        this.affectedCdo = affectedCdo;
    }

    void bindToCommit(CommitMetadata commitMetadata) {
        argumentIsNotNull(commitMetadata);

        if (this.commitMetadata != null) {
            throw new IllegalStateException("Change should be effectively immutable");
        }

        this.commitMetadata = commitMetadata;
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
