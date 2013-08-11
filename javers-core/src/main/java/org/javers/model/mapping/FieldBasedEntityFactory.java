package org.javers.model.mapping;

import org.javers.model.mapping.type.TypeMapper;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class FieldBasedEntityFactory extends EntityFactory {

    public FieldBasedEntityFactory(TypeMapper typeMapper) {
        super(typeMapper);
    }

    @Override
    public <S> Entity<S> create(Class<S> beanClass) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
