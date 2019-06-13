package org.javers.spring.boot.mongo

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("dedicated-mongo-host")
class JaversMongoStarterDedicatedMongoByHostTest extends JaversMongoStarterDedicatedMongoTest {

    def "should read dedicated mongo configuration from host"(){
        expect:
        javersProperties.mongodb
        javersProperties.mongodb.host == 'localhost'
        javersProperties.mongodb.port == PORT
        javersProperties.mongodb.database == 'javers-dedicated'
    }
}
