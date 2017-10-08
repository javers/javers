package org.javers.spring.jpa.connectionprovider

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.example.JaversSpringJpaApplicationConfig
import org.javers.spring.model.DummyObject
import org.javers.spring.repository.DummyAuditedJpaRepository
import org.javers.spring.repository.DummyAuditedRepository
import org.javers.spring.repository.DummyNoAuditJpaRepository
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

class JpaHibernateConnectionProviderTest extends Specification {

    @Shared
    AnnotationConfigApplicationContext context

    @Shared
    Javers javers

    @Shared
    def repository


    def setupSpec() {
        context = new AnnotationConfigApplicationContext(JaversSpringJpaApplicationConfig)
        javers = context.getBean(Javers)
    }

    @Unroll
    def "should use transactional JpaHibernateConnectionProvider with #repositortKind Repository to commit and read objects"() {
        given:
        repository = context.getBean(repositoryClass)
        def o = new DummyObject("some")

        when:
        repository.save(o)
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())

        then:
        true
        snapshots.size() == 1

        where:
        repositortKind <<  ["ordinal","spring-data-crud"]
        repositoryClass << [DummyAuditedRepository, DummyAuditedJpaRepository]
    }

}
