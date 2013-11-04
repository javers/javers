package org.javers.core;

import org.javers.common.validation.Validate;
import org.javers.core.pico.JaversContainerFactory;
import org.picocontainer.PicoContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates JaVers instance based on your domain model metadata and custom configuration.
 *
 * @author bartosz walacik
 */
public class JaversBuilder {

    private static final MappingStyle DEFAULT_MAPPING_STYLE = MappingStyle.BEAN;

    private MappingStyle mappingStyle;

    private List<Class> managedClasses;

    public JaversBuilder() {
        mappingStyle = DEFAULT_MAPPING_STYLE;
        managedClasses = new ArrayList<>();
    }

    public Javers build() {
        PicoContainer container = JaversContainerFactory.create(mappingStyle);
        Javers javers = container.getComponent(Javers.class);

        for (Class<?> managedClass : managedClasses) {
            javers.manage(managedClass);
        }

        return javers;
    }

    public JaversBuilder manageClasses(Class<?>... managedClasses) {
        for (Class<?> managedClass : managedClasses) {
            this.managedClasses.add(managedClass);
        }
        return this;
    }

    public JaversBuilder withMappingStyle(MappingStyle mappingStyle) {
        Validate.argumentIsNotNull(mappingStyle);

        this.mappingStyle = mappingStyle;
        return this;
    }
}
