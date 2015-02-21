package org.javers.spring.data.advice

import org.aopalliance.intercept.MethodInvocation
import org.javers.core.Javers
import org.javers.spring.AuthorProvider
import org.javers.spring.data.testdata.DummyObject
import org.objenesis.Objenesis
import org.objenesis.ObjenesisStd
import org.springframework.data.repository.core.RepositoryMetadata
import spock.lang.Specification

import java.lang.reflect.Method

/**
 * Created by gessnerfl on 21.02.15.
 */
class AuditMethodInvocationHandlerFactoryTest extends Specification {

    def invocation = Mock(MethodInvocation.class)

    def Method method

    def javers = Mock(Javers.class)
    def authorProvider = Mock(AuthorProvider.class)
    def repositoryMetadata = Mock(RepositoryMetadata.class)

    def domainObject = new DummyObject()

    def sut = new AuditMethodInvocationHandlerFactory(javers, authorProvider, repositoryMetadata)

    def setup() {
        def objenesis = new ObjenesisStd();
        method = objenesis.getInstantiatorOf(Method.class).newInstance()

        invocation.getMethod() >> method

        authorProvider.provide() >> "foo"

        repositoryMetadata.getDomainType() >> DummyObject.class
        repositoryMetadata.getIdType() >> String.class
    }

    def "Should return save handler"() {
        setup:
        method.name = "save"

        when:
        def handler = sut.createFor(invocation)

        then:
        handler != null
    }

    def "Should return delete handler"() {
        setup:
        method.name = "delete"

        when:
        def handler = sut.createFor(invocation)

        then:
        handler != null
    }

    def "Should fail as method is not a save or delete method"() {
        setup:
        method.getName() >> "foo"

        when:
        sut.createFor(invocation)

        then:
        def e = thrown(IllegalArgumentException.class)
        e.message.startsWith("Save or delete")
    }

    def "Should commit new version"() {
        setup:
        method.name = "save"
        def handler = sut.createFor(invocation)

        when:
        handler.onAfterMethodInvocation(domainObject)

        then:
        1 * javers.commit("foo", domainObject)
    }

    def "Should fail to commit new version as object type does not match repository type"() {
        setup:
        method.name = "save"
        def handler = sut.createFor(invocation)

        when:
        handler.onAfterMethodInvocation(BigDecimal.TEN)

        then:
        def ex = thrown(IllegalArgumentException.class)
        ex.message.startsWith("Domain object expected")
    }

    def "Should commit delete by object"() {
        setup:
        method.name = "delete"
        def handler = sut.createFor(invocation)

        when:
        handler.onAfterMethodInvocation(domainObject)

        then:
        1 * javers.commitShallowDelete("foo", domainObject)

    }

    def "Should commit delete by id"() {
        setup:
        method.name = "delete"
        def handler = sut.createFor(invocation)
        def id = UUID.randomUUID().toString();

        when:
        handler.onAfterMethodInvocation(id)

        then:
        1 * javers.commitShallowDeleteById("foo", _)
    }

    def "Should fail to delete as object type does not match repository type"() {
        setup:
        method.name = "delete"
        def handler = sut.createFor(invocation)

        when:
        handler.onAfterMethodInvocation(BigDecimal.TEN)

        then:
        def ex = thrown(IllegalArgumentException.class)
        ex.message.startsWith("Domain object or object id expected")
    }
}
