package org.javers.core.diff.appenders

import org.javers.core.Javers
import org.javers.core.JaversTestBuilder
import org.javers.core.diff.Diff
import org.javers.core.model.DummyUserWithDate
import org.joda.time.LocalDateTime

import static org.javers.core.diff.appenders.ValueChangeAssert.assertThat
import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.ChangeAssert
import org.javers.core.diff.NodePair
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.diff.changetype.ValueChange
import org.javers.model.mapping.Property
import org.javers.model.object.graph.ObjectNode
import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.OCCASIONALLY
import static org.javers.core.model.DummyUserWithDate.dummyUserWithDate
import static org.javers.core.model.DummyUserWithDate.dummyUserWithDate
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

    def "should set ValueChange metadata"() {
        given:
        ObjectNode left =  buildGraph(dummyUser().withName("1").withSex(FEMALE).build())
        ObjectNode right = buildGraph(dummyUser().withName("1").withSex(OCCASIONALLY).build())
        Property sex = getEntity(DummyUser).getProperty("sex")

        when:
        Collection<ValueChange> changes =
                new ValueChangeAppender().calculateChanges(new NodePair(left,right),sex)

        then:
        ChangeAssert.assertThat(changes[0])
                    .hasGlobalId(DummyUser, "1")
                    .hasAffectedCdo(right)
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
        changes.size() == 1
        assertThat(changes[0])
                  .hasProperty(sex)
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
        changes.size() == 1
        assertThat(changes[0])
                  .hasProperty(age)
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
        changes.size() == 1
        assertThat(changes[0])
                  .hasProperty(largeInt)
                  .haveLeftValueNull()
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
        changes.size() == 1
        assertThat(changes[0])
                  .hasProperty(flag)
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
        changes.size() == 1
        assertThat(changes[0])
                  .hasProperty(flag)
                  .haveLeftValueNull()
                  .hasRightValue(Boolean.TRUE)
    }

    def "should append ImmutableValue change" () {
        given:
        LocalDateTime dob = new LocalDateTime()
        def leftUser =  dummyUserWithDate("kaz", null)
        def rightUser = dummyUserWithDate("kaz", dob)
        ObjectNode left = buildGraph(leftUser)
        ObjectNode right = buildGraph(rightUser)
        Property dobProperty = getEntity(DummyUserWithDate).getProperty("dob")

        when:
        def changes = new ValueChangeAppender().calculateChanges(new NodePair(left,right), dobProperty)

        then:
        changes.size() == 1
        assertThat(changes[0])
                .hasGlobalId(DummyUserWithDate, "kaz")
                .hasProperty(dob)
                .hasLeftValue(null)
                .hasRightValue(dob)
    }

    def "should create fragment valueChange for embedded ValueObject" () {
        given:
        def leftUser =  dummyUserDetails(1).withAddress("Washington Street", "Boston").build();
        def rightUser = dummyUserDetails(1).withAddress("Wall Street",       "Boston").build();
        ObjectNode left = buildGraph(leftUser)
        ObjectNode right = buildGraph(rightUser)
        Property address = getEntity(DummyUserDetails).getProperty("dummyAddress")

        when:
        def changes = new ValueChangeAppender().calculateChanges(new NodePair(left,right),address)

        then:
        changes.size() == 1
        assertThat(changes[0])
                  .hasGlobalId(DummyUserDetails, 1, "street")
                  .hasProperty(address)
                  .hasLeftValue("Washington Street")
                  .hasRightValue("Wall Street")
    }
}
