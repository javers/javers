package org.javers.spring.auditable.integration

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.example.JaversSpringJpaApplicationConfig
import org.javers.spring.model.DummyObject
import org.javers.spring.repository.jpa.DummyAuditedJpaCrudRepository
import org.javers.spring.repository.jpa.DummyNoAuditJpaCrudRepository
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by gessnerfl on 22.02.15.
 */
class JaversSpringDataAspectIntegrationTest extends Specification {
    @Shared
    ApplicationContext context

    @Shared
    Javers javers

    @Shared
    DummyAuditedJpaCrudRepository repository

    @Shared
    DummyNoAuditJpaCrudRepository noAuditRepository

    def setupSpec() {
        context = new AnnotationConfigApplicationContext(JaversSpringJpaApplicationConfig)
        javers = context.getBean(Javers)
        repository = context.getBean(DummyAuditedJpaCrudRepository)
        noAuditRepository = context.getBean(DummyNoAuditJpaCrudRepository)
    }

    def "should create a new version on create via audited repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        repository.save(o)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())

        snapshots.size() == 1
        snapshots[0].initial
        snapshots[0].commitMetadata.author == "unknown"
    }

    def "should create a new version when creating few objects via audited repository"() {
        setup:
        def objects = [new DummyObject("foo"), new DummyObject("foo")]

        when:
        repository.save(objects)

        then:
        objects.each {
            def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(it.id, DummyObject).build())
            snapshots.size() == 1
            snapshots[0].initial
        }
    }


    def "should create a new version on update via audited repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        repository.save(o)
        o.name = "bar"
        repository.save(o)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        !snapshots[0].initial
        snapshots[1].initial
    }

    def "should create a new version on delete using object instance via audited repository"() {
        setup:
        def o =  new DummyObject("foo")

        when:
        repository.save(o)
        repository.delete(o)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[1].initial
    }

    def "should create a new version on delete using object id via audited repository"() {
        setup:
        def o =  new DummyObject("foo")

        when:
        repository.save(o)
        repository.delete(o.id)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[1].initial
    }

    def "should not create new version when finder is executed on audit enabled repository"() {
        setup:
        def o =  new DummyObject("foo")

        when:
        repository.save(o)
        def result = repository.findOne(o.id)

        then:
        result != null
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 1
        snapshots[0].initial
    }


    def "should not create a new version on save via normal repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        noAuditRepository.save(o)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.empty
    }

    def "should not create a new version on delete via normal repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        noAuditRepository.save(o)
        noAuditRepository.delete(o)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.empty
    }

    def "should not create new version when finder is executed on normal repository"() {
        setup:
        def o =  new DummyObject("foo")

        when:
        noAuditRepository.save(o)
        def result = noAuditRepository.findOne(o.id)

        then:
        result != null
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.empty
    }
}
