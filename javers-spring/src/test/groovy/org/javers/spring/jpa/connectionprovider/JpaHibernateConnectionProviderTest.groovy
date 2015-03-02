package org.javers.spring.jpa.connectionprovider

import org.javers.core.Javers
import org.javers.core.metamodel.object.InstanceIdDTO
import org.javers.spring.model.DummyObject
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification

/**
 * @author bartosz walacik
 */
class JpaHibernateConnectionProviderTest extends Specification {

    @Shared
    AnnotationConfigApplicationContext context

    @Shared
    Javers javers

    @Shared
    DummyJpaRepository repository


    def setupSpec() {
        context = new AnnotationConfigApplicationContext(JpaHibernateConnectionProviderApplicationConfig)
        javers = context.getBean(Javers)
        repository = context.getBean(DummyJpaRepository)
    }

    def "should use transactional JpaHibernateConnectionProvider to commit and read objects"() {
        given:
        def o = new DummyObject("some")

        when:
        repository.save(o)
        def snapshots = javers.getStateHistory(new InstanceIdDTO(DummyObject, o.id), 10)

        then:
        snapshots.size() == 1
    }
}
