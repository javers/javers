package org.javers.spring.boot.mongo

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("dedicated-mongo-uri")
class JaversMongoAutoConfigurationDedicatedMongoByUriTest extends JaversMongoAutoConfigurationDedicatedMongoTest {

    def "should read dedicated mongo configuration from URI"(){
        expect:
        javersProperties.mongodb
        javersProperties.mongodb.uri == "mongodb://localhost:${PORT}/javers-dedicated"
    }
}
