package org.javers.core.pico;

import java.util.Arrays;
import java.util.Collection;

import org.javers.common.pico.JaversModule;
import org.javers.core.Javers;
import org.javers.core.configuration.JaversCoreConfiguration;
import org.javers.core.diff.DFSGraphToSetConverter;
import org.javers.core.diff.DiffFactory;
import org.javers.core.diff.appenders.NewObjectAppender;
import org.javers.core.diff.appenders.ObjectRemovedAppender;
import org.javers.core.diff.appenders.ReferenceChangeAppender;
import org.javers.core.diff.appenders.ValueChangeAppender;
import org.javers.core.json.JsonConverterBuilder;
import org.javers.model.mapping.type.TypeMapper;
import org.javers.model.object.graph.ObjectGraphBuilder;

/**
 * @author Piotr Betkier
 */
public class CoreJaversModule implements JaversModule {

    private static Class[] moduleComponents = new Class[]{
            Javers.class,
            DiffFactory.class,
            ObjectGraphBuilder.class,
            DFSGraphToSetConverter.class,
            NewObjectAppender.class,
            ObjectRemovedAppender.class,
            ReferenceChangeAppender.class,
            JsonConverterBuilder.class,
            ValueChangeAppender.class,
            TypeMapper.class,
            JaversCoreConfiguration.class};

    @Override
    public Collection<Class> getModuleComponents() {
        return Arrays.asList(moduleComponents);
    }

}
