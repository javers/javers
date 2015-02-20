package org.javers.core.diff.appenders.levenshtein

import org.javers.core.model.DummyUser
import spock.lang.Unroll

import static org.javers.core.diff.appenders.ContainerChangeAssert.getAssertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser

class LevenshteinListChangeAppenderTest extends AbstractLevenshteinListTest {

    @Unroll
    def "should recognise lists as equal"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 3]).build()
        def rightNode = dummyUser().withIntegerList([1, 2, 3]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        change == null
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
                .hasReferenceAdded(3, 4)
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
                .hasReferenceAdded(2, 4)
    }

    @Unroll
    def "should find removed element in the middle of the list"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 4, 3]).build()
        def rightNode = dummyUser().withIntegerList([1, 2, 3]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasReferenceRemoved(2, 4)
    }

    @Unroll
    def "should find changed element in the middle of the list"() {

        when:
        def leftNode = dummyUser().withIntegerList([1, 2, 4, 3]).build()
        def rightNode = dummyUser().withIntegerList([1, 2, 5, 3]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))


        println(change)
        then:
        assertThat(change)
                .hasSize(1)
                .hasReferenceChange(2, 4, 5)
    }

    @Unroll
    def "should find changed element at the end of the list"() {

        when:
        def leftNode = dummyUser().withIntegerList([0, 1, 2, 4]).build()
        def rightNode = dummyUser().withIntegerList([0, 1, 2, 5]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        assertThat(change)
                .hasSize(1)
                .hasReferenceChange(3, 4, 5)
    }

    @Unroll
    def "should find changed and added element"() {

        when:
        def leftNode = dummyUser().withIntegerList([0, 1, 2, 4]).build()
        def rightNode = dummyUser().withIntegerList([0, 1, 3, 5, 6]).build()

        def change = levenshteinListChangeAppender().calculateChanges(
                realNodePair(leftNode, rightNode), getProperty(DummyUser, "integerList"))

        then:
        assertThat(change)
                .hasSize(3)
                .hasReferenceChange(2, 2, 3)
                .hasReferenceChange(3, 4, 5)
                .hasReferenceAdded(4, 6)
    }
}
