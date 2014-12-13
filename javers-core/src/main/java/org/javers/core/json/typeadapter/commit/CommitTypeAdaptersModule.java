package org.javers.core.json.typeadapter.commit;

import org.javers.core.metamodel.object.GlobalIdFactory;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

/**
 * @author bartosz walacik
 */
public class CommitTypeAdaptersModule extends InstantiatingModule {


    public CommitTypeAdaptersModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    public void instantiateAndBindComponents() {
        addComponent(new CdoSnapshotTypeAdapter(typeMapper()));
        addComponent(new CommitIdTypeAdapter());
        addComponent(new GlobalIdTypeAdapter(globalIdFactory(), typeMapper()));
        addComponent(new InstanceIdDTOTypeAdapter(globalIdFactory()));

    }

    private TypeMapper typeMapper(){
        return getComponent(TypeMapper.class);
    }

    private GlobalIdFactory globalIdFactory(){
        return getComponent(GlobalIdFactory.class);
    }
}
