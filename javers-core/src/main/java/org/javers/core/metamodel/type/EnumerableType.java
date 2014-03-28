package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.collections.IdentityEnumerableFunction;

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
     * Collection/Array/Map content type.
     * <p/>
     * When Enumerable is generic Type with actual Class arguments, returns this arguments.
     * For example, if baseJavaType = Set&lt;String&gt, returns List with String.class
     * <p/>
     *
     * For no generic types like Set, or generic types with unbounded type parameter like
     * Set&lt;V&gt;, Set&lt;?&gt; returns <b>empty List</b>.
     * <p/>
     *
     * For array, returns List with {@link Class#getComponentType()}
     * <p/>
     */
    public abstract List<Class> getElementTypes();

    /**
     * returns true if Enumerable is generic Type and all its arguments are actual Classes
     */
    public abstract boolean isFullyParameterized();

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
