package org.javers.spring.boot.mongo

import org.javers.core.Javers
import org.javers.spring.mongodb.TransactionalMongoRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("TransactionalMongo")
class JaversTransactionalMongoAutoConfigurationTest extends Specification {

    @Autowired
    Javers javers

    def "should auto-configure TransactionalMongoRepository when MongoTransactionManager is available"() {
        expect:
        javers.repository.delegate instanceof TransactionalMongoRepository
    }
}
