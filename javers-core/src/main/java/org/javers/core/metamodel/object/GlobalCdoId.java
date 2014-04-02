package org.javers.core.metamodel.object;

import org.javers.core.metamodel.property.Entity;
import org.javers.core.metamodel.property.ManagedClass;

/**
 * Client's domain object global ID
 */
public abstract class GlobalCdoId {

    /**
     * Class of client's domain object, preferably {@link Entity}
     */
    public abstract ManagedClass getCdoClass();

    /**
     * ID of Client's domain object, should be unique in Class scope,
     * for example database primary key or any domain identifier like user.login
     *
     */
    public abstract Object getCdoId();

    /**
     * <pre>
     * For ex.:
     * org.javers.core.model.SnapshotEntity/1
     * org.javers.core.model.SnapshotEntity/2#setOfValueObjects
     * </pre>
     */
    public String value() {
        return toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || this.getClass() != o.getClass() ) {return false;}

        return value().equals(((GlobalCdoId)o).value());
    }

    @Override
    public int hashCode() {
        return value().hashCode();
    }
}
