package org.javers.core.diff.changetype;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.GlobalId;

import java.util.Objects;
import java.util.Optional;

import static org.javers.common.string.ToStringBuilder.addField;
import static org.javers.common.validation.Validate.argumentIsNotNull;

/**
 * Property change like {@link ValueChange} or {@link ReferenceChange}
 *
 * @author bartosz walacik
 */
public abstract class PropertyChange extends Change {
    private final String propertyName;

    protected PropertyChange(GlobalId affectedCdoId, String propertyName, Optional<CommitMetadata> commitMetadata) {
        super(affectedCdoId, Optional.empty(), commitMetadata);
        argumentIsNotNull(propertyName);
        this.propertyName = propertyName;
    }

    public String getPropertyName(){
        return propertyName;
    }

    @Override
    protected String fieldsToString() {
        return super.fieldsToString() + ", " + propertyName + "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PropertyChange) {
            PropertyChange that = (PropertyChange) o;
            return super.equals(that) &&
                    Objects.equals(this.propertyName, that.propertyName);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), propertyName);
    }
}
