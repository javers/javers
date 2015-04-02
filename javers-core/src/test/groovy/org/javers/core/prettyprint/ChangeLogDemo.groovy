package org.javers.core.prettyprint

import org.javers.core.JaversBuilder
import org.javers.core.changelog.SimpleTextChangeLog
import org.javers.core.model.DummyUser
import org.javers.repository.jql.QueryBuilder
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class ChangeLogDemo extends Specification {

    def "should pretty print the changeLog"() {
        given:
            def javers = JaversBuilder.javers().build()

            def user = new DummyUser('bob', 'Dijk')
            user.setStringSet(['groovy'] as Set)
            javers.commit("some author", user)

            user.setAge(18)
            user.setSurname('van Dijk')
            user.setSupervisor(new DummyUser('New Supervisor'))
            javers.commit('some author', user)

            user.setIntegerList([22,23])
            user.setSex(DummyUser.Sex.MALE)
            user.setStringSet(['java','scala'] as Set)
            javers.commit('another author', user)

            javers.commitShallowDelete('another author', user)


        when:
            def changes = javers.findChanges(QueryBuilder.byInstanceId('bob',DummyUser).build())
            def textChangeLog = javers.processChangeList(changes, new SimpleTextChangeLog())

        then:
            println textChangeLog
            textChangeLog.length() > 0 //it's a demo, not a real test
    }

}
