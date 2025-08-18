package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.type.KeyValueType
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/1450
 *
 * These tests use {@link java.util.Map}, but they should apply to any types that
 * are handled by {@link org.javers.core.metamodel.type.KeyValueType},
 * since they all have type parameters {@code <K, V>}.
 *
 * @author Yat Ho
 */
class KeyValueTypeParameterResolutionTest extends Specification {
    class ExtendsMapSubtype extends HashMap<String, Integer> {}

    def "should recognise key and value types from Map subclass"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def jType = javers.<KeyValueType> getTypeMapping(ExtendsMapSubtype)
        def expectedJType = javers.<KeyValueType> getTypeMapping(ExtendsMapSubtype.getGenericSuperclass())

        then:
        jType.getKeyJavaType() == expectedJType.getKeyJavaType()
        jType.getValueJavaType() == expectedJType.getValueJavaType()
    }

    class ImplementsMap implements Map<String, Integer> {
        @Override
        int size() {
            return 0
        }

        @Override
        boolean isEmpty() {
            return true
        }

        @Override
        boolean containsKey(Object key) {
            return false
        }

        @Override
        boolean containsValue(Object value) {
            return false
        }

        @Override
        Integer get(Object key) {
            return null
        }

        @Override
        Integer put(String key, Integer value) {
            return null
        }

        @Override
        Integer remove(Object key) {
            return null
        }

        @Override
        void putAll(Map<? extends String, ? extends Integer> m) {}

        @Override
        void clear() {}

        @Override
        Set<String> keySet() {
            return null
        }

        @Override
        Collection<Integer> values() {
            return null
        }

        @Override
        Set<Entry<String, Integer>> entrySet() {
            return null
        }
    }

    def "should recognise key and value types from Map interface"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def jType = javers.<KeyValueType> getTypeMapping(ImplementsMap)
        def expectedJType = javers.<KeyValueType> getTypeMapping(ImplementsMap.getGenericInterfaces()[0])

        then:
        jType.getKeyJavaType() == expectedJType.getKeyJavaType()
        jType.getValueJavaType() == expectedJType.getValueJavaType()
    }

    interface ExtendsMap extends Map<String, Integer> {}

    class ImplementsExtendsMap implements ExtendsMap {
        @Override
        int size() {
            return 0
        }

        @Override
        boolean isEmpty() {
            return true
        }

        @Override
        boolean containsKey(Object key) {
            return false
        }

        @Override
        boolean containsValue(Object value) {
            return false
        }

        @Override
        Integer get(Object key) {
            return null
        }

        @Override
        Integer put(String key, Integer value) {
            return null
        }

        @Override
        Integer remove(Object key) {
            return null
        }

        @Override
        void putAll(Map<? extends String, ? extends Integer> m) {}

        @Override
        void clear() {}

        @Override
        Set<String> keySet() {
            return null
        }

        @Override
        Collection<Integer> values() {
            return null
        }

        @Override
        Set<Entry<String, Integer>> entrySet() {
            return null
        }
    }

    def "should recognise key and value types from Map subinterface"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def jType = javers.<KeyValueType> getTypeMapping(ImplementsExtendsMap)
        def expectedJType = javers.<KeyValueType> getTypeMapping(ImplementsExtendsMap.getInterfaces()[0].getGenericInterfaces()[0])

        then:
        jType.getKeyJavaType() == expectedJType.getKeyJavaType()
        jType.getValueJavaType() == expectedJType.getValueJavaType()
    }

    class EmptyClass {}

    class ExtendsEmptyClass extends EmptyClass {}

    class ExtendsImplementsMapInMiddle extends ExtendsEmptyClass implements Map<String, Integer> {
        @Override
        int size() {
            return 0
        }

        @Override
        boolean isEmpty() {
            return true
        }

        @Override
        boolean containsKey(Object key) {
            return false
        }

        @Override
        boolean containsValue(Object value) {
            return false
        }

        @Override
        Integer get(Object key) {
            return null
        }

        @Override
        Integer put(String key, Integer value) {
            return null
        }

        @Override
        Integer remove(Object key) {
            return null
        }

        @Override
        void putAll(Map<? extends String, ? extends Integer> m) {}

        @Override
        void clear() {}

        @Override
        Set<String> keySet() {
            return null
        }

        @Override
        Collection<Integer> values() {
            return null
        }

        @Override
        Set<Entry<String, Integer>> entrySet() {
            return null
        }
    }

    def "should recognise key and value types when Map is not the superest superinterface"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def jType = javers.<KeyValueType> getTypeMapping(ExtendsImplementsMapInMiddle)
        def expectedJType = javers.<KeyValueType> getTypeMapping(ExtendsImplementsMapInMiddle.getGenericInterfaces()[0])

        then:
        jType.getKeyJavaType() == expectedJType.getKeyJavaType()
        jType.getValueJavaType() == expectedJType.getValueJavaType()
    }
}
