package org.javers.repository.jql;

/**
 * Created by bartosz.walacik on 2015-03-28.
 */
class IdFilter extends Filter {
    private final GlobalIdDTO globalId;

    IdFilter(GlobalIdDTO globalId) {
        this.globalId = globalId;
    }

    GlobalIdDTO getGlobalId() {
        return globalId;
    }
}
