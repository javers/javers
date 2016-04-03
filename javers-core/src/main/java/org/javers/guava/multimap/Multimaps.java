package org.javers.guava.multimap;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

/**
 * @author akrystian
 */
public class Multimaps{
    /**
     * null args are allowed
     */
    public static <K, V> Multiset<K> commonKeys(Multimap<K, V> left, Multimap<K, V> right){
        if (left == null || right == null){
            return HashMultiset.create();
        }

        return Multisets.intersection(left.keys(), right.keys());
    }

    /**
     * null args are allowed
     */
    public static <K, V> Multiset<K> keysDifference(Multimap<K, V> left, Multimap<K, V> right){
        if (left == null){
            return HashMultiset.create();
        }

        if (right == null){
            return left.keys();
        }

        return Multisets.difference(left.keys(), right.keys());
    }
}

