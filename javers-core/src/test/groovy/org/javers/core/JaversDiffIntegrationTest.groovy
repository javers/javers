package org.javers.core

import groovy.json.JsonSlurper
import org.javers.core.diff.Diff
import org.javers.core.diff.DiffAssert
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.json.DummyPointJsonTypeAdapter
import org.javers.core.json.DummyPointNativeTypeAdapter
import org.javers.core.model.DummyPoint
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.model.PrimitiveEntity
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.metamodel.object.InstanceId.InstanceIdDTO.instanceId
import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.MALE
import static org.javers.core.model.DummyUserWithPoint.userWithPoint
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JaversDiffIntegrationTest extends Specification {

    def "should create NewObject for all nodes in initial diff"() {
        given:
        Javers javers = JaversTestBuilder.newInstance()
        DummyUser left = dummyUser("kazik").withDetails().build()

        when:
        Diff diff = javers.initial(left)

        then:
        DiffAssert.assertThat(diff).has(2, NewObject)
    }

    def "should create snapshot of NewObject"() {
        given:
        Javers javers = JaversBuilder.javers().build()
        DummyUser left =  new DummyUser(name: "kazik")
        DummyUser right = new DummyUser(name: "kazik", dummyUserDetails: new DummyUserDetails(id: 1, someValue: "some"))

        when:
        Diff diff = javers.compare(left, right)

        then:
        DiffAssert.assertThat(diff)
                  .hasNewObject(instanceId(1,DummyUserDetails),[id:1, someValue: "some"])
                  .hasReferenceChangeAt("dummyUserDetails",null,instanceId(1,DummyUserDetails))
    }

    //smoke test
    def "should create valueChange with Enum" () {
        given:
        DummyUser user =  dummyUser("id").withSex(FEMALE).build();
        DummyUser user2 = dummyUser("id").withSex(MALE).build();
        Javers javers = JaversTestBuilder.newInstance()

        when:
        Diff diff = javers.compare(user, user2)

        then:
        diff.changes.size() == 1
        ValueChange change = diff.changes[0]
        change.leftValue == FEMALE
        change.rightValue == MALE
    }

    def "should serialize whole Diff"() {
        given:
        DummyUser user =  dummyUser("id").withSex(FEMALE).build();
        DummyUser user2 = dummyUser("id").withSex(MALE).withDetails(1).build();
        Javers javers = JaversTestBuilder.newInstance()

        when:
        Diff diff = javers.compare(user, user2)
        String jsonText = javers.toJson(diff)
        //println("jsonText:\n"+jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.changes.size() == 4
        json.changes[0].changeType == "NewObject"
        json.changes[1].changeType == "ValueChange"
        json.changes[2].changeType == "ValueChange"
        json.changes[3].changeType == "ReferenceChange"
    }

    def "should support custom JsonTypeAdapter for ValueChange"() {
        given:
        Javers javers = javers()
                       .registerValueTypeAdapter( new DummyPointJsonTypeAdapter() )
                       .build()

        when:
        Diff diff = javers.compare(userWithPoint(1,2), userWithPoint(1,3))
        String jsonText = javers.toJson(diff)
        //println("jsonText:\n"+jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        def change = json.changes[0];
        change.globalCdoId.valueObject == "org.javers.core.model.DummyUserWithPoint"
        change.globalCdoId.cdoId == "/"
        change.changeType == "ValueChange"
        change.property == "point"
        change.leftValue == "1,2" //this is most important in this test
        change.rightValue == "1,3" //this is most important in this test
    }

    def "should support custom native Gson TypeAdapter"() {
        given:
        Javers javers = javers()
                .registerValueGsonTypeAdapter(DummyPoint, new DummyPointNativeTypeAdapter() )
                .build()

        when:
        def diff = javers.compare(userWithPoint(1,2), userWithPoint(1,3))
        def jsonText = javers.toJson(diff)
        //println("jsonText:\n"+jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.changes[0].leftValue == "1,2"
        json.changes[0].rightValue == "1,3"
    }

    def "should understand primitive default values when creating NewObject snapshot"() {
        given:
        Javers javers = javers().build()

        when:
        def diff = javers.initial(new PrimitiveEntity())

        then:
        DiffAssert.assertThat(diff).hasOnly(1, NewObject)
    }

    def "should understand primitive default values when creating ValueChange"() {
        given:
        Javers javers = javers().build()

        when:
        def diff = javers.compare(new PrimitiveEntity(), new PrimitiveEntity())

        then:
        DiffAssert.assertThat(diff).hasChanges(0)
    }
}
