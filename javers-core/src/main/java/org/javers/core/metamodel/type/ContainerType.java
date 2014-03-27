package org.javers.core.metamodel.type;

import java.lang.reflect.Type;

/**
 * Collection or Array
 *
 * @author bartosz walacik
 */
public abstract class ContainerType extends EnumerableType {

    ContainerType(Type baseJavaType) {
        super(baseJavaType);
    }

    /**
     * Collection/Array content type.
     * <p/>
     * When Collection is generic Type with actual Class argument, returns this argument.
     * For example, if baseJavaType = Set&lt;String&gt, returns String.class
     * <p/>
     *
     * For no generic types like Set, or generic types with unbounded type parameter like
     * Set&lt;V&gt;, Set&lt;?&gt; returns <b>null</b>.
     * <p/>
     *
     * When Array, returns {@link Class#getComponentType()}
     * <p/>
     */
    public abstract Class getElementType();
}
