package org.javers.core.json.typeadapter.change;

import org.javers.common.collections.Lists;
import org.javers.core.diff.changetype.ReferenceRemovedChange;
import org.javers.core.diff.changetype.ReferenceUpdatedChange;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class ChangeTypeAdaptersModule extends InstantiatingModule {

    public ChangeTypeAdaptersModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    protected Collection<Class> getImplementations() {
        return (Collection) Lists.asList(
                MapChangeTypeAdapter.class,
                ArrayChangeTypeAdapter.class,
                ListChangeTypeAdapter.class,
                SetChangeTypeAdapter.class,
                NewObjectTypeAdapter.class,
                ValueAddedTypeAdapter.class,
                ValueRemovedTypeAdapter.class,
                ValueUpdatedTypeAdapter.class,
                ObjectRemovedTypeAdapter.class,
                ChangeTypeAdapter.class,
                ReferenceUpdatedTypeAdapter.class,
            ReferenceAddedTypeAdapter.class,
            ReferenceRemovedTypeAdapter.class
        );
    }
}
