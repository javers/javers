package org.javers.core;

import org.javers.common.validation.Validate;
import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.EntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.FieldBasedEntityFactory;
import org.javers.model.mapping.type.TypeMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Creates JaVers instance based on your domain model metadata and custom configuration.
 *
 * @author bartosz walacik
 */
public class JaversFactory {
    private static final MappingStyle DEFAULT_MAPPING_STYLE = MappingStyle.BEAN;

    private Javers javers;
    private MappingStyle mappingStyle;
    private TypeMapper typeMapper;
    private List<Class> managedClasses;

    public JaversFactory() {
        typeMapper = new TypeMapper();
        mappingStyle = DEFAULT_MAPPING_STYLE;
        managedClasses = new ArrayList<>();
    }

    public Javers build() {
        EntityFactory entityFactory = null;

        if (mappingStyle == MappingStyle.BEAN) {
            entityFactory = new BeanBasedEntityFactory(typeMapper);
        }
        if (mappingStyle == MappingStyle.FIELD) {
            entityFactory = new FieldBasedEntityFactory(typeMapper);
        }

        EntityManager entityManager = new EntityManager(entityFactory);

        javers = new Javers(entityManager);
        for (Class<?> managedClass : managedClasses) {
            javers.manage(managedClass);
        }
        return javers;
    }

    public JaversFactory manageClasses(Class<?>... managedClasses) {
        for (Class<?> managedClass : managedClasses) {
            this.managedClasses.add(managedClass);
        }
        return this;
    }

    public void setMappingStyle(MappingStyle mappingStyle) {
        Validate.argumentIsNotNull(mappingStyle);

        this.mappingStyle = mappingStyle;
    }
}
