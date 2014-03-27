package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.exceptions.JaversException;
import java.lang.reflect.Type;
import static org.javers.common.exception.exceptions.JaversExceptionCode.NOT_IMPLEMENTED;

public class ListType extends CollectionType{

    public ListType(Type baseJavaType) {
        super(baseJavaType);
    }

    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction) {
        throw new JaversException(NOT_IMPLEMENTED);
    }
}
