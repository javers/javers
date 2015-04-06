package org.javers.repository.jql;

import org.javers.common.validation.Validate;

/**
 * Created by bartosz.walacik on 2015-04-06.
 */
class VoOwnerFilter extends Filter {

    private final Class ownerEntityClass;
    private final String path;

    VoOwnerFilter(Class ownerEntityClass, String path) {
        Validate.argumentsAreNotNull();
        this.ownerEntityClass = ownerEntityClass;
        this.path = path;
    }

    public Class getOwnerEntityClass() {
        return ownerEntityClass;
    }

    public String getPath() {
        return path;
    }
}
