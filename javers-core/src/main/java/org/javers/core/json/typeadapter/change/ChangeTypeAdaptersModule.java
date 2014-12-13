package org.javers.core.json.typeadapter.change;

import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.pico.InstantiatingModule;
import org.picocontainer.MutablePicoContainer;

/**
 * @author bartosz walacik
 */
public class ChangeTypeAdaptersModule extends InstantiatingModule {

    public ChangeTypeAdaptersModule(MutablePicoContainer container) {
        super(container);
    }

    @Override
    public void instantiateAndBindComponents() {

        addComponent(new MapChangeTypeAdapter(typeMapper()));
        addComponent(new ArrayChangeTypeAdapter(typeMapper()));
        addComponent(new ListChangeTypeAdapter(typeMapper()));
        addComponent(new SetChangeTypeAdapter(typeMapper()));
        addComponent(new NewObjectTypeAdapter());
        addComponent(new ValueChangeTypeAdapter());
        addComponent(new ObjectRemovedTypeAdapter());
        addComponent(new ChangeTypeAdapter());
        addComponent(new ReferenceChangeTypeAdapter());
    }

    private TypeMapper typeMapper(){
        return getComponent(TypeMapper.class);
    }
}
