package org.javers.core.examples

import ch.qos.logback.core.joran.conditional.ThenAction
import org.javers.core.JaversBuilder
import org.javers.core.model.DummyUser
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * @author bartosz.walacik
 */
class JqlExample extends Specification {

    def "should query for Entity changes by instance Id"() {
        given:
        def javers = JaversBuilder.javers().build()

        javers.commit( "author", new DummyUser(name:"bob",  age:30) )
        javers.commit( "author", new DummyUser(name:"bob",  age:31) )
        javers.commit( "author", new DummyUser(name:"john", age:25) )
        javers.commit( "author", new DummyUser(name:"lucy", age:35) )

        when:
        def changes = javers.findChanges( QueryBuilder.byInstanceId("bob", DummyUser.class).build() )

        then:
        printChanges(changes)
        assert changes.size() == 3
    }

    def printChanges(def changes){
        def i = 0
        changes.each {println "$i. commit "+ it.commitMetadata.get().id.toString()+": $it"; i++}
    }
}
