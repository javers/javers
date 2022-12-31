package org.javers.spring.boot.mongo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

class JaversMongoStarterDedicatedMongoByUriTest extends JaversMongoStarterDedicatedMongoTest {

    @Autowired
    JaversMongoProperties javersProperties

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("javers.mongodb.uri", () -> "mongodb://localhost:$mongoPort/javers-dedicated")
    }

    def "should read dedicated mongo configuration from javers Spring config URI property"(){
        expect:
        javersProperties.mongodb
        javersProperties.mongodb.uri == "mongodb://localhost:$mongoPort/javers-dedicated"
    }
}
