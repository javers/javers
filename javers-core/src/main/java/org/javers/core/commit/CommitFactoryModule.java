package org.javers.core.commit;

import org.javers.common.collections.Lists;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class CommitFactoryModule extends InstantiatingModule {
    public CommitFactoryModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection)Lists.asList(
                CommitFactory.class,
                CommitSeqGenerator.class,
                CommitIdFactory.class,
                DistributedCommitSeqGenerator.class
        );
    }
}
