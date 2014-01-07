package org.javers.core

import org.javers.core.model.DummyUser
import org.javers.core.diff.Diff
import org.javers.core.diff.changetype.ValueChange
import spock.lang.Specification

import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.MALE
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JaversIntegrationTest extends Specification {
    def "should create valueChange" () {
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
        //change.leftValue.json == '"FEMALE"'
        //change.rightValue.json == '"MALE"'
    }

    def "should serialize whole Diff"() {
        given:
        DummyUser user =  dummyUser("id").withSex(FEMALE).build();
        DummyUser user2 = dummyUser("id").withSex(MALE).withDetails(1).build();
        Javers javers = JaversTestBuilder.javers()

        when:
        Diff diff = javers.compare("user", user, user2)
        String json = javers.toJson(diff)
        System.out.println("json:\n"+json)

        then:
        json == "diff"
    }
}
