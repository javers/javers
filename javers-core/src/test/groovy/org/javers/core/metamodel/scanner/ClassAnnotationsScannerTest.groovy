package org.javers.core.metamodel.scanner

import org.javers.core.metamodel.clazz.*
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author bartosz walacik
 */
class ClassAnnotationsScannerTest extends Specification {

    def scanner = new ClassAnnotationsScanner(new AnnotationNamesProvider())

    @Unroll
    def "should map #annotation.name to Entity"() {

        when:
        def result = scanner.scan(classToScan)

        then:
        result.hasEntity

        where:
        annotation << [javax.persistence.Entity,
                       javax.persistence.MappedSuperclass,
                       org.javers.core.metamodel.annotation.Entity]
        classToScan << [JpaEntity, JpaMappedSuperclass, JaversEntity]
    }

    @Unroll
    def "should map #annotation.name to ValueObject"() {

        when:
        def result = scanner.scan(classToScan)

        then:
        result.hasValueObject

        where:
        annotation << [javax.persistence.Embeddable,
                       org.javers.core.metamodel.annotation.ValueObject]
        classToScan << [JpaEmbeddable, JaversValueObject]
    }

    @Unroll
    def "should map #annotation.name to Value"() {

        when:
        def result = scanner.scan(classToScan)

        then:
        result.hasValue

        where:
        annotation << [org.javers.core.metamodel.annotation.Value]
        classToScan << [JaversValue]
    }

}
