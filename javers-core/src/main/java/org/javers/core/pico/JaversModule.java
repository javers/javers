package org.javers.core.pico;

import java.util.Collection;

/**
 * @author Piotr Betkier
 */
public interface JaversModule {

    public Collection<Class> getComponents();

}
