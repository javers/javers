package org.javers.guavasupport

import com.google.common.collect.HashMultiset
import com.google.common.collect.Lists
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.JaversBuilder.javers

/**
 * @author akrystian
 */
class GuavaAddOnE2ETest extends Specification {

    @Unroll
    def "should detect value changes in Multiset "() {
        def javers = javers().build()

        def left = new SnapshotEntity(multiSetValueObject: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetValueObject: HashMultiset.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 1
        def changes = diff.changes[0].changes
        changes.size() == extpectedChanges

        where:
        leftList                                   | rightList                                | extpectedChanges
        Lists.asList(new DummyAddress("New York")) | Lists.asList(new DummyAddress("Boston")) | 2
        Lists.asList("New York")                   | Lists.asList("Boston")                   | 2
        Lists.asList("New York")                   | Lists.asList("New York", "New York")     | 1
        Collections.emptyList()                    | Lists.asList("New York")                 | 1
    }

    @Unroll
    def "should not detect any value changes in Multiset "() {
        def javers = javers().build()

        def left = new SnapshotEntity(multiSetValueObject: HashMultiset.create(leftList))
        def right = new SnapshotEntity(multiSetValueObject: HashMultiset.create(rightList))

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 0

        where:
        leftList                           | rightList
        Lists.asList("New York")           | Lists.asList("New York")
        Lists.asList("New York", "Boston") | Lists.asList("Boston", "New York")
    }
}
