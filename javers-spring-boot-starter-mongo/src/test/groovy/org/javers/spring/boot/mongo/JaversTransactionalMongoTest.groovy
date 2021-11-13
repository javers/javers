package org.javers.spring.boot.mongo

import com.github.silaev.mongodb.replicaset.MongoDbReplicaSet
import org.javers.spring.transactions.JaversTransactionalTest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("TransactionalMongo")
class JaversTransactionalMongoTest extends JaversTransactionalTest {
    @Autowired
    MongoDbReplicaSet replicaSet

    @Autowired
    DummyTransactionalService dummyTransactionalService

    def "should rollback a plain insert"() {
        given:
        def doc = dummyTransactionalService.createDocument()

        when:
        dummyTransactionalService.saveAndCatch(doc)

        then:
        !dummyTransactionalService.documentExists(doc)
    }
}
