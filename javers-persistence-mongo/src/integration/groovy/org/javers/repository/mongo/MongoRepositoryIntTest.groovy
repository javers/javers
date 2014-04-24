package org.javers.repository.mongo

import org.javers.core.Javers
import org.javers.core.JaversBuilder
import spock.lang.Specification

/**
 * @author pawel szymczyk
 */
class MongoRepositoryIntTest extends Specification {

    def "should save commit"() {

        given:
        Javers javers = JaversBuilder.javers()
                .registerEntity(DummyProduct)
                .registerJaversRepository(new MongoRepository())
                .build()

        DummyProduct dummyProduct = new DummyProduct(1, "Candy")

        when:
        javers.commit("charlie", dummyProduct)

        then:
        def history = javers.getStateHistory(1, DummyProduct, 1)
        history.size() == 1
    }
}