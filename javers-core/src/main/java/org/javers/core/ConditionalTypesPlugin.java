package org.javers.core;

import org.javers.core.diff.appenders.PropertyChangeAppender;
import org.javers.core.metamodel.type.JaversType;

import java.util.Collection;
import java.util.Collections;

/**
 * @author bartosz.walacik
 */
public abstract class ConditionalTypesPlugin {

    public Collection<Class<? extends PropertyChangeAppender<?>>> getPropertyChangeAppenders() {
        return Collections.emptyList();
    }

    public Collection<JaversType> getNewTypes() {
        return Collections.emptyList();
    }

    public void beforeAssemble(JaversBuilder javersBuilder) {}
}
