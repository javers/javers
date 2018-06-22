package org.javers.repository.jql;

import org.javers.common.collections.Lists;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

public class JqlModule extends InstantiatingModule {
    public JqlModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return Lists.<Class>asList(
                QueryRunner.class,
                ShadowQueryRunner.class,
                ShadowStreamQueryRunner.class,
                ChangesQueryRunner.class,
                SnapshotQueryRunner.class,
                QueryCompiler.class
        );
    }

}
