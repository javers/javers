package org.javers.core.diff.custom;

import org.javers.core.diff.changetype.container.CollectionChange;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.property.Property;

/**
 * Useful for classes that are logically collections but do not implement a java.util.Collection interface.
 * <br/><br/>
 *
 * Implementation should calculate diff between two collections of type T
 * and return the result as {@link CollectionChange}.
 *
 * @author bartosz walacik
 */
public interface CustomCollectionComparator<T> extends CustomPropertyComparator<T, CollectionChange>{

    @Override
    CollectionChange compare(T left, T right, GlobalId affectedId, Property property);
}
