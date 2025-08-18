package org.javers.core.cases

import org.javers.core.JaversBuilder
import org.javers.core.metamodel.type.CollectionType
import spock.lang.Specification

/**
 * https://github.com/javers/javers/issues/1450
 *
 * These tests use {@link java.util.Set}, but they should apply to any types that
 * are handled by {@link org.javers.core.metamodel.type.CollectionType},
 * since they all have a single type parameter.
 *
 * @author Yat Ho
 */
class CollectionTypeParameterResolutionTest extends Specification {
    class ExtendsSetSubtype extends HashSet<String> {}

    def "should recognise value type from Set subtype"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def jType = javers.<CollectionType> getTypeMapping(ExtendsSetSubtype)
        def expectedJType = javers.<CollectionType> getTypeMapping(ExtendsSetSubtype.getGenericSuperclass())

        then:
        jType.getItemJavaType() == expectedJType.getItemJavaType()
    }

    class ImplementsSet implements Set<String> {
        @Override
        int size() {
            return 0
        }

        @Override
        boolean isEmpty() {
            return false
        }

        @Override
        boolean contains(Object o) {
            return false
        }

        @Override
        Iterator<String> iterator() {
            return null
        }

        @Override
        Object[] toArray() {
            return null
        }

        @Override
        <T> T[] toArray(T[] a) {
            return null
        }

        @Override
        boolean add(String s) {
            return false
        }

        @Override
        boolean remove(Object o) {
            return false
        }

        @Override
        boolean containsAll(Collection<?> c) {
            return false
        }

        @Override
        boolean addAll(Collection<? extends String> c) {
            return false
        }

        @Override
        boolean retainAll(Collection<?> c) {
            return false
        }

        @Override
        boolean removeAll(Collection<?> c) {
            return false
        }

        @Override
        void clear() {}
    }
    def "should recognise value type from Set interface"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def jType = javers.<CollectionType> getTypeMapping(ImplementsSet)
        def expectedJType = javers.<CollectionType> getTypeMapping(ImplementsSet.getGenericInterfaces()[0])

        then:
        jType.getItemJavaType() == expectedJType.getItemJavaType()
    }

    interface ExtendsSet extends Set<String> {}
    class ImplementsExtendsSet implements ExtendsSet {
        @Override
        int size() {
            return 0
        }

        @Override
        boolean isEmpty() {
            return false
        }

        @Override
        boolean contains(Object o) {
            return false
        }

        @Override
        Iterator<String> iterator() {
            return null
        }

        @Override
        Object[] toArray() {
            return null
        }

        @Override
        <T> T[] toArray(T[] a) {
            return null
        }

        @Override
        boolean add(String s) {
            return false
        }

        @Override
        boolean remove(Object o) {
            return false
        }

        @Override
        boolean containsAll(Collection<?> c) {
            return false
        }

        @Override
        boolean addAll(Collection<? extends String> c) {
            return false
        }

        @Override
        boolean retainAll(Collection<?> c) {
            return false
        }

        @Override
        boolean removeAll(Collection<?> c) {
            return false
        }

        @Override
        void clear() {}
    }

    def "should recognise value type from Set subinterface"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def jType = javers.<CollectionType> getTypeMapping(ImplementsExtendsSet)
        def expectedJType = javers.<CollectionType> getTypeMapping(ImplementsExtendsSet.getInterfaces()[0].getGenericInterfaces()[0])

        then:
        jType.getItemJavaType() == expectedJType.getItemJavaType()
    }

    class EmptyClass {}

    class ExtendsEmptyClass extends EmptyClass {}

    class ExtendsImplementsSetInMiddle extends ExtendsEmptyClass implements Set<String> {
        @Override
        int size() {
            return 0
        }

        @Override
        boolean isEmpty() {
            return false
        }

        @Override
        boolean contains(Object o) {
            return false
        }

        @Override
        Iterator<String> iterator() {
            return null
        }

        @Override
        Object[] toArray() {
            return null
        }

        @Override
        <T> T[] toArray(T[] a) {
            return null
        }

        @Override
        boolean add(String s) {
            return false
        }

        @Override
        boolean remove(Object o) {
            return false
        }

        @Override
        boolean containsAll(Collection<?> c) {
            return false
        }

        @Override
        boolean addAll(Collection<? extends String> c) {
            return false
        }

        @Override
        boolean retainAll(Collection<?> c) {
            return false
        }

        @Override
        boolean removeAll(Collection<?> c) {
            return false
        }

        @Override
        void clear() {}
    }

    def "should recognise value type when Set is not the superest superinterface"() {
        given:
        def javers = JaversBuilder.javers().build()

        when:
        def jType = javers.<CollectionType> getTypeMapping(ExtendsImplementsSetInMiddle)
        def expectedJType = javers.<CollectionType> getTypeMapping(ExtendsImplementsSetInMiddle.getGenericInterfaces()[0])

        then:
        jType.getItemJavaType() == expectedJType.getItemJavaType()
    }
}
