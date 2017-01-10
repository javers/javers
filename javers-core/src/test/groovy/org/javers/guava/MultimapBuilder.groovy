package org.javers.guava
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap

/**
 * @author akrystian
 */
class MultimapBuilder {

    /**
     * Creates multimap from Map<K, List<V>>.
     */
     static <K, V> Multimap create(Map<K, List<V>> source) {
        def multimap = ArrayListMultimap.create()
        def set = source.keySet()
        set.forEach { k ->
            def vs = source[k]
            vs.forEach{v -> multimap.put(k,v)}
        }
        multimap
    }
}
