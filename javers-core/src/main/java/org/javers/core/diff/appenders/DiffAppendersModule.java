package org.javers.core.diff.appenders;

import org.javers.common.collections.Lists;
import org.javers.core.JaversCoreConfiguration;
import org.javers.core.diff.changetype.container.ListChange;
import org.javers.core.pico.LateInstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class DiffAppendersModule extends LateInstantiatingModule {

    private final Class<? extends PropertyChangeAppender<ListChange>> listChangeAppender;

    public DiffAppendersModule(JaversCoreConfiguration javersCoreConfiguration, MutablePicoContainer container) {
        super(javersCoreConfiguration, container);
        this.listChangeAppender = javersCoreConfiguration.getListCompareAlgorithm().getAppenderClass();
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection)Lists.asList(
                NewObjectAppender.class,
                MapChangeAppender.class,
                CollectionAsListChangeAppender.class,
                listChangeAppender,
                SetChangeAppender.class,
                ArrayChangeAppender.class,
                ObjectRemovedAppender.class,
                ReferenceChangeAppender.class,
                OptionalChangeAppender.class,
                ValueChangeAppender.class
        );
    }
}
