package org.javers.core.changelog

import org.javers.core.JaversBuilder
import org.javers.core.model.DummyUser
import spock.lang.Specification

import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId

/**
 * @author bartosz walacik
 */
class ChangeListTraverserIntegrationTest extends Specification {

    def "should call user's callback methods while iterating over change list "() {
        given:
        def javers = JaversBuilder.javers().build()

        def user = new DummyUser('bob', 'Dijk')
        javers.commit("some author", user)

        user.setAge(18)
        user.setSex(DummyUser.Sex.MALE)
        javers.commit('some author', user)

        javers.commitDelete('some author', user)
        def callbackMock = Mock(ChangeProcessor)

        when:
        def changes = javers.getChangeHistory(instanceId('bob',DummyUser),10)
        javers.processChangeList(changes, callbackMock)

        then:
        with(callbackMock) {
            1 * beforeChangeList()

            3 * onCommit(_)
            4 * beforeChange(_)
            4 * afterChange(_)

            1 * onObjectRemoved(_)

            1 * onAffectedObject(_)
            2 * onPropertyChange(_)
            2 * onValueChange(_)

            1 * onNewObject(_)
            1 * afterChangeList()

            1 * result()

            0 * _ //and no others interactions
        }
    }
}
