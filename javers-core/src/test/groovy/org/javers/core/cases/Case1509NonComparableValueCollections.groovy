package org.javers.core.cases

import groovy.transform.EqualsAndHashCode
import org.javers.core.JaversBuilder
import org.javers.core.JaversTestBuilder
import org.javers.core.diff.ListCompareAlgorithm
import spock.lang.Specification
import spock.lang.Unroll

class Case1509NonComparableValueCollections extends Specification {
    @Unroll
    def "should compare changed non-Comparable Values in #kind"() {
        given:
        def javers = JaversBuilder.javers()
                .registerValue(ModuleId)
                .withListCompareAlgorithm(ListCompareAlgorithm.AS_SET)
                .build()

        when:
        def diff = javers.compare(holder(kind, ['first', 'second']), holder(kind, ['first']))

        then:
        diff.hasChanges()

        where:
        kind << ['Set', 'ListAsSet']
    }

    @Unroll
    def "should ignore insertion order of non-Comparable Values in #kind"() {
        given:
        def javers = JaversBuilder.javers()
                .registerValue(ModuleId)
                .withListCompareAlgorithm(ListCompareAlgorithm.AS_SET)
                .build()

        expect:
        // Equal hash codes force HashSet traversal to retain the opposite insertion orders.
        new ModuleId(id: 'Aa').hashCode() == new ModuleId(id: 'BB').hashCode()
        !javers.compare(holder(kind, ['Aa', 'BB']), holder(kind, ['BB', 'Aa'])).hasChanges()

        where:
        kind << ['Set', 'ListAsSet']
    }

    @Unroll
    def "should canonicalize nullable Values regardless of insertion order in #kind"() {
        given:
        def javers = JaversBuilder.javers()
                .registerValue(ModuleId)
                .withListCompareAlgorithm(ListCompareAlgorithm.AS_SET)
                .build()

        expect:
        !javers.compare(holder(kind, [null, 'first']), holder(kind, ['first', null])).hasChanges()

        where:
        kind << ['Set', 'ListAsSet']
    }

    def "should ignore duplicate non-Comparable Values in ListAsSet properties"() {
        given:
        def javers = JaversBuilder.javers()
                .registerValue(ModuleId)
                .withListCompareAlgorithm(ListCompareAlgorithm.AS_SET)
                .build()

        expect:
        !javers.compare(holder('ListAsSet', ['first', 'first']), holder('ListAsSet', ['first'])).hasChanges()
    }

    def "should preserve element order in ordinary List properties"() {
        given:
        def javers = JaversBuilder.javers().registerValue(ModuleId).build()

        expect:
        javers.compare(holder('ListAsSet', ['first', 'second']),
                holder('ListAsSet', ['second', 'first'])).hasChanges()
    }

    def "should preserve natural ordering in existing Comparable hashes"() {
        given:
        def javers = JaversTestBuilder.javersTestAssembly()
        def assignment = new ComparableAssignment(values: new LinkedHashSet<>([10, 2]))
        def node = javers.createLiveNode(assignment)

        when:
        def state = javers.snapshotFactory.createSnapshotStateNoRefsSortedSets(node.cdo)

        then:
        state.getPropertyValue('values').toList() == [2, 10]
        assignment.values.toList() == [10, 2]
    }

    def "should preserve a SortedSet custom comparator in existing hashes"() {
        given:
        def javers = JaversTestBuilder.javersTestAssembly()
        def sorted = new TreeSet<Integer>(Comparator.reverseOrder())
        sorted.addAll([2, 10])
        def assignment = new ComparableAssignment(values: sorted)
        def node = javers.createLiveNode(assignment)

        when:
        def state = javers.snapshotFactory.createSnapshotStateNoRefsSortedSets(node.cdo)

        then:
        state.getPropertyValue('values').toList() == [10, 2]
        assignment.values.toList() == [10, 2]
    }

    private static Object holder(String kind, List<String> ids) {
        def values = ids.collect { it == null ? null : new ModuleId(id: it) }
        if (kind == 'Set') {
            return new SetHolder(assignments: [new SetAssignment(values: new LinkedHashSet<>(values))] as Set)
        }
        new ListHolder(assignments: [new ListAssignment(values: values)])
    }

    @EqualsAndHashCode
    static class ModuleId {
        String id

        @Override
        String toString() {
            // Distinct Values must not be ordered or collapsed by Object.toString().
            'module'
        }
    }

    static class SetAssignment {
        Set<ModuleId> values
    }

    static class ListAssignment {
        List<ModuleId> values
    }

    static class SetHolder {
        Set<SetAssignment> assignments
    }

    static class ListHolder {
        List<ListAssignment> assignments
    }

    static class ComparableAssignment {
        Set<Integer> values
    }
}
