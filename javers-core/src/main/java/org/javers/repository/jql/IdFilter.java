package org.javers.repository.jql;

import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;

/**
 * @author bartosz.walacik
 */
class IdFilter extends Filter {
    private final GlobalId globalId;

    IdFilter(GlobalId globalId) {
        Validate.argumentIsNotNull(globalId);
        this.globalId = globalId;
    }

    GlobalId getGlobalId() {
        return globalId;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this, "globalId", globalId);
    }

    boolean isInstanceIdFilter() {
        return globalId instanceof InstanceId;
    }

    @Override
    boolean matches(GlobalId targetId) {
        return globalId.equals(targetId);
    }
}
