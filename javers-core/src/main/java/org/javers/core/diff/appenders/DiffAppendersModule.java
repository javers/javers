package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class DiffAppendersModule extends InstantiatingModule {

    public DiffAppendersModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection)Lists.asList(
                NewObjectAppender.class,
                MapChangeAppender.class,
                ListChangeAppender.class,
                SetChangeAppender.class,
                ArrayChangeAppender.class,
                ObjectRemovedAppender.class,
                ReferenceChangeAppender.class,
                ValueChangeAppender.class
        );
    }
}
