package org.javers.core.diff.changetype;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Objects;
import java.util.Optional;


/**
 * Changed reference to Entity or Value Object
 *
 * @author bartosz walacik
 */
public abstract class ReferenceChange extends PropertyChange {

    public ReferenceChange(GlobalId affectedCdoId, String propertyName) {
        this(affectedCdoId, propertyName, Optional.empty());
    }

    public ReferenceChange(GlobalId affectedCdoId, String propertyName, Optional<CommitMetadata> commitMetadata ) {
        super(affectedCdoId, propertyName, commitMetadata);
    }

    /**
     * GlobalId of left (or previous) domain object reference
     */
    public abstract GlobalId getLeft();

    /**
     * GlobalId of right (or current) domain object reference
     */
    public abstract GlobalId getRight();

    /**
     * Domain object reference at left side of a diff.
     *<br/><br/>
     *
     * <b>Optional</b> - available only for freshly generated diff.
     * Not available for Changes read from JaversRepository
     */
    public abstract Optional<Object> getLeftObject();

    /**
     * Domain object reference at right side of a diff.
     *<br/><br/>
     *
     * <b>Optional</b> - available only for freshly generated diff.
     * Not available for Changes read from JaversRepository
     */
    public abstract Optional<Object> getRightObject();

    @Override
    public abstract String prettyPrint(PrettyValuePrinter valuePrinter);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ReferenceChange) {
            ReferenceChange that = (ReferenceChange) obj;
            return super.equals(that) &&
                Objects.equals(this.getAffectedGlobalId(), that.getAffectedGlobalId());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLeft(), getRight());
    }
}
