package org.javers.common.collections;

import org.javers.common.validation.Validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.javers.common.collections.Arrays.asList;

/**
 * @author pawel szymczyk
 */
public class Collections {

    public static <E> Collection<E> difference(Collection<E> first, Collection<E> second) {
        if (first == null){
            return java.util.Collections.EMPTY_SET;
        }

        if (second == null){
            return first;
        }

        Collection<E> difference;

        if (first instanceof List) {
            difference = new ArrayList(first);
        } else if (first instanceof Set) {
            difference = new HashSet(first);
        } else {
            throw new IllegalArgumentException("At this moment Javers don't support Queues");
        }

        difference.removeAll(second);
        return difference;
    }

    public static Collection<Object> asCollection(Object arrayOrCollection) {
        Validate.argumentIsNotNull(arrayOrCollection);

        if (arrayOrCollection.getClass().isArray()) {
            return asList(arrayOrCollection);
        } else if (arrayOrCollection instanceof  Collection) {
            return (Collection<Object>) arrayOrCollection;
        } else {
            throw new IllegalArgumentException("expected Array or Collection, got "+arrayOrCollection.getClass());
        }
    }
}
