package org.javers.core.metamodel.clazz;

import org.javers.common.validation.Validate;
import org.javers.core.diff.custom.CustomPropertyComparator;
import org.javers.core.metamodel.type.CustomType;

/**
 *  Recipe for {@link CustomType}
 *
 * @author bartosz walacik
 */
public class CustomDefinition<T> extends ClientsClassDefinition {
    private CustomPropertyComparator<T, ?> comparator;

    public CustomDefinition(Class<T> clazz, CustomPropertyComparator<T, ?> comparator) {
        super(clazz);
        Validate.argumentIsNotNull(comparator);
        this.comparator = comparator;
    }

    public CustomPropertyComparator<T, ?> getComparator() {
        return comparator;
    }
}
