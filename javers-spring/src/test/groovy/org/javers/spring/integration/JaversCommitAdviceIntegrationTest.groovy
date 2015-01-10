package org.javers.spring.integration

import org.javers.core.Javers
import org.javers.core.metamodel.object.InstanceIdDTO
import org.junit.Test
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId

/**
 * @author Pawel Szymczyk
 */
class JaversCommitAdviceIntegrationTest extends Specification {

    @Shared
    AnnotationConfigApplicationContext context

    @Shared
    Javers javers

    def setupSpec() {
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        javers = context.getBean(Javers)
    }

    @Test
    def "should advice selected methods from a bean when annotation over method is present"() {
        given:
        def project = new Project(1, "Angular-js");
        def repository = context.getBean(ProjectRepository)

        when:
        repository.save(project);

        then:
        javers.getStateHistory(instanceId("1", Project), 100).size() == 1

        when:
        project.name = "Angular JS"
        repository.update(project)

        then:
        "there should be still only one snapshot, because update() method is not annotated"
        javers.getStateHistory(instanceId("1", Project), 100).size() == 1
    }

    @Test
    def "should advice all methods from a bean when class-level annotation is present"() {
        given:
        def user = new User(1, "John");
        def repository = context.getBean(UserRepository)

        when:
        repository.save(user);

        then:
        javers.getStateHistory(new InstanceIdDTO(User, "1"), 100).size() == 1

        when:
        user.setName("Bob")
        repository.update(user)

        then:
        javers.getStateHistory(new InstanceIdDTO(User, "1"), 100).size() == 2
    }
}
