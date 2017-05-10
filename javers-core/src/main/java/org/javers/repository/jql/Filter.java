package org.javers.repository.jql;

import org.javers.core.metamodel.object.GlobalId;

/**
 * @author bartosz.walacik
 */
abstract class Filter {
    abstract boolean matches(GlobalId globalId);
}
