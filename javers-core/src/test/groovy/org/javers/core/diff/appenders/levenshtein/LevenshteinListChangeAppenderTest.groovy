package org.javers.core.diff.appenders.levenshtein

import org.javers.core.model.DummyUser
import spock.lang.Unroll

import static org.javers.core.diff.appenders.ContainerChangeAssert.assertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser

class LevenshteinListChangeAppenderTest extends AbstractLevenshteinListTest {

    @Unroll
    def "should find #changeDesc element at the beginning of the list"() {
        given:
        def leftNode =  dummyUser().withIntegerList(leftList).build()
        def rightNode = dummyUser().withIntegerList(rightList).build()

        when:
        def change = levenshteinListChangeAppender().calculateChanges(
                     realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        change.changes.size() == 1
        assertion.call(change)

        where:
        leftList  | rightList | changeDesc || assertion
        [1, 2, 3] | [2, 3]    | "removed"  || {it -> assertThat(it).hasValueRemoved(0, 1)}
        [2, 3]    | [1, 2, 3] | "added"    || {it -> assertThat(it).hasValueAdded(0, 1)}
        [1, 2, 3] | [9, 2, 3] | "changed"  || {it -> assertThat(it).hasValueChange(0, 1, 9)}
    }

    def "should find added element at the beginning of the list"() {
        given:
        def leftNode =  dummyUser().withIntegerList([2, 3]).build()
        def rightNode = dummyUser().withIntegerList([1, 2, 3]).build()

        when:
        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueRemoved(0, 1)
    }

    @Unroll
    def "should recognise that lists as equal"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 3]).build()
        def rightNode = dummyUser().withIntegerList([1, 2, 3]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        !change
    }

    @Unroll
    def "should find added element at the end of the list"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 3]).build()
        def rightNode = dummyUser().withIntegerList([1, 2, 3, 4]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueAdded(3, 4)
    }

    @Unroll
    def "should find added element in the middle of the list"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 3]).build()
        def rightNode = dummyUser().withIntegerList([1, 2, 4, 3]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueAdded(2, 4)
    }

    def "should find removed element in the middle of the list"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 4, 3]).build()
        def rightNode = dummyUser().withIntegerList([1, 2, 3]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueRemoved(2, 4)
    }

    def "should find removed element at the end of the list"() {

        when:
        def leftNode =   dummyUser().withIntegerList([1, 2, 3]).build()
        def rightNode =  dummyUser().withIntegerList([1, 2]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueRemoved(2, 3)
    }

    def "should find changed element in the middle of the list"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 4, 3]).build()
        def rightNode = dummyUser().withIntegerList([1, 2, 5, 3]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))


        then:
        assertThat(change)
                .hasSize(1)
                .hasValueChange(2, 4, 5)
    }

    def "should find changed element at the end of the list"() {
        when:
        def leftNode = dummyUser().withIntegerList([0, 1, 2, 4]).build()
        def rightNode = dummyUser().withIntegerList([0, 1, 2, 5]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasValueChange(3, 4, 5)
    }

    def "should find changed and added element"() {
        when:
        def leftNode = dummyUser().withIntegerList([0, 1, 2, 4]).build()
        def rightNode = dummyUser().withIntegerList([0, 1, 3, 5, 6]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        assertThat(change)
                .hasSize(3)
                .hasValueChange(2, 2, 3)
                .hasValueChange(3, 4, 5)
                .hasValueAdded(4, 6)
    }
}
