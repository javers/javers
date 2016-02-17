package org.javers.core.graph;

import org.javers.common.reflection.JaversMethod;
import org.javers.core.metamodel.property.Property;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author pawelszymczyk
 */
class TailoredJaversMethodFactory extends TailoredJaversMemberFactory {

    @Override
    public JaversMethod create(final Property primaryProperty, final Class<?> genericItemClass) {
        return new JaversMethod((Method) primaryProperty.getMember().getRawMember(), null) {
            @Override
            public Type getGenericResolvedType() {
                return parametrizedType(primaryProperty, genericItemClass);
            }

            @Override
            protected Type getRawGenericType() {
                return genericItemClass;
            }
        };
    }
}
