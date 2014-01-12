package org.javers.model.domain;

import org.javers.model.mapping.Entity;
import org.javers.model.mapping.ManagedClass;

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
}
