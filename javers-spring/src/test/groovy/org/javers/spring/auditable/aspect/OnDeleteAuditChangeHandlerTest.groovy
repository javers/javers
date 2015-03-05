package org.javers.spring.auditable.aspect

import org.javers.core.Javers
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.integration.DummyObject
import org.springframework.data.repository.core.RepositoryMetadata
import spock.lang.Specification

/**
 * Created by gessnerfl on 21.02.15.
 */
class OnDeleteAuditChangeHandlerTest extends Specification {
    def String AUTHOR_NAME = "foo"
    def javers = Mock(Javers)
    def authorProvider = Stub(AuthorProvider)
    def repositoryMetadata = Mock(RepositoryMetadata)

    def domainObject = new DummyObject()
    def sut = new OnDeleteAuditChangeHandler(javers, authorProvider)

    def setup() {
        authorProvider.provide() >> AUTHOR_NAME

        repositoryMetadata.getDomainType() >> DummyObject
        repositoryMetadata.getIdType() >> String
    }

    def "should commit delete by object"() {
        when:
        sut.handle(repositoryMetadata, domainObject)

        then:
        1 * javers.commitShallowDelete(AUTHOR_NAME, domainObject)

    }

    def "should commit delete by id"() {
        setup:
        def id = UUID.randomUUID().toString()

        when:
        sut.handle(repositoryMetadata, id)

        then:
        1 * javers.commitShallowDeleteById(AUTHOR_NAME, _)
    }

    def "should fail to delete as object type does not match repository type"() {
        when:
        sut.handle(repositoryMetadata, BigDecimal.TEN)

        then:
        def ex = thrown(IllegalArgumentException.class)
        ex.message.startsWith("Domain object or object id expected")
    }
}
