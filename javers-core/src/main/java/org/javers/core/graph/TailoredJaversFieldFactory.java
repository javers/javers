package org.javers.core.graph;

import org.javers.common.reflection.JaversField;
import org.javers.core.metamodel.property.Property;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

/**
 * @author pawelszymczyk
 */
class TailoredJaversFieldFactory extends TailoredJaversMemberFactory {

    @Override
    public JaversField create(final Property primaryProperty, final Class<?> genericItemClass) {
        return new JaversField((Field) primaryProperty.getMember().getRawMember(), null) {
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
