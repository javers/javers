package org.javers.common.collections;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author pawel szymczyk
 */
public class Collections {
    public static <E> Collection<E> difference(Collection<E> first, Collection<E> second) {
        if (first instanceof List) {
            return Lists.difference((List) first,(List) second);
        } else if (first instanceof Set) {
            return Sets.difference((Set) first,(Set) second);
        } else {
            throw new IllegalArgumentException("At this moment Javers don't support "  + first.getClass().getSimpleName());
        }
    }

    public static Collection wrapNull(Object sourceCollection) {
        if (sourceCollection == null) {
            return java.util.Collections.emptySet();
        }
        else{
            return (Collection)sourceCollection;
        }
    }
}
