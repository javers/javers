package org.javers.spring.auditable.integration

import org.javers.repository.mongo.DockerizedMongoContainer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest(classes = [TestApplicationConfig])
@ActiveProfiles('test')
@Testcontainers
abstract class BaseSpecification extends Specification {
    @Shared
    static DockerizedMongoContainer dockerizedMongoContainer = new DockerizedMongoContainer()

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", () -> dockerizedMongoContainer.replicaSetUrl)
    }
}
