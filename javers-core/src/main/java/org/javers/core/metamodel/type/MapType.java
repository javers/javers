package org.javers.core.metamodel.type;

import org.javers.common.collections.EnumerableFunction;
import org.javers.common.exception.exceptions.JaversException;
import java.lang.reflect.Type;
import static org.javers.common.exception.exceptions.JaversExceptionCode.NOT_IMPLEMENTED;

/**
 * Map where both keys and values
 * should be of {@link PrimitiveType} or {@link ValueType}.
 * <p/>
 *
 * Javers doesn't support complex maps with ValueObjects or Entities
 *
 * @author bartosz walacik
 */
public class MapType extends EnumerableType {
    private EntryClass entryClass;

    public MapType(Type baseJavaType) {
        super(baseJavaType);

        if (getActualClassTypeArguments().size() == 2) {
            entryClass = new EntryClass(getActualClassTypeArguments().get(0), getActualClassTypeArguments().get(1));
        }
    }

    /**
     * not null only if both Key and Value type arguments are actual Classes
     */
    public EntryClass getEntryClass() {
        return entryClass;
    }

    @Override
    public Object map(Object sourceEnumerable, EnumerableFunction mapFunction) {
        throw new JaversException(NOT_IMPLEMENTED);
    }

    @Override
    public Class getElementType() {
        throw new JaversException(NOT_IMPLEMENTED);
    }
}
