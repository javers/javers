package org.javers.common.pico;

import org.picocontainer.MutablePicoContainer;

public interface JaversModule {

    void addModuleComponentsTo(MutablePicoContainer container);

}
