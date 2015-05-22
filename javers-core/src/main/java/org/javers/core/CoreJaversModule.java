package org.javers.core;

import org.javers.common.collections.Lists;
import org.javers.common.date.DefaultDateProvider;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.object.SnapshotFactory;
import org.javers.core.metamodel.type.TypeFactory;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.pico.InstantiatingModule;
import org.javers.repository.jql.QueryRunner;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class CoreJaversModule extends InstantiatingModule {
    public CoreJaversModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                JaversCore.class,
                JsonConverterBuilder.class,
                TypeMapper.class,
                TypeFactory.class,
                JaversCoreConfiguration.class,
                SnapshotFactory.class,
                GlobalIdFactory.class,
                DefaultDateProvider.class,
                QueryRunner.class
        );
    }
}