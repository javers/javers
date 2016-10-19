package org.javers.repository.mongo

import com.github.fakemongo.Fongo
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.repository.mongo.model.CrossReferenceHost
import org.javers.repository.mongo.model.CrossReferenceObjectA
import org.javers.repository.mongo.model.CrossReferenceObjectB
import spock.lang.Shared
import spock.lang.Specification

/**
 * Decouple cross reference object by DiffIgnore
 *
 * @author hank cp
 */
class CrossReferenceTest extends Specification {

    @Shared def mongoDb

    def setup(){
        mongoDb = new Fongo("myDb").getDatabase("test")
    }

    def "should throw StackOverflowError exception"() {
        given:
        def javers = JaversBuilder.javers()
                .registerEntity(new EntityDefinition(CrossReferenceHost, "id"))
                .registerJaversRepository(new MongoRepository(mongoDb))
                .build()

        when:
        def host = new CrossReferenceHost()
        host.id = 1;
        host.a = new CrossReferenceObjectA()
        host.a.bList = new ArrayList<>()
        host.a.bList.add(new CrossReferenceObjectB(1, host.a))
        host.a.bList.add(new CrossReferenceObjectB(2, host.a))
        host.a.bList.add(new CrossReferenceObjectB(3, host.a))

        javers.commit("author", host)

        then:
            thrown StackOverflowError
    }

    def "should not throw StackOverflow exception"() {
        given:
        def javers = JaversBuilder.javers()
                .registerEntity(new EntityDefinition(CrossReferenceHost, "id"))
                .registerJaversRepository(new MongoRepository(mongoDb))
                .build()

        when:
        def host = new CrossReferenceHost()
        host.id = 1;
        host.a = new CrossReferenceObjectA()
        host.a.bListIgnored = new ArrayList<>()
        host.a.bListIgnored.add(new CrossReferenceObjectB(1, host.a))
        host.a.bListIgnored.add(new CrossReferenceObjectB(2, host.a))
        host.a.bListIgnored.add(new CrossReferenceObjectB(3, host.a))

        def commit = javers.commit("author", host)

        then:
        commit != null
    }
}
