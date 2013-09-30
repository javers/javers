package org.javers.core.pico;

import org.javers.common.pico.BaseJaversModule;
import org.javers.core.Javers;

public class CoreJaversModule extends BaseJaversModule {

    @Override
    protected Class[] getSimpleModuleComponents() {
        return new Class[] {Javers.class};
    }

}
