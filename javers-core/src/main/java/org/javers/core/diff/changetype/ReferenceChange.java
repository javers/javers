package org.javers.core.diff.changetype;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.MissingProperty;

import java.util.Objects;
import java.util.Optional;


/**
 * Changed reference to Entity or Value Object
 *
 * @author bartosz walacik
 */
public class ReferenceChange extends PropertyChange {
    private final GlobalId left;
    private final GlobalId right;
    private transient final Optional<Object> leftObject;
    private transient final Optional<Object> rightObject;

    public static ReferenceChange create(GlobalId affectedCdoId, String propertyName, GlobalId leftReference, GlobalId rightReference, Object leftObject, Object rightObject) {
        if (MissingProperty.INSTANCE == leftObject) {
            return new ReferenceChange.ReferenceAddedChange(affectedCdoId, propertyName, rightReference, rightObject, Optional.empty());
        }
        if (MissingProperty.INSTANCE == rightObject) {
            return new ReferenceChange.ReferenceRemovedChange(affectedCdoId, propertyName, leftReference, leftObject, Optional.empty());

        }
        return new ReferenceChange(affectedCdoId, propertyName, leftReference, rightReference, leftObject, rightObject, Optional.empty());
    }

    public ReferenceChange(GlobalId affectedCdoId, String propertyName, GlobalId leftReference,
                           GlobalId rightReference, Object leftObject, Object rightObject, Optional<CommitMetadata> commitMetadata ) {
        super(affectedCdoId, propertyName, commitMetadata);
        this.left = leftReference;
        this.right = rightReference;
        this.leftObject = Optional.ofNullable(leftObject);
        this.rightObject = Optional.ofNullable(rightObject);
    }

    /**
     * GlobalId of left (or previous) domain object reference
     */
    public GlobalId getLeft() {
        return left;
    }

    /**
     * GlobalId of right (or current) domain object reference
     */
    public GlobalId getRight() {
        return right;
    }

    /**
     * Domain object reference at left side of a diff.
     *<br/><br/>
     *
     * <b>Optional</b> - available only for freshly generated diff.
     * Not available for Changes read from JaversRepository
     */
    public Optional<Object> getLeftObject() {
    	return leftObject;
    }

    /**
     * Domain object reference at right side of a diff.
     *<br/><br/>
     *
     * <b>Optional</b> - available only for freshly generated diff.
     * Not available for Changes read from JaversRepository
     */
    public Optional<Object> getRightObject() {
    	return rightObject;
    }

    @Override
    public String prettyPrint(PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                " changed from " + valuePrinter.formatWithQuotes(getLeft()) + " to " +
                valuePrinter.formatWithQuotes(getRight());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ReferenceChange) {
            ReferenceChange that = (ReferenceChange) obj;
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

    public static class ReferenceRemovedChange extends ReferenceChange {
        public ReferenceRemovedChange(GlobalId affectedCdoId, String propertyName, GlobalId left, Object leftObject, Optional<CommitMetadata> commitMetadata) {
            super(affectedCdoId, propertyName, left, null, leftObject, null, commitMetadata);
        }

        @Override
        public String prettyPrint(final PrettyValuePrinter valuePrinter) {
            Validate.argumentIsNotNull(valuePrinter);

            return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) + " was removed, value "
                    + valuePrinter.formatWithQuotes(getLeft());
        }
    }

    public static class ReferenceAddedChange extends ReferenceChange {

        public ReferenceAddedChange(GlobalId affectedCdoId, String propertyName, GlobalId right, Object rightObject, Optional<CommitMetadata> commitMetadata) {
            super(affectedCdoId, propertyName, null, right, null, rightObject, commitMetadata);
        }

        @Override
        public String prettyPrint(final PrettyValuePrinter valuePrinter) {
            Validate.argumentIsNotNull(valuePrinter);

            return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) + " was added with value "
                    + valuePrinter.formatWithQuotes(getRight());
        }
    }
}
