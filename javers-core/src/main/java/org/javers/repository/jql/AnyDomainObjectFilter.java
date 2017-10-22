package org.javers.repository.jql;

import org.javers.common.string.ToStringBuilder;
import org.javers.core.metamodel.object.GlobalId;

class AnyDomainObjectFilter extends Filter {

    @Override
    boolean matches(GlobalId globalId) {
        return true;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this);
    }
}
