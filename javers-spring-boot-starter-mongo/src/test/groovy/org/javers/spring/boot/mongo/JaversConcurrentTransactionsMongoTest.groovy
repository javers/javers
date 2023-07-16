package org.javers.spring.boot.mongo

import com.github.silaev.mongodb.replicaset.MongoDbReplicaSet
import org.javers.core.Javers
import org.javers.spring.transactions.UberService
import org.javers.spring.transactions.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SpringBootTest(
        classes = [TestApplication],
        properties = ["javers.commitIdGenerator=random"])
@ActiveProfiles("TransactionalMongo")
class JaversConcurrentTransactionsMongoTest extends Specification {

    @Autowired
    MongoDbReplicaSet replicaSet

    @Autowired
    DummyTransactionalService dummyTransactionalService

    @Autowired
    private Javers javers

    @Autowired
    private UberService uberService

    def "should not produce write conflict on concurrent commits with RANDOM CommitId generator"() {
        given:
        def tasks = [
                { -> javers.commit("author_1", new User()) },
                { -> javers.commit("author_2", new User()) },
                { -> javers.commit("author_3", new User()) },
                { -> javers.commit("author_4", new User()) },
                { -> javers.commit("author_5", new User()) },
        ]
        ExecutorService threadPool = Executors.newFixedThreadPool(tasks.size())

        when:
        def futures = threadPool.invokeAll(tasks)

        then:
        futures.each {
            future -> future.get()
        }
    }
}
