package org.javers.spring.auditable.integration

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.example.JaversSpringMongoApplicationConfig
import org.javers.spring.repository.mongo.DummyAuditedRepository
import org.javers.spring.model.DummyObject
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author Pawel Szymczyk
 */
class JaversAuditableAspectIntegrationTest extends Specification {

    @Shared
    ApplicationContext context

    @Shared
    Javers javers

    @Shared
    DummyAuditedRepository repository

    def setupSpec() {
        context = new AnnotationConfigApplicationContext(JaversSpringMongoApplicationConfig)
        javers = context.getBean(Javers)
        repository = context.getBean(DummyAuditedRepository)
    }

    def "should commit single argument when method is annotated with @JaversAuditable"() {
        given: "one arg test"
        def o = new DummyObject()

        when:
        repository.save(o)

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build()).size() == 1
    }

    def "should commit few arguments when method is annotated with @JaversAuditable"() {
        given:
        def o1 = new DummyObject()
        def o2 = new DummyObject()

        when: "many args test"
        repository.saveTwo(o1, o2)

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId(o1.id, DummyObject).build()).size() == 1
        javers.findSnapshots(QueryBuilder.byInstanceId(o2.id, DummyObject).build()).size() == 1
    }

    def "should commit iterable argument when method is annotated with @JaversAuditable"() {
        given:
        def objects = [new DummyObject(), new DummyObject()]

        when: "iterable arg test"
        repository.saveAll(objects)

        then:
        objects.each {
            javers.findSnapshots(QueryBuilder.byInstanceId(it.id, DummyObject).build()).size() == 1
        }
    }

    def "should not advice a method from a Repository when no annotation"() {
        given:
        def o = new DummyObject()

        when:
        repository.find(o)

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build()).size() == 0
    }
}
