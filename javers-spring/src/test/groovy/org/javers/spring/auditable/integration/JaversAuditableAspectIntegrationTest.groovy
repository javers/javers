package org.javers.spring.auditable.integration

import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.example.JaversSpringMongoApplicationConfig
import org.javers.spring.model.DummyObject
import org.javers.spring.repository.DummyAuditedRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = JaversSpringMongoApplicationConfig)
class JaversAuditableAspectIntegrationTest extends Specification {

    @Autowired
    Javers javers

    @Autowired
    DummyAuditedRepository repository

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

    def "should commit with properties provided by CommitPropertiesProvider"(){
        given:
        def o = new DummyObject()

        when:
        repository.save(o)

        then:
        def snapshot = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())[0]
        snapshot.commitMetadata.properties["key"] == "ok"
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
