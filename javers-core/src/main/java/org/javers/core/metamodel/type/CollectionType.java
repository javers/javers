package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.JaversException;
import org.javers.core.metamodel.object.OwnerContext;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.javers.common.collections.Lists.immutableListOf;
import static org.javers.common.exception.JaversExceptionCode.NOT_IMPLEMENTED;

/**
 * @author bartosz walacik
 */
public class CollectionType extends ContainerType {
    private transient List<Class> elementTypes;

    public CollectionType(Type baseJavaType) {
        super(baseJavaType);
        if (getActualClassTypeArguments().size() == 1) {
            elementTypes = immutableListOf(getActualClassTypeArguments().get(0));
        }
        else {
            elementTypes = Collections.EMPTY_LIST;
        }
    }

    @Override
    public boolean isFullyParametrized() {
        return elementTypes.size() == 1;
    }

    @Override
    public List<Class> getElementTypes() {
        return elementTypes;
    }

    /**
     * implemented in subclasses
     */
    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction, OwnerContext owner) {
        throw new JaversException(NOT_IMPLEMENTED);
    }

    @Override
    public boolean isEmpty(Object collection) {
        return collection == null || ((Collection)collection).isEmpty();
    }
}
