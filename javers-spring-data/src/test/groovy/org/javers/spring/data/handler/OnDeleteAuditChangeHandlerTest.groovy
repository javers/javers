package org.javers.spring.data.handler

import org.javers.core.Javers
import org.javers.spring.AuthorProvider
import org.javers.spring.data.integration.testdata.DummyObject
import org.springframework.data.repository.core.RepositoryMetadata
import spock.lang.Specification

/**
 * Created by gessnerfl on 21.02.15.
 */
class OnDeleteAuditChangeHandlerTest extends Specification {
    public static final String AUTHOR_NAME = "foo"
    def javers = Mock(Javers.class)
    def authorProvider = Mock(AuthorProvider.class)
    def repositoryMetadata = Mock(RepositoryMetadata.class)

    def domainObject = new DummyObject()

    def sut = new OnDeleteAuditChangeHandler(javers, authorProvider)

    def setup() {
        authorProvider.provide() >> AUTHOR_NAME

        repositoryMetadata.getDomainType() >> DummyObject.class
        repositoryMetadata.getIdType() >> String.class
    }

    def "Should commit delete by object"() {
        when:
        sut.handle(repositoryMetadata, domainObject)

        then:
        1 * javers.commitShallowDelete(AUTHOR_NAME, domainObject)

    }

    def "Should commit delete by id"() {
        setup:
        def id = UUID.randomUUID().toString();

        when:
        sut.handle(repositoryMetadata, id)

        then:
        1 * javers.commitShallowDeleteById(AUTHOR_NAME, _)
    }

    def "Should fail to delete as object type does not match repository type"() {
        when:
        sut.handle(repositoryMetadata, BigDecimal.TEN)

        then:
        def ex = thrown(IllegalArgumentException.class)
        ex.message.startsWith("Domain object or object id expected")
    }
}
