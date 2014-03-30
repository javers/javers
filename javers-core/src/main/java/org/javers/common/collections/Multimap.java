package org.javers.common.collections;

import org.javers.common.validation.Validate;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * JaVers is designed as lightweight library so
 * not uses great Multimap from Guava.
 *
 * @author bartosz walacik
 */
public class Multimap<K,V> {
    private final Map<K, Object> map = new HashMap<>();

    public void put(K key, V value){
        Validate.argumentsAreNotNull(key, value);

        if (isEmpty(key)){
            map.put(key,value);
        }

        Set<V> values = initSet(key);
        values.add(value);
    }

    public Set<K> keySet(){
        return map.keySet();
    }

    public boolean isMultivalue(K key){
        if ( !isEmpty(key) ){
            return false;
        }
        Object valueOrSet = map.get(key);

        if (valueOrSet instanceof Set) {
            return ((Set)valueOrSet).size()>1;
        }
        return false;
    }

    private Set<V> initSet(K key) {
        Object valueOrSet = map.get(key);

        if (valueOrSet instanceof Set) {
            return (Set)valueOrSet;
        }

        Set<V> values = new HashSet<>();
        V existing = (V) valueOrSet;
        values.add(existing);

        return values;
    }


    private boolean isEmpty(K key) {
        return !map.containsKey(key);
    }

}
