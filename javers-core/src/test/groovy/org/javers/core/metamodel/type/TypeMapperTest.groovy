package org.javers.core.metamodel.type

import com.google.gson.reflect.TypeToken
import org.javers.core.metamodel.property.EntityDefinition
import org.javers.core.metamodel.property.ManagedClassFactory
import org.javers.core.metamodel.property.ValueObjectDefinition
import org.javers.core.model.AbstractDummyUser
import org.javers.core.model.DummyUser
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass
import static org.javers.core.JaversTestBuilder.javersTestAssembly

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
        TypeMapper mapper = new TypeMapper(new TypeSpawningFactory(Mock(ManagedClassFactory)))
        int arrayPrototypes  = mapper.getMappedTypes(ArrayType).size()
        Type intArray   = getFieldFromClass(Dummy, "intArray").genericType

        when:
        JaversType jType = mapper.getJaversType(intArray)

        then:
        jType.baseJavaType == int[]
        jType.class == ArrayType
        jType.elementType == int
        mapper.getMappedTypes(ArrayType).size() == arrayPrototypes + 1
    }

    def "should spawn concrete Enum type"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeSpawningFactory(Mock(ManagedClassFactory)))

        when:
        JaversType jType = mapper.getJaversType(DummyEnum)

        then:
        jType.baseJavaType == DummyEnum
        jType.class == PrimitiveType
    }

    @Unroll
    def "should map Container #expectedColType.simpleName by default"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeSpawningFactory(Mock(ManagedClassFactory)))

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
        TypeMapper mapper = new TypeMapper(new TypeSpawningFactory(Mock(ManagedClassFactory)))

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
        TypeMapper mapper = new TypeMapper(new TypeSpawningFactory(Mock(ManagedClassFactory)))

        when:
        def jType = mapper.getJaversType( givenJavaType )

        then:
        jType.class == expectedJaversType
        jType.baseJavaType == givenJavaType
        jType.elementType == String

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
        TypeMapper mapper = new TypeMapper(new TypeSpawningFactory(Mock(ManagedClassFactory)))

        when:
        MapType jType = mapper.getJaversType(givenJavaType)

        then:
        jType.baseJavaType == givenJavaType
        jType.entryClass.key == String
        jType.entryClass.value == Integer

        where:
        givenJavaType << [new TypeToken<Map<String, Integer>>(){}.type,new TypeToken<HashMap<String, Integer>>(){}.type]
    }

    def "should spawn ValueType from mapped superclass"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeSpawningFactory(Mock(ManagedClassFactory)))
        mapper.registerValueType(AbstractDummyUser)

        when:
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType.class == ValueType
        jType.baseJavaClass == DummyUser
    }

    def "should spawn EntityType from mapped superclass"() {
        given:
        TypeMapper mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)
        mapper.registerManagedClass(new EntityDefinition(AbstractDummyUser,"inheritedInt"))

        when:
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType.class == EntityType
        jType.baseJavaClass == DummyUser
    }

    def "should spawn ValueObjectType from mapped superclass"() {
        given:
        TypeMapper mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)
        mapper.registerManagedClass(new ValueObjectDefinition(AbstractDummyUser))

        when:
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType.class == ValueObjectType
        jType.baseJavaClass == DummyUser
    }

    class DummyGenericUser<T> extends AbstractDummyUser {}

    @Unroll
    def "should spawn #queryTypeAsString from the nearest prototype"() {
        given:
        TypeMapper mapper = new TypeMapper(javersTestAssembly().typeSpawningFactory)
        mapper.registerValueType(Object.class)
        mapper.registerManagedClass(new ValueObjectDefinition(AbstractDummyUser))

        when:
        def jType = mapper.getJaversType(queryType)

        then:
        jType.class == ValueObjectType
        jType.baseJavaClass == DummyGenericUser

        where:
        queryType << [DummyGenericUser, new TypeToken<DummyGenericUser<String>>(){}.type]
        queryTypeAsString << ["DummyGenericUser","DummyGenericUser<String>"]
    }

    def "should spawn generic types as distinct javers types"() {
        given:
        TypeMapper mapper = new TypeMapper(new TypeSpawningFactory(Mock(ManagedClassFactory)))

        when:
        JaversType setWithStringJaversType  = mapper.getJaversType(new TypeToken<Set<String>>(){}.type)
        JaversType hashSetWithIntJaversType = mapper.getJaversType(new TypeToken<HashSet<Integer>>(){}.type)

        then:
        setWithStringJaversType != hashSetWithIntJaversType
        setWithStringJaversType.baseJavaType  ==  new TypeToken<Set<String>>(){}.type
        hashSetWithIntJaversType.baseJavaType ==  new TypeToken<HashSet<Integer>>(){}.type
    }

}