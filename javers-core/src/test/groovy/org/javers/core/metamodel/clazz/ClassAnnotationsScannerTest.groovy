package org.javers.core.metamodel.clazz

import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author bartosz walacik
 */
class ClassAnnotationsScannerTest extends Specification {

    def scanner = new ClassAnnotationsScanner()

    @Unroll
    def "should map to ValueObject when no annotation found"() {
        when:
        def result = scanner.scanAndInfer(NotAnnotatedClass)

        then:
        result instanceof ValueObjectDefinition
    }

    @Unroll
    def "should map #annotation.name to Entity"() {

        when:
        def result = scanner.scanAndInfer(classToScan)

        then:
        result instanceof EntityDefinition

        where:
        annotation << [javax.persistence.Entity,
                       javax.persistence.MappedSuperclass,
                       org.javers.core.metamodel.annotation.Entity]
        classToScan << [JpaEntity, JpaMappedSuperclass, JaversEntity]
    }

    @Unroll
    def "should map #annotation.name to ValueObject"() {

        when:
        def result = scanner.scanAndInfer(classToScan)

        then:
        result instanceof ValueObjectDefinition

        where:
        annotation << [javax.persistence.Embeddable,
                       org.javers.core.metamodel.annotation.ValueObject]
        classToScan << [JpaEmbeddable, JaversValueObject]
    }

    @Unroll
    def "should map #annotation.name to Value"() {

        when:
        def result = scanner.scanAndInfer(classToScan)

        then:
        result instanceof ValueDefinition

        where:
        annotation << [org.javers.core.metamodel.annotation.Value]
        classToScan << [JaversValue]
    }

}
