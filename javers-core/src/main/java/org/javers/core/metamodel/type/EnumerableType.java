package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.IdentityEnumerableFunction;

import java.lang.reflect.Type;

/**
 * Collection or Array or Map
 * @author bartosz walacik
 */
public abstract class EnumerableType extends JaversType {
    public EnumerableType(Type baseJavaType) {
        super(baseJavaType);
    }

    public abstract Class getElementType();

    /**
     * returns shallow copy of sourceEnumerable
     */
    public Object copy(Object sourceEnumerable){
        return map(sourceEnumerable, new IdentityEnumerableFunction());
    }

    /**
     * Returns new instance of Enumerable with items from sourceEnumerable mapped by mapFunction.
     */
    public abstract Object map(Object sourceEnumerable, EnumerableFunction mapFunction);
}
