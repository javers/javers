package org.javers.core.diff.appenders

import org.javers.core.diff.ChangeAssert
import org.javers.core.diff.RealNodePair
import org.javers.core.graph.ObjectNode
import org.javers.repository.jql.InstanceIdDTO
import org.javers.core.metamodel.property.Property
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.DummyUserWithValues
import org.joda.time.LocalDateTime

import static DummyUserWithValues.dummyUserWithDate
import static org.javers.core.diff.appenders.ValueChangeAssert.assertThat
import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.OCCASIONALLY
import static org.javers.core.model.DummyUserWithValues.dummyUserWithSalary
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails

/**
 * @author bartosz walacik
 */
class ValueChangeAppenderTest extends AbstractDiffAppendersTest {

    def "should not append ValueChange when two Values are .equals()"() {
        given:
        def left = buildGraph(new ValuesHolder())
        def right = buildGraph(new ValuesHolder())
        def alwaysEquals = getManagedProperty(ValuesHolder, 'alwaysEquals')

        when:
        def change = new ValueChangeAppender().calculateChanges(new RealNodePair(left,right), alwaysEquals)

        then:
        !change
    }

    def "should append ValueChange when two Values are not .equals()"() {
        given:
        def left = buildGraph(new ValuesHolder())
        def right = buildGraph(new ValuesHolder())
        def neverEquals = getManagedProperty(ValuesHolder, 'neverEquals')

        when:
        def change = new ValueChangeAppender().calculateChanges(new RealNodePair(left,right), neverEquals)

        then:
        ChangeAssert.assertThat(change)
                    .hasProperty(neverEquals)
    }

    def "should set ValueChange metadata"() {
        given:
        def left =  buildGraph(dummyUser("1").withSex(FEMALE).build())
        def right = buildGraph(dummyUser("1").withSex(OCCASIONALLY).build())
        def sex = getManagedProperty(DummyUser,"sex")

        when:
        def change = new ValueChangeAppender().calculateChanges(new RealNodePair(left,right),sex)

        then:
        ChangeAssert.assertThat(change)
                    .hasProperty(sex)
                    .hasInstanceId(DummyUser, "1")
    }

    def "should append Enum valueChange" () {
        given:
        def left =  buildGraph(dummyUser("1").withSex(FEMALE).build())
        def right = buildGraph(dummyUser("1").withSex(OCCASIONALLY).build())
        def sex = getManagedProperty(DummyUser,"sex")

        when:
        def change = new ValueChangeAppender().calculateChanges(new RealNodePair(left,right),sex)

        then:
        assertThat(change)
                  .hasProperty(sex)
                  .hasLeftValue(FEMALE)
                  .hasRightValue(OCCASIONALLY)
    }

    def "should append int valueChange" () {
        given:
        def left =  buildGraph(dummyUser("1").withAge(1).build())
        def right = buildGraph(dummyUser("1").withAge(2).build())
        def age = getManagedProperty(DummyUser,"age")

        when:
        def change = new ValueChangeAppender().calculateChanges(new RealNodePair(left,right),age)

        then:
        assertThat(change)
                  .hasProperty(age)
                  .hasLeftValue(1)
                  .hasRightValue(2)
    }

    def "should append Integer valueChange" () {
        given:
        def left =  buildGraph(dummyUser("1").build())
        def right = buildGraph(dummyUser("1").withInteger(5).build())
        def largeInt = getManagedProperty(DummyUser,"largeInt")

        when:
        def change = new ValueChangeAppender().calculateChanges(new RealNodePair(left,right),largeInt)

        then:
        assertThat(change)
                  .hasProperty(largeInt)
                  .haveLeftValueNull()
                  .hasRightValue(5)
    }

    def "should append boolean valueChange" () {
        given:
        def left =  buildGraph(dummyUser("1").withFlag(true).build())
        def right = buildGraph(dummyUser("1").withFlag(false).build())
        def flag = getManagedProperty(DummyUser, "flag")

        when:
        def change = new ValueChangeAppender().calculateChanges(new RealNodePair(left,right),flag)

        then:
        assertThat(change)
                  .hasProperty(flag)
                  .hasLeftValue(true)
                  .hasRightValue(false)
    }

    def "should append Boolean valueChange" () {
        given:
        def left =  buildGraph(dummyUser("1").build())
        def right = buildGraph(dummyUser("1").withBoxedFlag(true).build())
        def flag = getManagedProperty(DummyUser,"bigFlag")

        when:
        def change = new ValueChangeAppender().calculateChanges(new RealNodePair(left,right),flag)

        then:
        assertThat(change)
                  .hasProperty(flag)
                  .haveLeftValueNull()
                  .hasRightValue(Boolean.TRUE)
    }

    def "should append LocalDateTime Value change" () {
        given:
        def dob = new LocalDateTime()
        def leftUser =  dummyUserWithDate("kaz", null)
        def rightUser = dummyUserWithDate("kaz", dob)
        def left = buildGraph(leftUser)
        def right = buildGraph(rightUser)
        def dobProperty = getManagedProperty(DummyUserWithValues,"dob")

        when:
        def change = new ValueChangeAppender().calculateChanges(new RealNodePair(left,right), dobProperty)

        then:
        assertThat(change)
                .hasProperty(dobProperty)
                .hasLeftValue(null)
                .hasRightValue(dob)
    }

    def "should append BigDecimal Value change" () {
        given:
        def salary = new BigDecimal(2.5)
        def leftUser =  dummyUserWithSalary("kaz", null)
        def rightUser = dummyUserWithSalary("kaz", salary)
        def left = buildGraph(leftUser)
        def right = buildGraph(rightUser)
        def salaryProperty = getManagedProperty(DummyUserWithValues,"salary")

        when:
        def change = new ValueChangeAppender().calculateChanges(new RealNodePair(left,right), salaryProperty)

        then:
        assertThat(change)
                .hasProperty(salaryProperty)
                .hasLeftValue(null)
                .hasRightValue(salary)
    }

    def "should create fragment valueChange for embedded ValueObject" () {
        given:
        def leftUser =  dummyUserDetails(1).withAddress("Boston","Washington Street").build();
        def rightUser = dummyUserDetails(1).withAddress("Boston","Wall Street").build();
        def left = buildGraph(leftUser)
        def right = buildGraph(rightUser)
        def address = getManagedProperty(DummyUserDetails,"dummyAddress")
        def street =  getManagedProperty(DummyAddress,"street")

        when:
        def change = new ValueChangeAppender().calculateChanges(
                      new RealNodePair(followEdge(left,address), followEdge(right,address)),street)

        then:
        assertThat(change)
                  .hasValueObjectId(DummyAddress, new InstanceIdDTO(DummyUserDetails,1), "dummyAddress")
                  .hasLeftValue("Washington Street")
                  .hasRightValue("Wall Street")
                  .hasProperty(street)
    }

    ObjectNode followEdge(ObjectNode node, Property property) {
        node.getEdge(property).reference;
    }
}
