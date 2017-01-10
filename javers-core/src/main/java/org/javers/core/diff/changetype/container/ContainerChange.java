package org.javers.core.diff.changetype.container;

import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.common.collections.Predicate;
import org.javers.common.validation.Validate;
import org.javers.core.diff.changetype.PropertyChange;
import org.javers.core.metamodel.object.GlobalId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.javers.common.string.ToStringBuilder.addEnumField;

/**
 * Collection or Array change
 *
 * @author bartosz walacik
 */
public abstract class ContainerChange extends PropertyChange {
    private final List<ContainerElementChange> changes;

    ContainerChange(GlobalId affectedCdoId, String propertyName, List<ContainerElementChange> changes) {
        super(affectedCdoId, propertyName);
        Validate.argumentIsNotNull(changes);
        Validate.argumentCheck(!changes.isEmpty(),"changes list should not be empty");
        this.changes = Collections.unmodifiableList(new ArrayList<>(changes));
    }

    public List<ContainerElementChange> getChanges() {
        return changes;
    }

    public List<ValueAdded> getValueAddedChanges() {
        return (List)Lists.positiveFilter(changes, new Predicate<ContainerElementChange>() {
            public boolean apply(ContainerElementChange input) {
                return input instanceof ValueAdded;
            }
        });
    }

    public List<ValueRemoved> getValueRemovedChanges() {
        return (List)Lists.positiveFilter(changes, new Predicate<ContainerElementChange>() {
            public boolean apply(ContainerElementChange input) {
                return input instanceof ValueRemoved;
            }
        });
    }

    public List<?> getAddedValues() {
        return Lists.transform(getValueAddedChanges(), new Function<ValueAdded, Object>() {
            public Object apply(ValueAdded input) {
                return input.getAddedValue();
            }
        });
    }

    public List<?> getRemovedValues() {
        return Lists.transform(getValueRemovedChanges(), new Function<ValueRemoved, Object>() {
            public Object apply(ValueRemoved input) {
                return input.getRemovedValue();
            }
        });
    }

    @Override
    protected String fieldsToString() {
        StringBuilder changesAsString = new StringBuilder();

        for (ContainerElementChange c : changes){
            if (changesAsString.length() > 0) { changesAsString.append(", "); }
            changesAsString.append(c);
        }
        return super.fieldsToString() + addEnumField("containerChanges", changesAsString);
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
}
