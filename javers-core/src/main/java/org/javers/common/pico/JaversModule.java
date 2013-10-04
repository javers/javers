package org.javers.common.pico;

import java.util.Collection;

/**
 * @author Piotr Betkier
 */
public interface JaversModule {

    public Collection<Class> getModuleComponents();

}
