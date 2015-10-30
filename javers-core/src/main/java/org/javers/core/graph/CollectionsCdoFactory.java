package org.javers.core.graph;

import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.common.reflection.JaversField;
import org.javers.common.reflection.JaversMember;
import org.javers.common.reflection.JaversMethod;
import org.javers.core.MappingStyle;
import org.javers.core.graph.wrappers.SpecifiedClassCollectionWrapper;
import org.javers.core.metamodel.object.Cdo;
import org.javers.core.metamodel.object.CdoWrapper;
import org.javers.core.metamodel.object.UnboundedValueObjectId;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.property.PropertyScanner;
import org.javers.core.metamodel.type.ParametrizedDehydratedType;
import org.javers.core.metamodel.type.ValueObject;
import org.javers.core.metamodel.type.ValueObjectType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectionsCdoFactory {

    private Map<MappingStyle, Function<GenericPropertyChangeMetadata, ? extends JaversMember>> mergeFunctions = new HashMap<>();

    private final PropertyScanner propertyScanner;

    public CollectionsCdoFactory(PropertyScanner propertyScanner) {
        this.propertyScanner = propertyScanner;
        mergeFunctions.put(MappingStyle.FIELD, new JaversMemberHandCreator());
        mergeFunctions.put(MappingStyle.BEAN, new JaversMemberHandCreatorMethod());
    }

    public Cdo createCdo(SpecifiedClassCollectionWrapper wrapper, final Class<?> clazz, MappingStyle mappingStyle) {
        Property primaryProperty = propertyScanner.scanSingleProperty(wrapper.getClass(), wrapper.getWrappedCollectionFieldName());

        JaversMember javersMember = mergeFunctions.get(mappingStyle).apply(new GenericPropertyChangeMetadata(primaryProperty, clazz));
        Property fixedProperty = new Property(javersMember, false);

        ValueObjectType valueObject = new ValueObjectType(new ValueObject(wrapper.getClass(), Lists.asList(fixedProperty)));

        return new CdoWrapper(wrapper, new UnboundedValueObjectId(valueObject));
    }

    private class JaversMemberHandCreator implements Function<GenericPropertyChangeMetadata, JaversField> {

        @Override
        public JaversField apply(final GenericPropertyChangeMetadata input) {
            return new JaversField((Field) input.primaryProperty.getMember().getRawMember(), null) {
                @Override
                public Type getGenericType() {
                    return new ParametrizedDehydratedType(List.class, Lists.asList((Type) input.genericItemClass));
                }

                @Override
                protected Type getRawGenericType() {
                    return input.genericItemClass;
                }
            };
        }
    }

    private class JaversMemberHandCreatorMethod implements Function<GenericPropertyChangeMetadata, JaversMethod> {

        @Override
        public JaversMethod apply(final GenericPropertyChangeMetadata input) {
            return new JaversMethod((Method) input.primaryProperty.getMember().getRawMember(), null) {
                @Override
                public Type getGenericType() {
                    return new ParametrizedDehydratedType(List.class, Lists.asList((Type) input.genericItemClass));
                }

                @Override
                protected Type getRawGenericType() {
                    return input.genericItemClass;
                }
            };
        }
    }

    private class GenericPropertyChangeMetadata {
        private Property primaryProperty;
        private Class<?> genericItemClass;

        public GenericPropertyChangeMetadata(Property primaryProperty, Class<?> genericItemClass) {
            this.primaryProperty = primaryProperty;
            this.genericItemClass = genericItemClass;
        }
    }
}
