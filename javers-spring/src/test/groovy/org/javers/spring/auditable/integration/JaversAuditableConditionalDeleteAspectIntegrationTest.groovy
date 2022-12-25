package org.javers.spring.auditable.integration

import org.javers.common.exception.JaversException
import org.javers.common.exception.JaversExceptionCode
import org.javers.core.Javers
import org.javers.repository.jql.QueryBuilder
import org.javers.spring.model.DummyObject
import org.javers.spring.repository.DummyAuditedRepository
import org.springframework.beans.factory.annotation.Autowired

class JaversAuditableConditionalDeleteAspectIntegrationTest extends BaseSpecification {

    @Autowired
    Javers javers

    @Autowired
    DummyAuditedRepository repository

    def "should commit returned entity when method is annotated with @JaversAuditableConditionalDelete"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.deleteByNameResult = [o]
        repository.deleteOneByName("dummy")

        then:
        def snapshots = javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build())

        snapshots.size() == 2
        snapshots[0].terminal
        snapshots[1].initial
    }

    def "should commit returned entities when method is annotated with @JaversAuditableConditionalDelete"() {
        given:
        def o1 = new DummyObject()
        def o2 = new DummyObject()
        def o3 = new DummyObject()

        when: "many args test"
        repository.saveAll([o1, o2, o3])
        repository.deleteByNameResult = [o1, o2]
        repository.deleteByName("dummy")

        then:
        def snapshots1 = javers.findSnapshots(QueryBuilder.byInstanceId(o1.id, DummyObject).build())
        def snapshots2 = javers.findSnapshots(QueryBuilder.byInstanceId(o2.id, DummyObject).build())
        def snapshots3 = javers.findSnapshots(QueryBuilder.byInstanceId(o3.id, DummyObject).build())

        [snapshots1, snapshots2].each { snapshots ->
            snapshots.size() == 2
            snapshots[0].terminal
            snapshots[1].initial
        }

        snapshots3.size() == 1
        snapshots3[0].initial
    }

    def "should throw the exception if no entity parameter is given when deleting by Id using @JaversAuditableDelete"() {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.deleteByNameWrongReturnType(o.id)

        then:
        JaversException e = thrown()
        println e
        e.code == JaversExceptionCode.WRONG_USAGE_OF_JAVERS_AUDITABLE_CONDITIONAL_DELETE
    }

    def "should not delete when null is returned" () {
        given:
        def o = new DummyObject()

        when:
        repository.save(o)
        repository.deleteByNameReturnNull(o.id)

        then:
        javers.findSnapshots(QueryBuilder.byInstanceId(o.id, DummyObject).build()).size() == 1
    }
}
