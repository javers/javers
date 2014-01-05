package org.javers.model.domain;

import org.javers.model.domain.changeType.ReferenceChanged;
import org.javers.model.domain.changeType.ValueChange;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.conditionFulfilled;

/**
 * Change represents <b>atomic</b> difference between two objects.
 * <br/><br/>
 *
 * There are several change types: {@link ValueChange}, {@link ReferenceChanged}, ...
 * For complete list see inheritance hierarchy.
 * <br/><br/>
 *
 * Change is a <i>Value Object</i> and typically can not exists without
 * owning {@link Diff}. For more information see {@link Diff} javadoc.
 *
 * @author bartosz walacik
 */
public abstract class Change {
    private Diff parent;
    private final GlobalCdoId globalCdoId;
    private Object affectedCdo;

    protected Change(GlobalCdoId globalCdoId) {
        argumentIsNotNull(globalCdoId);
        this.globalCdoId = globalCdoId;
    }

    /**
     * Affected Cdo Id
     */
    public GlobalCdoId getGlobalCdoId() {
        return globalCdoId;
    }

    /**
     * Affected Cdo, depending on concrete Change type, it could be new Object, removed Object or new version of changed Object
     * <br/><br/>
     *
     * <b>Transient</b> reference - not null only or freshly generated diff
     */
    public Object getAffectedCdo() {
        return affectedCdo;
    }

    /**
     * Owning aggregate
     */
    public Diff getParent() {
        return parent;
    }

    protected void setAffectedCdo(Object affectedCdo) {
        argumentIsNotNull(affectedCdo);
        conditionFulfilled(this.affectedCdo == null, "affectedCdo already set");
        this.affectedCdo = affectedCdo;
    }

    protected void bind(Diff parent) {
        conditionFulfilled(this.parent == null, "parent Diff already set");
        this.parent = parent;
    }
}
