package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.ChangeAssert
import org.javers.core.diff.RealNodePair
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.diff.Change
import org.javers.core.metamodel.property.Property
import org.javers.model.object.graph.ObjectNode

import static ReferenceChangeAssert.assertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser

class ReferenceChangeAppenderTest extends AbstractDiffTest{

    def "should not append change when the same references"() {
        given:
        DummyUser leftReference = dummyUser().withName("1").withDetails(2).build()
        DummyUser rightReference = leftReference;
        ObjectNode leftNode = buildGraph(leftReference)
        ObjectNode rightNode = buildGraph(rightReference)
        Property property = getEntity(DummyUser).getProperty("dummyUserDetails")

        when:
        def change = new ReferenceChangeAppender().calculateChanges(new RealNodePair(leftNode, rightNode), property)

        then:
        change == null
    }

    def "should set ReferenceChange metadata"() {
        given:
        ObjectNode leftNode =  buildGraph(dummyUser().withName("1").withDetails(2).build())
        ObjectNode rightNode = buildGraph(dummyUser().withName("1").build())
        Property property = getEntity(DummyUser).getProperty("dummyUserDetails")

        when:
        ReferenceChange change = new ReferenceChangeAppender().calculateChanges(new RealNodePair(leftNode, rightNode), property)

        then:
        ChangeAssert.assertThat(change)
                    .hasInstanceId(DummyUser, "1")
                    .hasAffectedCdo(rightNode)
    }

    def "should compare refs null safely"() {
        given:
        ObjectNode leftNode =  buildGraph(dummyUser().withName("1").withDetails(2).build())
        ObjectNode rightNode = buildGraph(dummyUser().withName("1").build())
        Property property = getEntity(DummyUser).getProperty("dummyUserDetails")

        when:
        ReferenceChange change = new ReferenceChangeAppender().calculateChanges(new RealNodePair(leftNode, rightNode), property)

        then:
        assertThat(change)
                  .hasLeftReference(DummyUserDetails,2)
                  .hasRightReference(null)
                  .hasProperty(property)
    }

    def "should append reference change"() {
        given:
        ObjectNode leftNode =  buildGraph(dummyUser().withName("1").withDetails(2).build())
        ObjectNode rightNode = buildGraph(dummyUser().withName("1").withDetails(3).build())
        Property property = getEntity(DummyUser).getProperty("dummyUserDetails")

        when:
        ReferenceChange change =
            new ReferenceChangeAppender().calculateChanges(new RealNodePair(leftNode, rightNode), property)

        then:
        assertThat(change)
                  .hasLeftReference(DummyUserDetails,2)
                  .hasRightReference(DummyUserDetails,3)
                  .hasProperty(property)
    }

}
