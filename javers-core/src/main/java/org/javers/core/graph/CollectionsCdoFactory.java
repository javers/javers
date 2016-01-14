package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.common.reflection.JaversMember;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.CdoWrapper;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.PropertyScanner;
import org.javers.core.metamodel.type.ValueObjectType;

/**
 * @author pawelszymczyk
 */
public class CollectionsCdoFactory {

    private final PropertyScanner propertyScanner;
    private final TailoredJaversMemberFactory memberGenericTypeInjector;

    public CollectionsCdoFactory(PropertyScanner propertyScanner, TailoredJaversMemberFactory memberGenericTypeInjector) {
        this.propertyScanner = propertyScanner;
        this.memberGenericTypeInjector = memberGenericTypeInjector;
    }

    public Cdo createCdo(final CollectionWrapper wrapper, final Class<?> clazz) {
        Property primaryProperty = propertyScanner.scan(wrapper.getClass()).get(0);
        JaversMember javersMember = memberGenericTypeInjector.create(primaryProperty, clazz);
        Property fixedProperty = new Property(javersMember, false);
        ValueObjectType valueObject = new ValueObjectType(wrapper.getClass(), Lists.asList(fixedProperty));
        return new CdoWrapper(wrapper, new UnboundedValueObjectId(valueObject.getName()), valueObject);
    }
}
