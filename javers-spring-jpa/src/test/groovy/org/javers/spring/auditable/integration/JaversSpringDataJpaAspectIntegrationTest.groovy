package org.javers.spring.auditable.integration

import org.javers.core.Javers
import org.javers.spring.example.JaversSpringJpaApplicationConfig
import org.javers.spring.model.DummyObject
import org.javers.spring.repository.DummyAuditedJpaRepository
import org.javers.spring.repository.DummyNoAuditJpaRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

import static org.javers.repository.jql.QueryBuilder.byInstanceId

@ContextConfiguration(classes = [JaversSpringJpaApplicationConfig])
class JaversSpringDataJpaAspectIntegrationTest extends Specification {
    @Autowired
    ApplicationContext context

    @Autowired
    Javers javers

    @Autowired
    DummyAuditedJpaRepository repository

    @Autowired
    DummyNoAuditJpaRepository noAuditRepository

    def "should commit to JaVers on audited jpaRepository.save(Object)"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)

        then:
        javers.findSnapshots(byInstanceId(o.id, DummyObject).build()).size() == 1
    }

    def "should commit to JaVers on audited jpaRepository.saveAndFlush(object)"() {
        given:
        def o = new DummyObject()

        when:
        repository.saveAndFlush(o)

        then:
        javers.findSnapshots(org.javers.repository.jql.QueryBuilder.byInstanceId(o.id, DummyObject).build()).size() == 1
    }

    def "should commitDelete on audited jpaRepository.delete(object)"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.delete(o)

        then:
        def snapshots = javers.findSnapshots(byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[1].initial
    }

    def "should not commit when any method is called on not audited repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        noAuditRepository.save(o)
        noAuditRepository.delete(o)
        noAuditRepository.findOne(o.id)

        then:
        javers.findSnapshots(byInstanceId(o.id, DummyObject).build()).size() == 0
    }
}
