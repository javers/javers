package org.javers.model.pico;

import org.javers.common.pico.BaseJaversModule;
import org.javers.core.MappingStyle;
import org.javers.model.mapping.BeanBasedEntityFactory;
import org.javers.model.mapping.EntityManager;
import org.javers.model.mapping.FieldBasedEntityFactory;
import org.javers.model.mapping.type.TypeMapper;
import org.picocontainer.MutablePicoContainer;

public class ModelJaversModule extends BaseJaversModule {

    private MappingStyle configuredStyle;

    public ModelJaversModule(MappingStyle configuredStyle) {
        this.configuredStyle = configuredStyle;
    }

    @Override
    protected Class[] getSimpleModuleComponents() {
        return new Class[] {EntityManager.class, BeanBasedEntityFactory.class, TypeMapper.class};
    }

    @Override
    protected void addComplexModuleComponentsTo(MutablePicoContainer container) {
        container.addConfig(EntityFactoryFactory.CONFIGURED_MAPPING_STYLE_KEY, configuredStyle);
        container.addComponent(EntityFactoryFactory.getEntityFactoryKeyName(MappingStyle.BEAN), BeanBasedEntityFactory.class);
        container.addComponent(EntityFactoryFactory.getEntityFactoryKeyName(MappingStyle.FIELD), FieldBasedEntityFactory.class);
        container.addAdapter(new EntityFactoryFactory());
    }
}
