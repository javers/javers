package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.Optional;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;

/**
 * Collection or Array or Map
 * @author bartosz walacik
 */
public abstract class EnumerableType extends JaversType {

    public EnumerableType(Type baseJavaType, int expectedArgs) {
        super(baseJavaType, Optional.<String>empty(), expectedArgs);
    }

    /**
     * Returns new instance of Enumerable with items from sourceEnumerable mapped by mapFunction.
     */
    public abstract Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner);

    public abstract boolean isEmpty(Object container);
}
