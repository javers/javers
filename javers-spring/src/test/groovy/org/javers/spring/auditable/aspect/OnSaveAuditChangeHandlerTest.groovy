package org.javers.spring.auditable.aspect

import org.javers.core.Javers
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.model.DummyObject
import org.springframework.data.repository.core.RepositoryMetadata
import spock.lang.Specification

/**
 * Created by gessnerfl on 21.02.15.
 */
class OnSaveAuditChangeHandlerTest extends Specification {
    def AUTHOR_NAME = "foo"
    def javers = Mock(Javers.class)
    def authorProvider = Stub(AuthorProvider)
    def repositoryMetadata = Mock(RepositoryMetadata)

    def domainObject = new DummyObject()

    def sut = new OnSaveAuditChangeHandler(javers, authorProvider)

    def setup() {
        authorProvider.provide() >> AUTHOR_NAME

        repositoryMetadata.getDomainType() >> DummyObject
        repositoryMetadata.getIdType() >> String
    }

    def "should commit new version"() {
        when:
        sut.handle(repositoryMetadata, domainObject)

        then:
        1 * javers.commit(AUTHOR_NAME, domainObject)

    }
}
