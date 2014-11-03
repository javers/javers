package org.javers.spring.aspect

import org.aopalliance.intercept.MethodInvocation
import org.javers.core.Javers
import org.javers.core.JaversBuilder
import org.javers.core.model.DummyUser
import spock.lang.Shared
import spock.lang.Specification


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
        javers.getStateHistory("kazik", DummyUser, 100).size() == 1
    }

    def "should commit array of entities"() {
        given:
        def methodInvocation = Stub(MethodInvocation) {
            getArguments() >> [new DummyUser("kazik"), new DummyUser("romek"), new DummyUser("waldek")]
        }

        when:
        javersAdvice.invoke(methodInvocation)

        then:
        javers.getStateHistory("kazik", DummyUser, 100).size() == 1
        javers.getStateHistory("romek", DummyUser, 100).size() == 1
        javers.getStateHistory("waldek", DummyUser, 100).size() == 1
    }

    def "should commit data after proceed"() {
        given:
        def methodInvocation = Stub(MethodInvocation) {
            getArguments() >> [new DummyUser("kazik"), new DummyUser("romek")]
        }

        when:
        javersAdvice.invoke(methodInvocation)

        then:
        javers.getStateHistory("kazik", DummyUser, 100).size() == 1
        javers.getStateHistory("romek", DummyUser, 100).size() == 1
    }
}
