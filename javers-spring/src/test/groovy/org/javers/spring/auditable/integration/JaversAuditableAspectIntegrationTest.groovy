package org.javers.spring.auditable.integration

import org.javers.core.Javers
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId

/**
 * @author Pawel Szymczyk
 */
class JaversAuditableAspectIntegrationTest extends Specification {

    @Shared
    AnnotationConfigApplicationContext context

    @Shared
    Javers javers

    @Shared
    DummyAuditedRepository repository

    def setupSpec() {
        context = new AnnotationConfigApplicationContext(JaversAuditableAspectApplicationConfig)
        javers = context.getBean(Javers)
        repository = context.getBean(DummyAuditedRepository)
    }

    def "should commit single argument when method is annotated with @JaversAuditable"() {
        given: "one arg test"
        def o = new DummyObject()

        when:
        repository.save(o)

        then:
        javers.getStateHistory(instanceId(o.id, DummyObject), 10).size() == 1
    }

    def "should commit few arguments when method is annotated with @JaversAuditable"() {
        given:
        def o1 = new DummyObject()
        def o2 = new DummyObject()

        when: "many args test"
        repository.saveTwo(o1, o2)

        then:
        javers.getStateHistory(instanceId(o1.id, DummyObject), 10).size() == 1
        javers.getStateHistory(instanceId(o2.id, DummyObject), 10).size() == 1
    }

    def "should commit iterable argument when method is annotated with @JaversAuditable"() {
        given:
        def objects = [new DummyObject(), new DummyObject()]

        when: "iterable arg test"
        repository.saveAll(objects)

        then:
        objects.each {
            javers.getStateHistory(instanceId(it.id, DummyObject), 10).size() == 1
        }
    }

    def "should not advice a method from a Repository when no annotation"() {
        given:
        def o = new DummyObject()

        when:
        repository.find(o)

        then:
        javers.getStateHistory(instanceId(o.id, DummyObject), 10).size() == 0
    }
}
