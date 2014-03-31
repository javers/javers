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
        }   else {
            initSet(key).add(value);
        }
    }

    public int size(){
        return map.size();
    }

    public Set<K> keySet(){
        return map.keySet();
    }

    public boolean isMultivalue(K key){
        if (isEmpty(key) ){
            return false;
        }
        Object valueOrSet = map.get(key);

        if (valueOrSet instanceof Set) {
            return ((Set)valueOrSet).size()>1;
        }
        return false;
    }

    /**
     * @throws java.lang.IllegalArgumentException
     */
    public V getOne(K key) {
         if (!isValue(key)) {
             throw new IllegalArgumentException("more than one or no values at key "+key);
         }
        return (V) map.get(key);
    }

    private Set<V> initSet(K key) {
        Set<V> values;
        if (isEmpty(key) ){
            values = new HashSet<>();
            map.put(key, values);
        }
        else if (isValue(key)){
            values = new HashSet<>();
            V existing = (V) map.get(key);
            values.add(existing);
            map.put(key, values);
        } else {
            values = (Set) map.get(key);
        }
        return values;
    }

    private boolean isEmpty(K key) {
        return !map.containsKey(key);
    }

    private boolean isValue(K key) {
        Object o = map.get(key);
        return (o!=null && !(o instanceof Set));
    }

}
