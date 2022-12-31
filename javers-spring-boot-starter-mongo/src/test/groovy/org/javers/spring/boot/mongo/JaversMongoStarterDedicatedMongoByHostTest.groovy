package org.javers.spring.boot.mongo

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource

class JaversMongoStarterDedicatedMongoByHostTest extends JaversMongoStarterDedicatedMongoTest {

    @Autowired
    JaversMongoProperties javersProperties

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("javers.mongodb.host", () -> 'localhost')
        registry.add("javers.mongodb.port", () -> mongoPort)
        registry.add("javers.mongodb.database", () -> 'javers-dedicated')
    }

    def "should read dedicated mongo configuration from javers Spring config props: host, port, and database"(){
        expect:
        javersProperties.mongodb
        javersProperties.mongodb.host == 'localhost'
        javersProperties.mongodb.port == mongoPort
        javersProperties.mongodb.database == 'javers-dedicated'
    }
}
