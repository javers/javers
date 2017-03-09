package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.common.reflection.JaversMember;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.CdoWrapper;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.scanner.ClassScanner;
import org.javers.core.metamodel.type.JaversProperty;
import org.javers.core.metamodel.type.TypeMapper;
import org.javers.core.metamodel.type.ValueObjectType;

/**
 * @author pawelszymczyk
 */
public class CollectionsCdoFactory {

    private final ClassScanner classScanner;
    private final TailoredJaversMemberFactory memberGenericTypeFactory;
    private final TypeMapper typeMapper;

    public CollectionsCdoFactory(ClassScanner classScanner, TailoredJaversMemberFactory memberGenericTypeFactory, TypeMapper typeMapper) {
        this.classScanner = classScanner;
        this.memberGenericTypeFactory = memberGenericTypeFactory;
        this.typeMapper = typeMapper;
    }

    Cdo createCdo(final CollectionWrapper wrapper, final Class<?> clazz) {
        Property primaryProperty = classScanner.scan(wrapper.getClass()).getProperties().get(0);
        JaversMember javersMember = memberGenericTypeFactory.create(primaryProperty, clazz);

        Property fixedProperty = new Property(javersMember, false);
        JaversProperty fixedJProperty = new JaversProperty(() -> typeMapper.getPropertyType(fixedProperty), fixedProperty);

        ValueObjectType valueObject = new ValueObjectType(wrapper.getClass(), Lists.asList(fixedJProperty));
        return new CdoWrapper(wrapper, new UnboundedValueObjectId(valueObject.getName()), valueObject);
    }
}
