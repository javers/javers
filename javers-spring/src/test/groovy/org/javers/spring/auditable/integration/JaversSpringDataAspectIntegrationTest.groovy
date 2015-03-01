package org.javers.spring.auditable.integration

import org.javers.core.Javers
import org.javers.core.metamodel.object.InstanceIdDTO
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by gessnerfl on 22.02.15.
 */
class JaversSpringDataAspectIntegrationTest extends Specification {
    @Shared
    AnnotationConfigApplicationContext context

    @Shared
    Javers javers

    @Shared
    DummyAuditedCrudRepository repository

    @Shared
    DummyNoAuditCrudRepository noAuditRepository

    def setupSpec() {
        context = new AnnotationConfigApplicationContext(JaversAuditableAspectApplicationConfig)
        javers = context.getBean(Javers)
        repository = context.getBean(DummyAuditedCrudRepository)
        noAuditRepository = context.getBean(DummyNoAuditCrudRepository)
    }

    def "should create a new version on create via audited repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        repository.save(o)

        then:
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject, o.id), 10)
        snapshots.size() == 1
        snapshots[0].initial
        snapshots[0].commitMetadata.author == "author"
    }

    def "should create a new version when creating few objects via audited repository"() {
        setup:
        def objects = [new DummyObject("foo"), new DummyObject("foo")]

        when:
        repository.save(objects)

        then:
        objects.each {
            def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject, it.id), 10)
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
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject, o.id), 10)
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
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject, o.id), 10)
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
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject, o.id), 10)
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
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject, o.id), 10)
        snapshots.size() == 1
        snapshots[0].initial
    }


    def "should not create a new version on save via normal repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        noAuditRepository.save(o)

        then:
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject, o.id), 10)
        snapshots.empty
    }

    def "should not create a new version on delete via normal repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        noAuditRepository.save(o)
        noAuditRepository.delete(o)

        then:
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject, o.id), 10)
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
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject, o.id), 10)
        snapshots.empty
    }
}
