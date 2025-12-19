package org.javers.spring.boot.mongo

import org.javers.repository.mongo.DockerizedMongoContainer
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@Testcontainers
abstract class BaseSpecification extends Specification {
    @Shared
    static DockerizedMongoContainer dockerizedMongoContainer = new DockerizedMongoContainer()

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.mongodb.uri", () -> dockerizedMongoContainer.replicaSetUrl)
    }

    static int getMongoPort() {
        dockerizedMongoContainer.mongoPort
    }
}
