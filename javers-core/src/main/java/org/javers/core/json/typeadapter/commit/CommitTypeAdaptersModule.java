package org.javers.core.json.typeadapter.commit;

import org.javers.common.collections.Lists;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class CommitTypeAdaptersModule extends InstantiatingModule {
    public CommitTypeAdaptersModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection)Lists.asList(
                CdoSnapshotTypeAdapter.class,
                GlobalIdTypeAdapter.class,
                CommitIdTypeAdapter.class,
                JsonElementFakeAdapter.class,
                CdoSnapshotStateTypeAdapter.class,
                CommitMetadataTypeAdapter.class
        );
    }
}
