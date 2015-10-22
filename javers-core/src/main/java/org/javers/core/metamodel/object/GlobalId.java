package org.javers.core.metamodel.object;

import org.javers.core.metamodel.type.ManagedType;
import org.javers.repository.jql.GlobalIdDTO;

/**
 * Global ID of Client's domain object (CDO)
 */
public abstract class GlobalId {

    /**
     * JaversType of client's domain object
     */
    public abstract ManagedType getManagedType();

    /**
     * ID of Client's domain object, should be unique in Class scope,
     * for example database primary key or any domain identifier like user.login
     */
    public abstract Object getCdoId();

    /**
     * <pre>
     * For ex.:
     * org.javers.core.model.SnapshotEntity/1
     * org.javers.core.model.SnapshotEntity/2#setOfValueObjects
     * </pre>
     */
    public abstract String value();

    @Override
    public String toString(){
        return this.value();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }

        //for testing
        if (o != null && o instanceof GlobalIdDTO) {
            return equals((GlobalIdDTO)o);
        }

        if (o == null || !(o instanceof GlobalId) ) {return false;}

        return value().equals(((GlobalId) o).value());
    }

    //for testing
    private boolean equals(GlobalIdDTO o) {
        return this.value().equals(o.value());
    }

    @Override
    public int hashCode() {
        return value().hashCode();
    }
}
