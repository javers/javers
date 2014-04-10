package org.javers.core.diff;

import org.javers.common.collections.Optional;
import org.javers.common.exception.exceptions.JaversException;
import org.javers.common.exception.exceptions.JaversExceptionCode;
import org.javers.core.metamodel.object.GlobalCdoId;
import org.javers.core.diff.changetype.ValueChange;
import org.javers.common.patterns.visitors.Visitable;

import static org.javers.common.validation.Validate.argumentIsNotNull;
import static org.javers.common.validation.Validate.conditionFulfilled;

/**
 * Change represents <b>atomic</b> difference between two objects.
 * <br/><br/>
 *
 * There are several change types: {@link ValueChange}, {@link org.javers.core.diff.changetype.ReferenceChange}, ...
 * For complete list see inheritance hierarchy.
 * <br/><br/>
 *
 * Change is a <i>Value Object</i> and typically can not exists without
 * owning {@link org.javers.core.diff.Diff}. For more information see {@link org.javers.core.diff.Diff} javadoc.
 *
 * @author bartosz walacik
 */
public abstract class Change implements Visitable<ChangeVisitor> {
    //private Diff parent;

    private final GlobalCdoId globalCdoId;

    private transient Optional<Object> affectedCdo;

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
     * Affected Cdo, depending on concrete Change type,
     * it could be new Object, removed Object or new version of changed Object
     * <br/>
     *
     * <b>Transient</b> reference - available only for freshly generated diff
     *
     * @throws JaversException AFFECTED_CDO_IS_NOT_AVAILABLE
     */
    public Object getAffectedCdo() {
        if (affectedCdo == null || affectedCdo.isEmpty()){
            throw new JaversException(JaversExceptionCode.AFFECTED_CDO_IS_NOT_AVAILABLE);
        }
        return affectedCdo.get();
    }

    /**
     * Owning aggregate
     *
    public Diff getParent() {
        return parent;
    }*/

    protected void setAffectedCdo(Optional<Object> affectedCdo) {
        argumentIsNotNull(affectedCdo);
        conditionFulfilled(this.affectedCdo == null, "affectedCdo already set");
        this.affectedCdo = affectedCdo;
    }

    //protected void bind(Diff parent) {
    //    conditionFulfilled(this.parent == null, "parent Diff already set");
    //    this.parent = parent;
    //}

    @Override
    public void accept(ChangeVisitor changeVisitor) {
        changeVisitor.visit(this);
    }
}
