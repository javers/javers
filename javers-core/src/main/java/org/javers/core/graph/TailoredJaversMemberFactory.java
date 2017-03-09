package org.javers.core.graph;

import org.javers.common.collections.Lists;
import org.javers.common.reflection.JaversMember;
import org.javers.core.metamodel.property.Property;
import org.javers.core.metamodel.type.ParametrizedDehydratedType;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author pawelszymczyk
 */
abstract class TailoredJaversMemberFactory {

     abstract JaversMember create(Property primaryProperty, Class<?> genericItemClass);

     ParameterizedType parametrizedType(Property property, Class<?> itemClass) {
          return new ParametrizedDehydratedType(property.getRawType(), Lists.asList((Type) itemClass));
     }
}