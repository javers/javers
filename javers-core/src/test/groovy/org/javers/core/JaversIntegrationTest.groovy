package org.javers.core

import groovy.json.JsonSlurper
import org.javers.core.model.DummyUser
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.model.DummyUserWithDate
import org.javers.test.builder.DummyUserDetailsBuilder
import org.joda.time.LocalDateTime
import spock.lang.Specification

import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.MALE
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserDetailsBuilder.dummyUserDetails

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
        change.leftValue.value == FEMALE
        change.rightValue.value == MALE
    }

    def "should serialize whole Diff"() {
        given:
        DummyUser user =  dummyUser("id").withSex(FEMALE).build();
        DummyUser user2 = dummyUser("id").withSex(MALE).withDetails(1).build();
        Javers javers = JaversTestBuilder.javers()

        when:
        Diff diff = javers.compare("user", user, user2)
        String jsonText = javers.toJson(diff)
        System.out.println("jsonText:\n"+jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.size() == 4
        json.id == 0
        json.userId == "user"
        json.diffDate != null
        json.changes.size() == 3
        json.changes[0].changeType == "NewObject"
        //...
    }
}
