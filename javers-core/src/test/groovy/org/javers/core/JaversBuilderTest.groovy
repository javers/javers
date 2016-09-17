package org.javers.core

import org.javers.core.diff.DiffFactory
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.core.diff.appenders.SimpleListChangeAppender
import org.javers.core.diff.appenders.levenshtein.LevenshteinListChangeAppender
import org.javers.core.examples.typeNames.NewEntityWithTypeAlias
import org.javers.core.examples.typeNames.NewValueObjectWithTypeAlias
import org.javers.core.graph.ObjectAccessHook
import org.javers.core.metamodel.scanner.BeanBasedPropertyScanner
import org.javers.core.metamodel.scanner.FieldBasedPropertyScanner
import org.javers.core.metamodel.type.EntityType
import org.javers.core.metamodel.type.TypeMapper
import org.javers.core.metamodel.type.ValueObjectType
import org.javers.core.model.DummyNetworkAddress
import spock.lang.Specification
import spock.lang.Unroll

import javax.persistence.Id

import static org.fest.assertions.api.Assertions.assertThat
import static org.javers.core.JaversBuilder.javers

/**
 * @author bartosz walacik
 */
class JaversBuilderTest extends Specification {

    def "should scan Entities with @TypeName when packegaToScan is given"() {
        when:
        def javers = JaversTestBuilder.javersTestAssembly("org.zonk, org.javers.core.examples.typeNames")
        def typeMapper = javers.typeMapper

        then:
        typeMapper.getJaversManagedType("myValueObject").baseJavaClass == NewValueObjectWithTypeAlias
    }

    def "should scan given Entity when requested explicitly"() {
        when:
        def javers = JaversTestBuilder.javersTestAssembly(NewEntityWithTypeAlias)
        def typeMapper = javers.typeMapper

        then:
        typeMapper.getJaversManagedType("myName").baseJavaClass == NewEntityWithTypeAlias
    }

    def "should manage Entity"() {
        when:
        def javers = javers().registerEntity(DummyEntity).build()

        then:
        javers.getTypeMapping(DummyEntity) instanceof EntityType
    }

    def "should manage ValueObject"() {
        when:
        def javers = javers().registerValueObject(DummyNetworkAddress).build()

        then:
        javers.getTypeMapping(DummyNetworkAddress) instanceof ValueObjectType
    }

    def "should create Javers"() {
        when:
        def javers = javers().build()

        then:
        javers != null
    }

    def "should create multiple Javers instances"() {
        when:
        def javers1 = javers().build()
        def javers2 = javers().build()

        then:
        javers1 != javers2
    }

    def "should contain ObjectAccessHook when given"() {
        given:
        def graphFactoryHook = Stub(ObjectAccessHook)
        JaversBuilder javersBuilder = javers().withObjectAccessHook(graphFactoryHook)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(ObjectAccessHook) == graphFactoryHook
    }

    def "should not contain FieldBasedPropertyScanner when Bean style"() {
        given:
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.BEAN)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(FieldBasedPropertyScanner) == null
    }


    def "should not contain BeanBasedPropertyScanner when Field style"() {
        given:
        JaversBuilder javersBuilder = javers().withMappingStyle(MappingStyle.FIELD)

        when:
        javersBuilder.build()

        then:
        javersBuilder.getContainerComponent(BeanBasedPropertyScanner) == null
    }


    def "should create multiple javers containers"() {
        given:
        JaversBuilder builder1 = JaversBuilder.javers()
        JaversBuilder builder2 = JaversBuilder.javers()

        when:
        builder1.build()
        builder2.build()

        then:
        builder1.getContainerComponent(Javers) != builder2.getContainerComponent(Javers)
    }

    @Unroll
    def "should contain #clazz.getSimpleName() bean"() {
        given:
        JaversBuilder builder = javers()

        when:
        builder.build()

        then:
        assertThat(builder.getContainerComponent(clazz)) isInstanceOf(clazz)

        where:
        clazz << [Javers, TypeMapper, DiffFactory]
    }

    def "should contain singletons"() {
        given:
        JaversBuilder builder = javers()

        when:
        builder.build()

        then:
        builder.getContainerComponent(Javers) == builder.getContainerComponent(Javers)
    }

    def "should use LevenshteinListChangeAppender when selected"() {
        given:
        def builder = javers().withListCompareAlgorithm(ListCompareAlgorithm.LEVENSHTEIN_DISTANCE)

        when:
        builder.build()

        then:
        builder.getContainerComponent(LevenshteinListChangeAppender)
    }

    def "should use SimpleListChangeAppender by default"() {
        given:
        def builder = javers()

        when:
        builder.build()

        then:
        builder.getContainerComponent(SimpleListChangeAppender)
    }

    class DummyEntity {
        @Id
        int id
        DummyEntity parent
    }
}
