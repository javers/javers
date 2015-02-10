package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Collection or Array or Map
 * @author bartosz walacik
 */
public abstract class EnumerableType extends JaversType {

    public EnumerableType(Type baseJavaType) {
        super(baseJavaType);
    }

    /**
     * returns true if Enumerable is generic Type and all its arguments are actual Classes
     */
    public abstract boolean isFullyParametrized();

    /**
     * Returns new instance of Enumerable with items from sourceEnumerable mapped by mapFunction.
     */
    public abstract Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner);

    public abstract boolean isEmpty(Object container);
}
