package org.javers.spring.data.advice

import org.aopalliance.intercept.MethodInvocation
import org.javers.common.collections.Lists
import org.javers.spring.data.testdata.DummyObject
import spock.lang.Specification

/**
 * Created by gessnerfl on 21.02.15.
 */
class JaversSpringDataAdviceTest extends Specification {

    def domainObject = Mock(DummyObject.class)

    def invocation = Mock(MethodInvocation.class)

    def factory = Mock(AuditMethodInvocationHandlerFactory)
    def handler = Mock(AuditMethodInvocationHandler.class)

    def sut = new JaversSpringDataAdvice(factory)

    def setup(){
        factory.createFor(invocation) >> handler
    }

    def "should invoke handler on single object"(){
        setup:
        invocation.getArguments() >> [ domainObject ]

        when:
        sut.invoke(invocation)

        then:
        1 * handler.onAfterMethodInvocation(domainObject)
    }

    def "should invoke handler on multiple objects"(){
        setup:
        invocation.getArguments() >> [Lists.asList(domainObject, domainObject) ]

        when:
        sut.invoke(invocation)

        then:
        2 * handler.onAfterMethodInvocation(domainObject)
    }

    def "should not invoke handler if the number of arguments does not match"(){
        setup:
        invocation.getArguments() >> [domainObject, domainObject]

        when:
        sut.invoke(invocation)

        then:
        0 * handler.onAfterMethodInvocation(domainObject)
    }

}
