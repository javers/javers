package org.javers.core.diff.appenders;

import org.javers.core.diff.changetype.PropertyChange;

/**
 * @author bartosz walacik
 */
public abstract class CorePropertyChangeAppender<T extends PropertyChange> implements PropertyChangeAppender<T> {
    @Override
    public int priority() {
        return LOW_PRIORITY;
    }
}
