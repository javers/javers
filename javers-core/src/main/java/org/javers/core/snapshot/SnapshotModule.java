package org.javers.core.snapshot;

import org.javers.common.collections.Lists;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class SnapshotModule extends InstantiatingModule {
    public SnapshotModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                SnapshotFactory.class,
                ObjectHasher.class,
                SnapshotDiffer.class,
                SnapshotGraphFactory.class,
                ChangedCdoSnapshotsFactory.class
        );
    }
}
