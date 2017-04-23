package org.javers.core;

import org.javers.common.collections.Lists;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.pico.InstantiatingModule;
import org.javers.repository.jql.QueryRunner;
import org.javers.repository.jql.ShadowQueryRunner;
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
        return Lists.<Class>asList(
                JaversCore.class,
                JsonConverterBuilder.class,
                JaversCoreConfiguration.class,
                GlobalIdFactory.class,
                QueryRunner.class,
                ShadowQueryRunner.class
        );
    }
}