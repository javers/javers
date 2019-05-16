package org.javers.core.diff.changetype;

import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;

import java.util.Objects;

/**
 * Change on a Value property, like int or String
 *
 * @author bartosz walacik
 */
public class ValueChange extends PropertyChange {
    private final Atomic left;
    private final Atomic right;

    public ValueChange(PropertyChangeMetadata metadata, Object leftValue, Object rightValue){
        super(metadata);
        this.left = new Atomic(leftValue);
        this.right = new Atomic(rightValue);
    }

    public Object getLeft() {
        return left.unwrap();
    }

    public Object getRight() {
        return right.unwrap();
    }

    @Override
    public String prettyPrint(PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        if (isPropertyAdded()) {
            return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                    " property with value " + valuePrinter.formatWithQuotes(right.unwrap()) +" added";
        }
        else if (isPropertyRemoved()) {
            return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                    " property with value " + valuePrinter.formatWithQuotes(left.unwrap()) +" removed";
        } else {
            return valuePrinter.formatWithQuotes(getPropertyNameWithPath()) +
                " value changed from " + valuePrinter.formatWithQuotes(left.unwrap()) + " to " +
                                   valuePrinter.formatWithQuotes(right.unwrap());
        }
    }

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
