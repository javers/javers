package org.javers.core.metamodel.type

import com.google.gson.reflect.TypeToken
import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.property.FieldBasedPropertyScanner
import org.javers.core.metamodel.property.ManagedClassFactory
import org.javers.core.metamodel.property.Property
import org.javers.core.model.AbstractDummyUser
import org.javers.core.model.DummyUser
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class TypeMapperTest extends Specification {

    enum DummyEnum {A,B}

    class DummySet extends HashSet{}

    class Dummy <T,X> {
        int[] intArray
    }

    def "should spawn concrete Array type"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeFactory(Mock(ManagedClassFactory)))
        int arrayPrototypes  = mapper.getMappedTypes(ArrayType).size()
        Type intArray   = getFieldFromClass(Dummy, "intArray").genericType

        when:
        JaversType jType = mapper.getJaversType(intArray)

        then:
        jType.baseJavaType == int[]
        jType.class == ArrayType
        jType.itemClass == int
        mapper.getMappedTypes(ArrayType).size() == arrayPrototypes + 1
    }

    def "should spawn concrete Enum type"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeFactory(Mock(ManagedClassFactory)))

        when:
        JaversType jType = mapper.getJaversType(DummyEnum)

        then:
        jType.baseJavaType == DummyEnum
        jType.class == PrimitiveType
    }

    @Unroll
    def "should map Container #expectedColType.simpleName by default"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeFactory(Mock(ManagedClassFactory)))

        when:
        JaversType jType = mapper.getJaversType(givenJavaType)

        then:
        jType.baseJavaType == givenJavaType
        jType.class == expectedColType

        where:
        givenJavaType | expectedColType
        Set  | SetType
        List | ListType
        Map  | MapType
    }

    @Unroll
    def "should spawn concrete Container #expectedColType.simpleName from prototype interface for #givenJavaType.simpleName"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeFactory(Mock(ManagedClassFactory)))

        when:
        JaversType jType = mapper.getJaversType(givenJavaType)

        then:
        jType.baseJavaType == givenJavaType
        jType.class == expectedColType

        where:
        givenJavaType | expectedColType
        HashSet  | SetType
        ArrayList | ListType
        HashMap | MapType
    }

    @Unroll
    def "should spawn generic Collection #givenJavaType from non-generic prototype interface"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeFactory(Mock(ManagedClassFactory)))

        when:
        def jType = mapper.getJaversType( givenJavaType )

        then:
        jType.class == expectedJaversType
        jType.baseJavaType == givenJavaType
        jType.itemClass == String

        where:
        givenJavaType                        | expectedJaversType
        new TypeToken<Set<String>>(){}.type  | SetType
        new TypeToken<HashSet<String>>(){}.type  | SetType
        new TypeToken<List<String>>(){}.type | ListType
        new TypeToken<ArrayList<String>>(){}.type | ListType
    }

    @Unroll
    def "should spawn generic Map #givenJavaType from non-generic prototype interface"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeFactory(Mock(ManagedClassFactory)))

        when:
        MapType jType = mapper.getJaversType(givenJavaType)

        then:
        jType.baseJavaType == givenJavaType
        jType.keyClass == String
        jType.valueClass == Integer

        where:
        givenJavaType << [new TypeToken<Map<String, Integer>>(){}.type,new TypeToken<HashMap<String, Integer>>(){}.type]
    }

    def "should spawn ValueType from mapped superclass"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeFactory(Mock(ManagedClassFactory)))
        mapper.registerValueType(AbstractDummyUser)

        when:
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType.class == ValueType
        jType.baseJavaClass == DummyUser
    }

    def "should spawn generic types as distinct javers types"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeFactory(Mock(ManagedClassFactory)))

        when:
        JaversType setWithStringJaversType  = mapper.getJaversType(new TypeToken<Set<String>>(){}.type)
        JaversType hashSetWithIntJaversType = mapper.getJaversType(new TypeToken<HashSet<Integer>>(){}.type)

        then:
        setWithStringJaversType != hashSetWithIntJaversType
        setWithStringJaversType.baseJavaType  ==  new TypeToken<Set<String>>(){}.type
        hashSetWithIntJaversType.baseJavaType ==  new TypeToken<HashSet<Integer>>(){}.type
    }

    def "should recognize Collection of Entity type"() {
        given:
        JaversTestBuilder javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        TypeMapper mapper = javersTestBuilder.typeMapper
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner()
        List<Property> properties = scanner.scan(DummyUser)
        Property property = properties.find {
            it.name == "dummyUserDetailsList"
        }

        expect:
        mapper.isContainerOfManagedClasses(property)
    }

    def "should not recognize primitive type collection as collection of entity"() {
        given:
        JaversTestBuilder javersTestBuilder = JaversTestBuilder.javersTestAssembly()
        TypeMapper mapper = javersTestBuilder.typeMapper
        FieldBasedPropertyScanner scanner = new FieldBasedPropertyScanner()
        List<Property> properties = scanner.scan(DummyUser)
        Property property = properties.find {
            it.name == "integerList"
        }

        expect:
        mapper.isContainerOfManagedClasses(property) == false
    }

}
