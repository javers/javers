package org.javers.repository.mongo.cases

import com.github.fakemongo.Fongo
import org.javers.core.JaversBuilder
import org.javers.core.metamodel.annotation.DiffIgnore
import org.javers.core.metamodel.annotation.Value
import org.javers.repository.mongo.MongoRepository
import spock.lang.Shared
import spock.lang.Specification

import javax.persistence.Id
import javax.persistence.OneToMany

/**
 * Decouple cross reference object by DiffIgnore
 *
 * @author hank cp
 */
class CrossReferenceTest extends Specification {

    class CrossReferenceHost {
        @Id long id
        CrossReferenceObjectA a
    }

    class CrossReferenceObjectA {

        @OneToMany
        List<CrossReferenceObjectB> bList

        @OneToMany
        @DiffIgnore
        List<CrossReferenceObjectB> bListIgnored
    }

    public class CrossReferenceObjectB {
        public int value;
        public CrossReferenceObjectA a

        CrossReferenceObjectB(int value, CrossReferenceObjectA a) {
            this.value = value
            this.a = a
        }
    }

    @Shared def mongoDb

    def setup(){
        mongoDb = new Fongo("myDb").getDatabase("test")
    }

    def "should not throw StackOverflowError exception"() {
        given:
        def javers = JaversBuilder.javers()
                .registerJaversRepository(new MongoRepository(mongoDb))
                .build()

        when:
        def host = new CrossReferenceHost()
        host.id = 1
        host.a = new CrossReferenceObjectA()
        host.a.bList = [new CrossReferenceObjectB(1, host.a),
                        new CrossReferenceObjectB(2, host.a),
                        new CrossReferenceObjectB(3, host.a)]

        def commit = javers.commit("author", host)

        then:
        commit
        println commit
    }
}
