package org.javers.spring.aspect

import org.aopalliance.intercept.MethodInvocation
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.model.DummyUser
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId

/**
 * @author Pawel Szymczyk
 */
class JaversAdviceTest extends Specification {

    @Shared
    Javers javers

    @Shared
    JaversAdvice javersAdvice;

    def setup() {
        javers = JaversBuilder.javers().build()
        javersAdvice = new JaversAdvice(javers, "author")
    }

    def "should commit single entity"() {
        given:
        def methodInvocation = Stub(MethodInvocation) {
            getArguments() >> [new DummyUser("kazik")]
        }

        when:
        javersAdvice.invoke(methodInvocation)

        then:
        javers.getStateHistory(instanceId("kazik", DummyUser), 100).size() == 1
    }

    def "should commit array of entities"() {
        given:
        def methodInvocation = Stub(MethodInvocation) {
            getArguments() >> [[new DummyUser("kazik"), new DummyUser("romek"), new DummyUser("waldek")]]
        }

        when:
        javersAdvice.invoke(methodInvocation)

        then:
        javers.getStateHistory(instanceId("kazik", DummyUser), 100).size() == 1
        javers.getStateHistory(instanceId("romek", DummyUser), 100).size() == 1
        javers.getStateHistory(instanceId("waldek", DummyUser), 100).size() == 1
    }
}
