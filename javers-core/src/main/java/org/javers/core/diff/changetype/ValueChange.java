package org.javers.core.diff.changetype;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Objects;
import java.util.Optional;

/**
 * Change on a Value property, like int or String
 *
 * @author bartosz walacik
 */
public abstract class ValueChange extends PropertyChange {
    public ValueChange(GlobalId affectedCdoId, String propertyName) {
        this(affectedCdoId, propertyName, Optional.empty());
    }

    public ValueChange(GlobalId affectedCdoId, String propertyName, Optional<CommitMetadata> commitMetadata) {
        super(affectedCdoId, propertyName, commitMetadata);
    }

    public abstract Object getLeft();

    public abstract Object getRight();

    @Override
    public abstract String prettyPrint(PrettyValuePrinter valuePrinter);

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ValueChange) {
            ValueChange that = (ValueChange) obj;
            return super.equals(that)
                    && Objects.equals(this.getLeft(), that.getLeft())
                    && Objects.equals(this.getRight(), that.getRight());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getLeft(), getRight());
    }
}
