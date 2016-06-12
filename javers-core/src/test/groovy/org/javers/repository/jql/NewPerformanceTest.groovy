package org.javers.repository.jql

import org.javers.core.Javers
import spock.lang.Ignore
import spock.lang.Specification

import java.math.RoundingMode

@Ignore
class NewPerformanceTest extends Specification {

    static int n = 10000
    Javers javers
    def start

    def "should init database - insert and updates"() {
        given:
        clearDatabase()
        start()

        when:
        n.times {
            def root = NewPerformanceEntity.produce(it * 100, 2)
            javers.commit("author", root, [os: "android", country: "pl"])

            root.change()
            javers.commit("author", root, [os: "a" + it, lang: "pl", country: "de"])

            commitDatabase()
        }

        then:
        stop(n)
    }

    def "should query - standard queries"() {
        given:
        start()

        when:
        javers.findSnapshots(QueryBuilder.byClass(NewPerformanceEntity).limit(100).build()).size() == 100
        javers.findSnapshots(QueryBuilder.byClass(MigrationValueObject).limit(100).build()).size() == 100
        javers.findSnapshots(QueryBuilder.byClass(AnotherValueObject).limit(100).build()).size() == 100
        javers.findSnapshots(QueryBuilder.byValueObject(NewPerformanceEntity, 'vo').limit(100).build()).size() == 100
        javers.findSnapshots(QueryBuilder.byValueObject(NewPerformanceEntity, 'anotherVo').limit(100).build()).size() == 100

        def n = 30
        n.times {
            def id = n * 100
            javers.findSnapshots(QueryBuilder.byInstanceId(id, NewPerformanceEntity).build()).size() == 2
            javers.findSnapshots(QueryBuilder.byValueObjectId(id, NewPerformanceEntity, 'vo').build()).size() == 2
            javers.findSnapshots(QueryBuilder.byValueObjectId(id, NewPerformanceEntity, 'anotherVo').build()).size() == 2
        }

        then:
        stop(n * 3 + 5)
    }

    def "should query - new query by property"() {
        given:
        start()

        when:
        int n = 10

        n.times {
            assert javers.findSnapshots(QueryBuilder.byClass(NewPerformanceEntity)
                    .withCommitProperty("os", "a" + it)
                    .build()).size() == 3
            assert javers.findSnapshots(QueryBuilder.byClass(AnotherValueObject)
                    .withCommitProperty("lang", "pl")
                    .withCommitProperty("os", "a" + it)
                    .build()).size() == 3
            assert javers.findSnapshots(QueryBuilder.byValueObject(NewPerformanceEntity, "vo")
                    .withCommitProperty("country", "de")
                    .withCommitProperty("lang", "pl")
                    .withCommitProperty("os", "a" + it)
                    .build()).size() == 3
        }

        then:
        stop(n * 3)
    }

    void start() {
        start = System.currentTimeMillis()
    }

    boolean stop(int times) {
        def stop = System.currentTimeMillis()

        def opAvg = (stop - start) / times

        println "total time: " + round(stop - start) + " ms"
        println "op avg:     " + round(opAvg) + " ms"

        true
    }

    String round(def what) {
        new BigDecimal(what).setScale(2, RoundingMode.HALF_UP).toString()
    }

    void clearDatabase() {
    }

    void commitDatabase() {
    }
}
