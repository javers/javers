package org.javers.model.domain;

import org.javers.common.validation.Validate;
import org.javers.model.mapping.Property;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import org.javers.model.domain.changeType.*;

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

    protected Change(GlobalCdoId globalCdoId) {
        argumentIsNotNull(globalCdoId);
        this.globalCdoId = globalCdoId;
    }

    /**
     * Affected object
     */
    public GlobalCdoId getGlobalCdoId() {
        return globalCdoId;
    }

    /**
     * Owning aggregate
     */
    public Diff getParent() {
        return parent;
    }

    public void bind(Diff parent) {
        Validate.conditionFulfilled(this.parent == null, "parent Diff already set");
        this.parent = parent;
    }
}
