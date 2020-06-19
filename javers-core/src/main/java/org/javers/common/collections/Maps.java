package org.javers.common.collections;

import org.javers.core.metamodel.property.MissingProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author bartosz walacik
 */
public class Maps {

    public static Map wrapNull(Object map){
        if (map == null || map == MissingProperty.INSTANCE){
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

    public static Map of(Object key, Object val) {
        Map m = new HashMap();
        m.put(key, val);
        return Collections.unmodifiableMap(m);
    }

    public static  <K,V> Map<K,V> merge(Map<K,V> a, Map<K,V> b) {
        if (a == null || a.isEmpty()) {
            return b;
        }

        if (b == null || b.isEmpty()) {
            return a;
        }

        Map m = new HashMap(b);
        m.putAll(a);

        return Collections.unmodifiableMap(m);
    }
}
