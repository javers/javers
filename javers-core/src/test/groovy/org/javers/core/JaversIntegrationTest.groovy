package org.javers.core

import groovy.json.JsonSlurper
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.json.DummyPointJsonTypeAdapter
import org.javers.core.json.DummyPointNativeTypeAdapter
import org.javers.core.model.DummyPoint
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserWithPoint
import spock.lang.Specification

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.MALE
import static org.javers.core.model.DummyUserWithPoint.userWithPoint
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JaversIntegrationTest extends Specification {
    //smoke test
    def "should create valueChange with Enum" () {
        given:
        DummyUser user =  dummyUser("id").withSex(FEMALE).build();
        DummyUser user2 = dummyUser("id").withSex(MALE).build();
        Javers javers = JaversTestBuilder.javers()

        when:
        Diff diff = javers.compare("user", user, user2)

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
        Javers javers = JaversTestBuilder.javers()

        when:
        Diff diff = javers.compare("user", user, user2)
        String jsonText = javers.toJson(diff)
        println("jsonText:\n"+jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.size() == 4
        json.id == 0
        json.userId == "user"
        json.diffDate != null
        json.changes.size() == 3
        json.changes[0].changeType == "NewObject"
        json.changes[1].changeType == "ValueChange"
        json.changes[2].changeType == "ReferenceChange"
    }

    def "should support custom JsonTypeAdapter for ValueChange"() {
        given:
        Javers javers = javers()
                       .registerValueObject(DummyUserWithPoint)
                       .registerValueTypeAdapter( new DummyPointJsonTypeAdapter() )
                       .build()

        when:
        Diff diff = javers.compare("user", userWithPoint(1,2), userWithPoint(1,3))
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
                .registerValueObject(DummyUserWithPoint)
                .registerValueGsonTypeAdapter(DummyPoint, new DummyPointNativeTypeAdapter() )
                .build()

        when:
        Diff diff = javers.compare("user", userWithPoint(1,2), userWithPoint(1,3))
        String jsonText = javers.toJson(diff)
        //println("jsonText:\n"+jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.changes[0].leftValue == "1,2"
        json.changes[0].rightValue == "1,3"
    }
}
