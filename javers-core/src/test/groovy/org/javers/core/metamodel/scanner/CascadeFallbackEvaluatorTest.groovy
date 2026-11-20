package org.javers.core.metamodel.scanner

import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Type

import static org.javers.core.JaversBuilder.javers

/**
 * Unit tests for the fallback evaluators that decide the cascading behavior of
 * properties without an explicit {@code @Cascading} annotation.
 */
class CascadeFallbackEvaluatorTest extends Specification {

    @Shared def javers = javers().build()

    @Shared def entityType = javers.getTypeMapping(SnapshotEntity)
    @Shared def valueObjectType = javers.getTypeMapping(DummyAddress)
    @Shared def listOfEntitiesType = javers.getTypeMapping(listOfEntitiesJavaType())
    @Shared def listOfValueObjectsType = javers.getTypeMapping(listOfValueObjectsJavaType())

    static Type listOfEntitiesJavaType() {
        SnapshotEntity.getDeclaredField("listOfEntities").genericType
    }

    static Type listOfValueObjectsJavaType() {
        SnapshotEntity.getDeclaredField("listOfValueObjects").genericType
    }

    def "NoneFallbackEvaluator should never cascade"() {
        given:
        def evaluator = new CascadeFallbackEvaluator.NoneFallbackEvaluator()

        expect:
        !evaluator.shouldCascade(entityType)
        !evaluator.shouldCascade(valueObjectType)
        !evaluator.shouldCascade(listOfEntitiesType)
        !evaluator.shouldCascade(listOfValueObjectsType)
    }

    def "AllFallbackEvaluator should always cascade"() {
        given:
        def evaluator = new CascadeFallbackEvaluator.AllFallbackEvaluator()

        expect:
        evaluator.shouldCascade(entityType)
        evaluator.shouldCascade(valueObjectType)
        evaluator.shouldCascade(listOfEntitiesType)
        evaluator.shouldCascade(listOfValueObjectsType)
    }

    @Unroll
    def "OnlyValueObjectFallbackEvaluator should cascade only ValueObjects (directly or wrapped in a container)"() {
        given:
        def evaluator = new CascadeFallbackEvaluator.OnlyValueObjectFallbackEvaluator()

        expect:
        evaluator.shouldCascade(type) == expectedCascade

        where:
        type                    | expectedCascade
        valueObjectType         | true
        entityType              | false
        listOfValueObjectsType  | true
        listOfEntitiesType      | false
    }
}
