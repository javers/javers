package org.javers.spring.auditable.integration

import org.javers.core.Javers
import org.javers.spring.model.DummyObject
import org.javers.spring.repository.DummyAuditedCrudRepository
import org.javers.spring.repository.DummyNoAuditedCrudRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.repository.jql.QueryBuilder.byInstanceId

@ContextConfiguration(classes = [TestApplicationConfig])
class JaversSpringDataAspectIntegrationTest extends Specification {
    @Autowired
    ApplicationContext context

    @Autowired
    Javers javers

    @Autowired
    DummyAuditedCrudRepository repository

    @Autowired
    DummyNoAuditedCrudRepository noAuditRepository

    def "should not fail on JaVers aspect when deleting an object which not exists in JaVers repository"(){
        when:
        repository.deleteById("a")

        then:
        notThrown(Exception)

        when:
        repository.delete(new DummyObject(id:"a"))

        then:
        notThrown(Exception)
    }

    def "should commit to JaVers on audited crudRepository.save(Object)"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)

        then:
        def snapshots = javers.findSnapshots(byInstanceId(o.id, DummyObject).build())

        snapshots.size() == 1
        snapshots[0].initial
        snapshots[0].commitMetadata.properties["key"] == "ok"
        snapshots[0].commitMetadata.author == "unauthenticated"
    }

    def "should commit to JaVers on audited crudRepository.save(Iterable)"() {
        given:
        def o1 = new DummyObject()
        def o2 = new DummyObject()

        when:
        repository.saveAll([o1,o2])

        then:
        javers.findSnapshots(byInstanceId(o1.id, DummyObject).build()).size() == 1
        javers.findSnapshots(byInstanceId(o2.id, DummyObject).build()).size() == 1
    }

    def "should commitDelete on audited crudRepository.delete(object)"() {
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

    @Unroll
    def "should commitDelete on audited CrudRepository.#method ()"() {
        given:
        def o =  new DummyObject()

        when:
        repository.save(o)
        call(repository, o)

        then:
        def snapshots = javers.findSnapshots(byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[1].initial

        where:
        method << ['deleteById', 'delete']
        call << [
                { def repo, def obj -> repo.deleteById(obj.id) },
                { def repo, def obj -> repo.delete(obj) }
        ]

    }

    def "should commitDelete on audited CrudRepository.deleteAll ()"() {
        given:
        def o1 = new DummyObject()
        def o2 = new DummyObject()

        when:
        repository.saveAll([o1, o2])
        repository.deleteAll([o1, o2] as Iterable)

        then:
        def snapshots1 = javers.findSnapshots(byInstanceId(o1.id, DummyObject).build())
        def snapshots2 = javers.findSnapshots(byInstanceId(o2.id, DummyObject).build())

        [snapshots1, snapshots2].each { snapshots ->
            snapshots.size() == 2
            snapshots[0].terminal
            snapshots[1].initial
        }
    }

    def "should not commit when finder is called on audited repository"() {
        setup:
        def o =  new DummyObject("foo")

        when:
        repository.save(o)
        def result = repository.findById(o.id)

        then:
        result != null
        def snapshots = javers.findSnapshots(byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 1
        snapshots[0].initial
    }

    def "should commit on update via audited crudRepository.save()"() {
        setup:
        def o = new DummyObject()

        when:
        repository.save(o)
        o.name = "a"
        repository.save(o)

        then:
        def snapshots = javers.findSnapshots(byInstanceId(o.id, DummyObject).build())
        snapshots.size() == 2
        !snapshots[0].initial
        snapshots[1].initial
    }

    def "should not commit when any method is called on not audited repository"() {
        setup:
        def o = new DummyObject("foo")

        when:
        noAuditRepository.save(o)
        noAuditRepository.delete(o)
        noAuditRepository.findById(o.id)

        then:
        javers.findSnapshots(byInstanceId(o.id, DummyObject).build()).size() == 0
    }
}
