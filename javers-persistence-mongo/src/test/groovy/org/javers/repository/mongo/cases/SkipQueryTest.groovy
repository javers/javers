package org.javers.repository.mongo.cases


import com.mongodb.client.MongoDatabase
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.Entity
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.ValueObject
import org.javers.repository.jql.QueryBuilder
import org.javers.repository.mongo.BaseMongoTest
import org.javers.repository.mongo.MongoRepository


/**
 * @author: antongub
 */
class SkipQueryTest extends BaseMongoTest {

    @Entity
    class Customer {
        @Id
        String id

        Configuration conf = new Configuration()

        Customer(id) {
            this.id = id
        }
    }

    @ValueObject
    class Configuration {
        String currency = "EUR"
    }


    def "should return two different snapshot ids"() {
        given:
        Customer customer1 = new Customer("1")
        Customer customer2 = new Customer("2")

        MongoDatabase mongo = mongoClient.getDatabase("test")
        def repo = new MongoRepository(mongo)
        def javers = JaversBuilder.javers().registerJaversRepository(repo).build()

        when:
        javers.commit("author", customer1)
        javers.commit("author", customer2)

        then:
        def query1 = QueryBuilder.anyDomainObject().limit(1).skip(0).build()
        def query2 = QueryBuilder.anyDomainObject().limit(1).skip(1).build()
        def snapshot1Id = javers.findSnapshots(query1)[0].globalId
        def snapshot2Id = javers.findSnapshots(query2)[0].globalId
        snapshot1Id != snapshot2Id
    }
}
