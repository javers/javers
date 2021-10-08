package org.javers.core;

import org.javers.core.diff.appenders.PropertyChangeAppender;
import org.javers.core.metamodel.type.JaversType;
import org.javers.core.metamodel.type.TypeMapperLazy;

import java.util.Collection;
import java.util.Collections;

/**
 * @author bartosz.walacik
 */
public abstract class ConditionalTypesPlugin implements JaversBuilderPlugin {

    public Collection<Class<? extends PropertyChangeAppender<?>>> getPropertyChangeAppenders() {
        return Collections.emptyList();
    }

    public Collection<JaversType> getNewTypes(TypeMapperLazy typeMapperLazy) {
        return Collections.emptyList();
    }
}
