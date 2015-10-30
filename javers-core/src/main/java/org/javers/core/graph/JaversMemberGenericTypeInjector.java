package org.javers.core.graph;

import org.javers.common.collections.Function;
import org.javers.common.collections.Lists;
import org.javers.common.reflection.JaversField;
import org.javers.common.reflection.JaversMember;
import org.javers.common.reflection.JaversMethod;
import org.javers.core.MappingStyle;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.ParametrizedDehydratedType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author pawelszymczyk
 */
class JaversMemberGenericTypeInjector {

    static class ChangeMetadata {
        private Property primaryProperty;
        private Class<?> genericItemClass;

        public ChangeMetadata(Property primaryProperty, Class<?> genericItemClass) {
            this.primaryProperty = primaryProperty;
            this.genericItemClass = genericItemClass;
        }
    }

    private final Map<MappingStyle, Function<ChangeMetadata, ? extends JaversMember>> injectors = new HashMap<>();

    public JaversMemberGenericTypeInjector() {
        injectors.put(MappingStyle.FIELD, new FieldInjector());
        injectors.put(MappingStyle.BEAN, new MethodInjector());
    }

    public JaversMember injectGenericType(ChangeMetadata changeMetadata, MappingStyle mappingStyle) {
        return injectors.get(mappingStyle).apply(changeMetadata);
    }

    private class FieldInjector implements Function<ChangeMetadata, JaversField> {
        @Override
        public JaversField apply(final ChangeMetadata m) {
            return new JaversField((Field) m.primaryProperty.getMember().getRawMember(), null) {
                @Override
                public Type getGenericType() {
                    return parametrizedType(m);
                }

                @Override
                protected Type getRawGenericType() {
                    return m.genericItemClass;
                }
            };
        }
    }

    private class MethodInjector implements Function<ChangeMetadata, JaversMethod> {
        @Override
        public JaversMethod apply(final ChangeMetadata m) {
            return new JaversMethod((Method) m.primaryProperty.getMember().getRawMember(), null) {
                @Override
                public Type getGenericType() {
                    return parametrizedType(m);
                }

                @Override
                protected Type getRawGenericType() {
                    return m.genericItemClass;
                }
            };
        }
    }

    private Type parametrizedType(ChangeMetadata m) {
        return new ParametrizedDehydratedType(m.primaryProperty.getType(), Lists.asList((Type) m.genericItemClass));
    }
}