package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.common.reflection.JaversMember;
import org.javers.core.MappingStyle;
import org.javers.core.graph.JaversMemberGenericTypeInjector.ChangeMetadata;
import org.javers.core.graph.wrappers.SpecifiedClassCollectionWrapper;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.CdoWrapper;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.PropertyScanner;
import org.javers.core.metamodel.type.ValueObject;
import org.javers.core.metamodel.type.ValueObjectType;

public class CollectionsCdoFactory {

    private final PropertyScanner propertyScanner;
    private final JaversMemberGenericTypeInjector memberGenericTypeInjector;

    public CollectionsCdoFactory(PropertyScanner propertyScanner, JaversMemberGenericTypeInjector memberGenericTypeInjector) {
        this.propertyScanner = propertyScanner;
        this.memberGenericTypeInjector = memberGenericTypeInjector;
    }

    public Cdo createCdo(SpecifiedClassCollectionWrapper wrapper, final Class<?> clazz, MappingStyle mappingStyle) {
        Property primaryProperty = propertyScanner.scanSingleProperty(wrapper.getClass(), wrapper.getWrappedCollectionPropertyName());
        JaversMember javersMember = memberGenericTypeInjector.injectGenericType(new ChangeMetadata(primaryProperty, clazz), mappingStyle);
        Property fixedProperty = new Property(javersMember, false);
        ValueObjectType valueObject = new ValueObjectType(new ValueObject(wrapper.getClass(), Lists.asList(fixedProperty)));
        return new CdoWrapper(wrapper, new UnboundedValueObjectId(valueObject));
    }
}
