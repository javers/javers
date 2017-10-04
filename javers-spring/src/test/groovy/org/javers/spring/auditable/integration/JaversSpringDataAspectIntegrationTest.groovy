package org.javers.spring.auditable.integration

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.example.JaversSpringJpaApplicationConfig
import org.javers.spring.model.DummyObject
import org.javers.spring.repository.jpa.DummyAuditedJpaCrudRepository
import org.javers.spring.repository.jpa.DummyNoAuditJpaCrudRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.dao.EmptyResultDataAccessException
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [JaversSpringJpaApplicationConfig])
class JaversSpringDataAspectIntegrationTest extends Specification {
    @Autowired
    ApplicationContext context

    @Autowired
    Javers javers

    @Autowired
    DummyAuditedJpaCrudRepository repository

    @Autowired
    DummyNoAuditJpaCrudRepository noAuditRepository

    def "should not fail on JaVers aspect when deleting an object which not exists in JaVers repository"(){
        when:
        repository.delete("a")

        then:
        thrown(EmptyResultDataAccessException)

        when:
        repository.delete(new DummyObject(id:"a"))

        then:
        notThrown(Exception)
    }

    def "should create a new version on save via audited repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        repository.save(o)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())

        snapshots.size() == 1
        snapshots[0].initial
        snapshots[0].commitMetadata.properties["key"] == "ok"
        snapshots[0].commitMetadata.author == "unauthenticated"
    }

    def "should create a new version on saveAndFlush via audited repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        repository.saveAndFlush(o)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())

        snapshots.size() == 1
        snapshots[0].initial
        snapshots[0].commitMetadata.properties["key"] == "ok"
        snapshots[0].commitMetadata.author == "unauthenticated"
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
