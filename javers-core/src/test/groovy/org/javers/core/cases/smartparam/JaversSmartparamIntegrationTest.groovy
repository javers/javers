package org.javers.core.cases.smartparam

import groovy.json.JsonSlurper
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.JaversTestBuilder
import org.javers.core.diff.Diff
import org.javers.core.model.DummyUser

import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.MALE
import static org.javers.test.builder.DummyUserBuilder.dummyUser
import static org.javers.test.builder.DummyUserBuilder.dummyUser

/**
 * @author bartosz walacik
 */
class JaversSmartparamIntegrationTest {

    def "should serialize diff for ValueObject with map"() {
        given:
        JaversBuilder.javers()
                     .registerValueObject(DummyUser)
                     .build()

        def user =  dummyUser("id").withValueMap(null).build()
        def user2 = dummyUser("id").withValueMap(null).build()
        Javers javers = JaversTestBuilder.javers()

        when:
        Diff diff = javers.compare("user", user, user2)
        String jsonText = javers.toJson(diff)
        println("jsonText:\n"+jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.userId == "user"
        json.changes.size() == 3
        json.changes[0].changeType == "EntryAdded"
        json.changes[1].changeType == "EntryRemoved"
        json.changes[2].changeType == "EntryChanged"


    }
}
