package org.javers.core.diff;

import org.javers.common.collections.Lists;
import org.javers.core.pico.JaversModule;

import java.util.Collection;

/**
 * @author bartosz walacik
 */
public class DiffFactoryModule implements JaversModule{

    @Override
    public Collection<Class> getComponents() {
        return (Collection) Lists.asList(
                DiffFactory.class
        );
    }
}
