package org.javers.core.metamodel.annotation

import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.clazz.EntityDefinition
import org.javers.core.metamodel.clazz.JaversEntity
import org.javers.core.metamodel.clazz.JaversValue
import org.javers.core.metamodel.clazz.JaversValueObject
import org.javers.core.metamodel.clazz.JpaEmbeddable
import org.javers.core.metamodel.clazz.JpaEntity
import org.javers.core.metamodel.clazz.JpaMappedSuperclass
import org.javers.core.metamodel.clazz.NotAnnotatedClass
import org.javers.core.metamodel.clazz.ValueDefinition
import org.javers.core.metamodel.clazz.ValueObjectDefinition
import spock.lang.Specification
import spock.lang.Unroll

/**
 * @author bartosz walacik
 */
class ClassAnnotationsScannerTest extends Specification {

    def scanner = JaversTestBuilder.javersTestAssembly().classAnnotationsScanner

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
