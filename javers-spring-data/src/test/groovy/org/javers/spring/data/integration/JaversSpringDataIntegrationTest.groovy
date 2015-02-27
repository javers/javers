package org.javers.spring.data.integration

import org.javers.core.Javers
import org.javers.core.metamodel.object.InstanceIdDTO
import org.javers.spring.AuthorProvider
import org.javers.spring.data.integration.testdata.DummyNoAuditRepository
import org.javers.spring.data.integration.testdata.DummyObject
import org.javers.spring.data.integration.testdata.DummyRepository
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification

/**
 * Created by gessnerfl on 22.02.15.
 */
class JaversSpringDataIntegrationTest extends Specification {
    @Shared
    AnnotationConfigApplicationContext context
    @Shared
    Javers javers
    @Shared
    TestAuthorProvider authorProvider

    @Shared
    DummyRepository repository;
    @Shared
    DummyNoAuditRepository noAuditRepository;

    def setupSpec() {
        context = new AnnotationConfigApplicationContext(SpringApplicationConfig.class);
        javers = context.getBean(Javers)
        authorProvider = (TestAuthorProvider) context.getBean(AuthorProvider.class)
        repository = context.getBean(DummyRepository.class);
        noAuditRepository = context.getBean(DummyNoAuditRepository.class)
    }

    def "should create a new version on create via audit enabled repository"() {
        setup:
        DummyObject o = createNewDummyObject("foo")

        when:
        repository.save(o);

        then:
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject.class, o.id), 10)
        snapshots.size() == 1
        snapshots.get(0).isInitial()
    }

    def "should create a new version on update via audit enabled repository"() {
        setup:
        DummyObject o = createNewDummyObject("foo")

        when:
        repository.save(o);
        o.name = "bar"
        repository.save(o)

        then:
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject.class, o.id), 10)
        snapshots.size() == 2
        !snapshots.get(0).isInitial()
        !snapshots.get(0).isTerminal()
        snapshots.get(1).isInitial()
    }

    def "should create a new version on delete by object via audit enabled repository"() {
        setup:
        DummyObject o = createNewDummyObject("foo")

        when:
        repository.save(o);
        repository.delete(o)

        then:
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject.class, o.id), 10)
        snapshots.size() == 2
        snapshots.get(0).isTerminal()
        snapshots.get(1).isInitial()
    }

    def "should create a new version on delete by id via audit enabled repository"() {
        setup:
        DummyObject o = createNewDummyObject("foo")

        when:
        repository.save(o);
        repository.delete(o.id)

        then:
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject.class, o.id), 10)
        snapshots.size() == 2
        snapshots.get(0).isTerminal()
        snapshots.get(1).isInitial()
    }

    def "should not create new version when finder is executed on audit enabled repository"() {
        setup:
        DummyObject o = createNewDummyObject("foo")

        when:
        repository.save(o);
        Object result = repository.findOne(o.id)

        then:
        result != null
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject.class, o.id), 10)
        snapshots.size() == 1
        snapshots.get(0).isInitial()
    }


    def "should not create a new version on save via default repository"() {
        setup:
        DummyObject o = createNewDummyObject("foo")

        when:
        noAuditRepository.save(o);

        then:
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject.class, o.id), 10)
        snapshots.isEmpty()
    }

    def "should not create a new version on delete via default repository"() {
        setup:
        DummyObject o = createNewDummyObject("foo")

        when:
        noAuditRepository.save(o);
        noAuditRepository.delete(o)

        then:
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject.class, o.id), 10)
        snapshots.isEmpty()
    }

    def "should not create new version when finder is executed on default repository"() {
        setup:
        DummyObject o = createNewDummyObject("foo")

        when:
        noAuditRepository.save(o);
        def result = noAuditRepository.findOne(o.id)

        then:
        result != null
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject.class, o.id), 10)
        snapshots.isEmpty()
    }

    def createNewDummyObject(String name) {
        def o = new DummyObject()
        o.id = UUID.randomUUID().toString()
        o.name = name
        return o
    }
}
