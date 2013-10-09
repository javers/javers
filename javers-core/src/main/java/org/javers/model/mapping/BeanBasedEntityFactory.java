package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author bartosz walacik
 */
public class BeanBasedEntityFactory extends EntityFactory {
    private static final Logger logger = LoggerFactory.getLogger(BeanBasedEntityFactory.class);

    public BeanBasedEntityFactory(TypeMapper typeMapper) {
        super(typeMapper, new BeanBasedManagedClassPropertyScanner(typeMapper));
    }


    @Override
    public <S> Entity<S> create(Class<S> entityClass) {
        typeMapper.registerReferenceType(entityClass);
        List<Property> beanProperties = scanner.scan(entityClass);
        return new Entity<S>(entityClass,beanProperties);
    }
}
