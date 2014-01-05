package org.javers.core;

import org.javers.core.model.DummyUser
import org.javers.model.domain.Diff
import org.javers.model.domain.changeType.ValueChange
import spock.lang.Specification

import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.MALE
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import spock.lang.Specification


/**
 * @author bartosz walacik
 */
class JaversIntegrationTest extends Specification {
    def "should create valueChange and dehydrate it" () {
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
        change.leftValue.json == '"FEMALE"'
        change.rightValue.json == '"MALE"'
    }
}
