package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.NodePair
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.model.domain.Change
import org.javers.model.domain.GlobalCdoId
import org.javers.model.mapping.Property
import org.javers.model.object.graph.ObjectNode

import static org.javers.test.ReferenceChangesAssert.assertThat

import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails


class ReferenceChangeAppenderTest extends AbstractDiffTest{

    def "should not append when the same reference"() {
        given:
        DummyUser leftReference = dummyUser().withName("1").withDetails(2).build()
        DummyUser rightReference = leftReference;
        ObjectNode leftNode = buildGraph(leftReference)
        ObjectNode rightNode = buildGraph(rightReference)
        Property property = getEntity(DummyUser).getProperty("dummyUserDetails")

        when:
        Collection<Change> changes =
            new ReferenceChangeAppender().calculateChanges(new NodePair(leftNode, rightNode), property);

        then:
        assertThat changes hasSize 0
    }

    def "should append reference change"() {
        given:
        DummyUser leftReference = dummyUser().withName("1").withDetails(2).build()
        DummyUser rightReference = dummyUser().withName("1").withDetails(3).build()
        ObjectNode leftNode = buildGraph(leftReference)
        ObjectNode rightNode = buildGraph(rightReference)
        Property property = getEntity(DummyUser).getProperty("dummyUserDetails")

        when:
        Collection<Change> changes =
            new ReferenceChangeAppender().calculateChanges(new NodePair(leftNode, rightNode), property);

        then:
        assertThat(changes)
            .hasSize(1)
            .assertThatFirstChange()
            .hasCdoId("1")
            .hasLeftReference(new GlobalCdoId(leftReference.dummyUserDetails.id, getEntity(DummyUserDetails)))
            .hasRightReference(new GlobalCdoId(rightReference.dummyUserDetails.id, getEntity(DummyUserDetails)))
            .hasProperty(property)
    }

    def "should not append reference change on primitive"() {
        given:
        DummyUser leftReference = dummyUser().withName("1").withInteger(3).build()
        DummyUser rightReference = dummyUser().withName("1").withInteger(4).build()
        ObjectNode leftNode = buildGraph(leftReference)
        ObjectNode rightNode = buildGraph(rightReference)
        Property property = getEntity(DummyUser).getProperty("largeInt")

        when:
        Collection<Change> changes =
            new ReferenceChangeAppender().calculateChanges(new NodePair(leftNode, rightNode), property);

        then:
        assertThat changes hasSize 0
    }

    def "should not append reference change on ValueObject"() {
        given:
        DummyUserDetails leftReference = dummyUserDetails().withId(1).withAddress("Green Street", "London").build()
        DummyUserDetails rightReference = dummyUserDetails().withId(1).withAddress("Yellow Street", "Belfast").build()
        ObjectNode leftNode = buildGraph(leftReference)
        ObjectNode rightNode = buildGraph(rightReference)
        Property property = getEntity(DummyUserDetails).getProperty("dummyAddress")

        when:
        Collection<Change> changes =
            new ReferenceChangeAppender().calculateChanges(new NodePair(leftNode, rightNode), property);

        then:
        assertThat changes hasSize 0
    }
}
