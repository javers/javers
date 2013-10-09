package org.javers.model.mapping;

import org.javers.model.mapping.type.JaversType;
import org.javers.model.mapping.type.TypeMapper;

import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Pawel Cierpiatka <pawel.cierpiatka@gmail.com>
 */
public class FieldBasedEntityFactory extends EntityFactory {

    public FieldBasedEntityFactory(TypeMapper typeMapper) {
        super(typeMapper, new FieldBasedManagedClassPropertyScanner(typeMapper));
    }

    @Override
    public <S> Entity<S> create(Class<S> entityClass) {
        typeMapper.registerReferenceType(entityClass);
        List<Property> beanProperties = scanner.scan(entityClass);
        return new Entity<S>(entityClass,beanProperties);
    }
}
