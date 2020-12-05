package org.javers.repository.jql;

import org.javers.common.string.ToStringBuilder;
import org.javers.common.validation.Validate;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;

import java.util.Collections;
import java.util.Set;

/**
 * @author bartosz.walacik
 */
class IdFilter extends Filter {
    private final Set<GlobalId> globalIds;

    IdFilter(GlobalId globalIds) {
        Validate.argumentIsNotNull(globalIds);
        this.globalIds = Collections.singleton(globalIds);
    }

    IdFilter(Set<GlobalId> globalIds) {
        globalIds.forEach(Validate::argumentIsNotNull);
        this.globalIds = globalIds;
    }

    Set<GlobalId> getGlobalIds() {
        return globalIds;
    }

    @Override
    public String toString() {
        return ToStringBuilder.toString(this, "globalIds", globalIds);
    }

    boolean isInstanceIdFilter() {
        return globalIds.stream()
                .allMatch(it -> it instanceof InstanceId);
    }

    @Override
    boolean matches(GlobalId targetId) {
        return globalIds.contains(targetId);
    }
}
