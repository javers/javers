package org.javers.core.diff.changetype.container;

import org.javers.common.collections.Lists;
import org.javers.common.string.PrettyValuePrinter;
import org.javers.common.validation.Validate;
import org.javers.core.diff.changetype.Atomic;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.diff.changetype.PropertyChangeMetadata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Changes on an Array or Collection property
 *
 * @author bartosz walacik
 */
public abstract class ContainerChange<T> extends PropertyChange<T> {
    private final List<ContainerElementChange> changes;

    private transient final T left;
    private transient final T right;

    ContainerChange(PropertyChangeMetadata metadata, List<ContainerElementChange> changes, T left, T right) {
        super(metadata);
        Validate.argumentIsNotNull(changes);
        Validate.argumentCheck(!changes.isEmpty(),"changes list should not be empty");
        this.changes = Collections.unmodifiableList(new ArrayList<>(changes));
        this.left = left;
        this.right = right;
    }

    /**
     * Right (or new) Collection or array
     */
    @Override
    public T getRight() {
        return right;
    }

    /**
     * Left (or old) Collection or array
     */
    @Override
    public T getLeft() {
        return left;
    }

    public abstract int getRightSize();

    public abstract int getLeftSize();

    public List<ContainerElementChange> getChanges() {
        return changes;
    }

    public List<ValueAdded> getValueAddedChanges() {
        return (List)Lists.positiveFilter(changes, input -> input instanceof ValueAdded);
    }

    public List<ValueRemoved> getValueRemovedChanges() {
        return (List)Lists.positiveFilter(changes, input -> input instanceof ValueRemoved);
    }

    public List<?> getAddedValues() {
        return Lists.transform(getValueAddedChanges(), input -> input.getAddedValue());
    }

    public List<?> getRemovedValues() {
        return Lists.transform(getValueRemovedChanges(), input -> input.getRemovedValue());
    }

    @Override
    public String prettyPrint(PrettyValuePrinter valuePrinter) {
        Validate.argumentIsNotNull(valuePrinter);

        StringBuilder builder = new StringBuilder();

        builder.append(valuePrinter.formatWithQuotes(getPropertyNameWithPath()) + " collection changes :\n");

        changes.forEach(cc -> builder.append("   " + cc.prettyPrint(valuePrinter)+"\n"));

        String result = builder.toString();
        return result.substring(0, result.length() - 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ContainerChange) {
            ContainerChange that = (ContainerChange) obj;
            return super.equals(that)
                    && Objects.equals(this.changes, that.changes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), changes);
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{ property: '"+getPropertyName() +"'," +
                " elementChanges:"+changes.size() + ", left.size: "+getLeftSize()+", right.size: "+getRightSize()+"}";
    }
}
