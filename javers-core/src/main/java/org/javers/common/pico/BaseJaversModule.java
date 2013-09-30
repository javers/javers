package org.javers.common.pico;

import org.picocontainer.MutablePicoContainer;

public abstract class BaseJaversModule implements JaversModule {

    @Override
    public void addModuleComponentsTo(MutablePicoContainer container) {
        for (Class component : getSimpleModuleComponents()) {
            container.addComponent(component);
        }

        addComplexModuleComponentsTo(container);
    }

    protected Class[] getSimpleModuleComponents() {
        return new Class[] {};
    }

    protected void addComplexModuleComponentsTo(MutablePicoContainer container) {
        // nothing
    }
}
