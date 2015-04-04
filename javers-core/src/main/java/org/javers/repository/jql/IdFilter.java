package org.javers.repository.jql;

import org.javers.common.validation.Validate;

/**
 * Created by bartosz.walacik on 2015-03-28.
 */
class IdFilter extends Filter {
    private final GlobalIdDTO globalId;

    IdFilter(GlobalIdDTO globalId) {
        Validate.argumentIsNotNull(globalId);
        this.globalId = globalId;
    }

    GlobalIdDTO getGlobalId() {
        return globalId;
    }

    @Override
    public String toString() {
        return "globalId=" + globalId.value();
    }
}
