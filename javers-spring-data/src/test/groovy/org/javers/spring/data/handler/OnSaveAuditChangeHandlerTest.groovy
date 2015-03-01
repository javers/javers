package org.javers.spring.data.handler

import org.javers.core.Javers
import org.javers.spring.AuthorProvider
import org.javers.spring.data.integration.DummyObject
import org.springframework.data.repository.core.RepositoryMetadata
import spock.lang.Specification

/**
 * Created by gessnerfl on 21.02.15.
 */
class OnSaveAuditChangeHandlerTest extends Specification {
    public static final String AUTHOR_NAME = "foo"
    def javers = Mock(Javers.class)
    def authorProvider = Mock(AuthorProvider.class)
    def repositoryMetadata = Mock(RepositoryMetadata.class)

    def domainObject = new DummyObject()

    def sut = new OnSaveAuditChangeHandler(javers, authorProvider)

    def setup() {
        authorProvider.provide() >> AUTHOR_NAME

        repositoryMetadata.getDomainType() >> DummyObject.class
        repositoryMetadata.getIdType() >> String.class
    }

    def "Should commit new version"() {
        when:
        sut.handle(repositoryMetadata, domainObject)

        then:
        1 * javers.commit(AUTHOR_NAME, domainObject)

    }

    def "Should fail to commit new version as data is not a valid domain object"() {
        when:
        sut.handle(repositoryMetadata, "foo")

        then:
        def ex = thrown(IllegalArgumentException.class)
        ex.message.startsWith("Domain object expected")

        0 * javers.commit(_, _)
    }
}
