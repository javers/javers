package org.javers.spring.auditable.integration

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.model.DummyObject
import org.javers.spring.repository.DummyAuditedRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [TestApplicationConfig])
class JaversAuditableDeleteAspectIntegrationTest extends Specification {

    @Autowired
    Javers javers

    @Autowired
    DummyAuditedRepository repository

    def "should commit single argument when method is annotated with @JaversAuditableDelete"() {
        given: "one arg test"
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.delete(o)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[1].initial
    }

    def "should commit few arguments when method is annotated with @JaversAuditableDelete"() {
        given:
        def o1 = new DummyObject()
        def o2 = new DummyObject()

        when: "many args test"
        repository.saveTwo(o1, o2)
        repository.deleteTwo(o1, o2)

        then:
        def snapshots1 = javers.findSnapshots(QueryBuilder.byInstanceId(o1.id, DummyObject).build())
        def snapshots2 = javers.findSnapshots(QueryBuilder.byInstanceId(o2.id, DummyObject).build())

        [snapshots1, snapshots2].each { snapshots ->
            snapshots.size() == 2
            snapshots[0].terminal
            snapshots[1].initial
        }
    }

    def "should commit with properties provided by CommitPropertiesProvider when method is annotated with @JaversAuditableDelete"(){
        given:
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.delete(o)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[0].commitMetadata.properties["key"] == "ok"
        snapshots[1].initial
    }

    def "should commit iterable argument when method is annotated with @JaversAuditableDelete"() {
        given:
        def objects = [new DummyObject(), new DummyObject()]

        when: "iterable arg test"
        repository.saveAll(objects)
        repository.deleteAll(objects)

        then:
        objects.each {
            javers.findSnapshots(QueryBuilder.byInstanceId(it.id, DummyObject).build()).size() == 1

            def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(it.id, DummyObject).build())
            snapshots.size() == 2
            snapshots[0].terminal
            snapshots[1].initial
        }
    }

    def "should commit delete by Id when a method is annotated with @JaversAuditableDelete"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.deleteById(o.id)

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[1].initial
    }

    def "should commit by Ids iterable when a method is annotated with @JaversAuditableDelete"() {
        given:
        def objects = [new DummyObject(), new DummyObject()]

        when: "iterable arg test"
        repository.saveAll(objects)
        repository.deleteAllById(objects.collect { it.id })

        then:
        objects.each {
            javers.findSnapshots(QueryBuilder.byInstanceId(it.id, DummyObject).build()).size() == 1

            def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(it.id, DummyObject).build())
            snapshots.size() == 2
            snapshots[0].terminal
            snapshots[1].initial
        }
    }

    def "should throw the exception if no entity parameter is given when deleting by Id using @JaversAuditableDelete"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.deleteByIdNoClass(o.id)

        then:
        JaversException e = thrown()
        println e
        e.code == JaversExceptionCode.WRONG_USAGE_OF_JAVERS_AUDITABLE_DELETE
    }
}
