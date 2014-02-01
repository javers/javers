package org.javers.common.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
}
