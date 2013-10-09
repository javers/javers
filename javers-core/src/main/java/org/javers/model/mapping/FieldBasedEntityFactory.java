package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

import java.util.List;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class FieldBasedEntityFactory extends EntityFactory {

    public FieldBasedEntityFactory(TypeMapper typeMapper) {
        super(typeMapper, FieldBasedScanner.getInstane(typeMapper));
    }

    @Override
    public <S> Entity<S> create(Class<S> entityClass) {
        typeMapper.registerReferenceType(entityClass);
        List<Property> beanProperties = scanner.scan(entityClass);
        return new Entity<S>(entityClass,beanProperties);
    }
}
