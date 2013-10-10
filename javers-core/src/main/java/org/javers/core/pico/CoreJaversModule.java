package org.javers.core.pico;

import org.javers.common.pico.JaversModule;
import org.javers.core.Javers;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Piotr Betkier
 */
public class CoreJaversModule implements JaversModule {

    private static Class[] moduleComponents = new Class[] {Javers.class};

    @Override
    public Collection<Class> getModuleComponents() {
        return Arrays.asList(moduleComponents);
    }

}
