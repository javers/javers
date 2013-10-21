package org.javers.core.pico;

import java.util.Arrays;
import java.util.Collection;

import org.javers.common.pico.JaversModule;
import org.javers.core.Javers;
import org.javers.core.diff.DFSGraphToSetConverter;
import org.javers.core.diff.DiffFactory;
import org.javers.core.diff.appenders.NewObjectAppender;
import org.javers.core.diff.appenders.ObjectRemovedAppender;

/**
 * @author Piotr Betkier
 */
public class CoreJaversModule implements JaversModule {

    private static Class[] moduleComponents = new Class[]{Javers.class, DiffFactory.class,
            DFSGraphToSetConverter.class, NewObjectAppender.class, ObjectRemovedAppender.class};

    @Override
    public Collection<Class> getModuleComponents() {
        return Arrays.asList(moduleComponents);
    }

}
