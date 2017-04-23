package org.javers.repository.jql;

import org.javers.core.metamodel.object.GlobalId;

class AnyDomainObjectFilter extends Filter {

    @Override
    boolean matches(GlobalId globalId) {
        return true;
    }
}
