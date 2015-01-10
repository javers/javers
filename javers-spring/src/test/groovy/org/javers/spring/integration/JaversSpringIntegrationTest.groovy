package org.javers.spring.integration

import org.javers.core.Javers
import org.javers.core.metamodel.object.InstanceIdDTO
import org.javers.spring.integration.domain.Project
import org.javers.spring.integration.domain.User
import org.javers.spring.integration.repositories.ProjectRepository
import org.javers.spring.integration.repositories.UserRepository
import org.junit.Test
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import spock.lang.Shared
import spock.lang.Specification

import static org.javers.core.metamodel.object.InstanceIdDTO.instanceId

/**
 * @author Pawel Szymczyk
 */
class JaversSpringIntegrationTest extends Specification {

    @Shared
    AnnotationConfigApplicationContext context

    @Shared
    Javers javers

    def setupSpec() {
        context = new AnnotationConfigApplicationContext(ApplicationConfig.class);
        javers = context.getBean(Javers)
    }

    @Test
    def "should follow only annotated methods"() {
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
        javers.getStateHistory(instanceId("1", Project), 100).size() == 1
    }

    @Test
    def "should follow all methods from repository"() {
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
