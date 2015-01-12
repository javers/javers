package org.javers.core.snapshot;

import org.javers.common.collections.Lists;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class GraphSnapshotModule extends InstantiatingModule {
    public GraphSnapshotModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                GraphSnapshotFacade.class,
                GraphSnapshotFactory.class,
                SnapshotDiffer.class,
                GraphShadowFactory.class
        );
    }
}
