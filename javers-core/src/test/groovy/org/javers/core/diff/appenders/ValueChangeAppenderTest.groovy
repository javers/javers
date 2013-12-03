package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.NodePair
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.model.domain.changeType.ValueChange
import org.javers.model.mapping.Property
import org.javers.model.object.graph.ObjectNode
import org.javers.test.builder.DummyUserBuilder
import org.javers.test.builder.DummyUserDetailsBuilder

import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.OCCASIONALLY
import static org.javers.test.ValueChangesAssert.assertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails

/**
 * @author bartosz walacik
 */
class ValueChangeAppenderTest extends AbstractDiffTest {

    def "should not append valueChange when values are equal" () {
        given:
        ObjectNode left =  buildGraph(dummyUser().withName("1").withSex(FEMALE).build())
        ObjectNode right = buildGraph(dummyUser().withName("1").withSex(FEMALE).build())
        Property sex = getEntity(DummyUser).getProperty("sex")

        when:
        Collection<ValueChange> changes =
            new ValueChangeAppender().calculateChanges(new NodePair(left,right),sex)

        then:
        changes.size() == 0
    }

    def "should append Enum valueChange" () {
        given:
        ObjectNode left =  buildGraph(dummyUser().withName("1").withSex(FEMALE).build())
        ObjectNode right = buildGraph(dummyUser().withName("1").withSex(OCCASIONALLY).build())
        Property sex = getEntity(DummyUser).getProperty("sex")

        when:
        Collection<ValueChange> changes =
            new ValueChangeAppender().calculateChanges(new NodePair(left,right),sex)

        then:
        assertThat(changes)
            .hasSize(1)
            .assertThatFirstElement()
            .hasProperty(sex)
            .hasCdoId("1")
            .hasLeftValue(FEMALE)
            .hasRightValue(OCCASIONALLY)
    }

    def "should append int valueChange" () {
        given:
        ObjectNode left =  buildGraph(dummyUser().withName("1").withAge(1).build())
        ObjectNode right = buildGraph(dummyUser().withName("1").withAge(2).build())
        Property age = getEntity(DummyUser).getProperty("age")

        when:
        Collection<ValueChange> changes =
            new ValueChangeAppender().calculateChanges(new NodePair(left,right),age)

        then:
        assertThat(changes)
                .hasSize(1)
                .assertThatFirstElement()
                .hasProperty(age)
                .hasCdoId("1")
                .hasLeftValue(1)
                .hasRightValue(2)
    }

    def "should append Integer valueChange" () {
        given:
        ObjectNode left =  buildGraph(dummyUser().withName("1").build())
        ObjectNode right = buildGraph(dummyUser().withName("1").withInteger(5).build())
        Property largeInt = getEntity(DummyUser).getProperty("largeInt")

        when:
        Collection<ValueChange> changes =
            new ValueChangeAppender().calculateChanges(new NodePair(left,right),largeInt)

        then:
        assertThat(changes)
                .hasSize(1)
                .assertThatFirstElement()
                .hasProperty(largeInt)
                .hasCdoId("1")
                .doesNotHaveLeftValue()
                .hasRightValue(5)
    }

    def "should append boolean valueChange" () {
        given:
        ObjectNode left =  buildGraph(dummyUser().withName("1").withFlag(true).build())
        ObjectNode right = buildGraph(dummyUser().withName("1").withFlag(false).build())
        Property flag = getEntity(DummyUser).getProperty("flag")

        when:
        Collection<ValueChange> changes =
            new ValueChangeAppender().calculateChanges(new NodePair(left,right),flag)

        then:
        assertThat(changes)
                .hasSize(1)
                .assertThatFirstElement()
                .hasProperty(flag)
                .hasCdoId("1")
                .hasLeftValue(true)
                .hasRightValue(false)
    }

    def "should append Boolean valueChange" () {
        given:
        ObjectNode left =  buildGraph(dummyUser().withName("1").build())
        ObjectNode right = buildGraph(dummyUser().withName("1").withBoxedFlag(true).build())
        Property flag = getEntity(DummyUser).getProperty("bigFlag")

        when:
        Collection<ValueChange> changes =
            new ValueChangeAppender().calculateChanges(new NodePair(left,right),flag)

        then:
        assertThat(changes)
                .hasSize(1)
                .assertThatFirstElement()
                .hasProperty(flag)
                .hasCdoId("1")
                .doesNotHaveLeftValue()
                .hasRightValue(Boolean.TRUE)
    }


    def "should append ValueObject valueChange" () {
        given:
        DummyUserDetails leftDummyUserDetails = dummyUserDetails()
                .withId(1)
                .withAddress("Washington Street", "Boston")
                .build();
        DummyUserDetails rightDummyUserDetails = dummyUserDetails()
                .withId(1)
                .withAddress("Wall Street", "New York")
                .build();
        ObjectNode left = buildGraph(leftDummyUserDetails)
        ObjectNode right = buildGraph(rightDummyUserDetails)
        Property address = getEntity(DummyUserDetails).getProperty("dummyAddress")

        when:
        Collection<ValueChange> changes =
            new ValueChangeAppender().calculateChanges(new NodePair(left,right),address)

        then:
        assertThat(changes)
                .hasSize(1)
                .assertThatFirstElement()
                .hasProperty(address)
                .hasCdoId(1)
                .hasLeftValue(leftDummyUserDetails.dummyAddress)
                .hasRightValue(rightDummyUserDetails.dummyAddress)
    }
}
