package org.javers.common.collections;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class Maps {

    public static Map wrapNull(Object map){
        if (map == null){
            return Collections.emptyMap();
        }
        return (Map)map;
    }

    /**
     * null args are allowed
     */
    public static <K,V> Set<K> commonKeys(Map<K,V> left, Map<K,V> right) {
        if (left == null || right == null) {
            return Collections.emptySet();
        }

        return Sets.intersection(left.keySet(),right.keySet());
    }

    /**
     * null args are allowed
     */
    public static <K,V> Set<K> keysDifference(Map<K,V> left, Map<K,V> right) {
        if (left == null){
            return Collections.emptySet();
        }

        if (right == null){
            return left.keySet();
        }

        return Sets.difference(left.keySet(), right.keySet());
    }
}
