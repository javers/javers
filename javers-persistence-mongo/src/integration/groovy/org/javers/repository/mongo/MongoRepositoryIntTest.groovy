package org.javers.repository.mongo

import com.mongodb.MongoClient
import org.javers.core.Javers
import org.javers.core.JaversTestBuilder
import spock.lang.Specification

/**
 * @author pawel szymczyk
 */
class MongoRepositoryIntTest extends Specification {

    def "should save commit"() {

        given:
        MongoClient mongoClient = new MongoClient( "localhost" , 27017 );

        MongoRepository mongoRepository = new MongoRepository(mongoClient.getDB("myDB"))
        JaversTestBuilder javersTestBuilder = JaversTestBuilder.javersTestAssembly(mongoRepository)

        mongoRepository.setJsonConverter(javersTestBuilder.jsonConverter);
        Javers javers = javersTestBuilder.javers()

        DummyProduct dummyProduct = new DummyProduct(1, "Candy")

        when:
        javers.commit("charlie", dummyProduct)

        then:
        def history = javers.getStateHistory(1, DummyProduct, 1)
        history.size() == 1
    }
}