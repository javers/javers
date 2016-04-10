package org.javers.core.metamodel.type

import com.google.gson.reflect.TypeToken
import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.object.GlobalId
import org.javers.core.model.AbstractDummyUser
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class TypeMapperTest extends Specification {

    @Shared
    def mapper = JaversTestBuilder.javersTestAssembly().typeMapper
    
    enum DummyEnum {A,B}

    class DummySet extends HashSet{}

    class Dummy <T,X> {
        int[] intArray
    }
    
    class DummyMapWithGenericValue
    {
        Map<String, EnumSet<DummyEnum>> mapWithGenericValueArgument
    }
    
    @Unroll
    def "should return dehydrated type for simple #givenJaversType"() {
        expect:
        mapper.getDehydratedType(givenJaversType) == expectedGenericDehydratedType

        where:
        givenJaversType || expectedGenericDehydratedType
        DummyUser       || GlobalId
        DummyAddress    || GlobalId
        String          || String
        Integer.TYPE    || Integer.TYPE
    }
    @Unroll
    def "should return dehydrated type for array of #givenJaversType"() {
        expect:
        mapper.getDehydratedType(givenJaversType) == expectedDehydratedType

        where:
        givenJaversType || expectedDehydratedType
        ([] as DummyUser[]).class     || ([] as GlobalId[]).class
        ([] as DummyAddress[]).class  || ([] as GlobalId[]).class
        ([] as int[]).class           || ([] as int[]).class
        ([] as String[]).class        || ([] as String[]).class
    }

    def "should return dehydrated type for Map<String,EnumSet<DummyEnum>>"() {
        given:
        Type givenJaversType = getFieldFromClass(DummyMapWithGenericValue, "mapWithGenericValueArgument").genericType
        
        when:
        def dehydrated = mapper.getDehydratedType(givenJaversType)
        
        then:
        dehydrated instanceof ParameterizedType
        dehydrated.rawType == Map
        dehydrated.actualTypeArguments[0] == String
        dehydrated.actualTypeArguments[1] instanceof ParameterizedType
        dehydrated.actualTypeArguments[1].rawType == EnumSet
        dehydrated.actualTypeArguments[1].actualTypeArguments[0] == new TypeToken<DummyEnum>(){}.type
    }
    
    @Unroll
    def "should return dehydrated type for generic #givenJavaType"() {
        when:
        def dehydrated =  mapper.getDehydratedType(givenJavaType)

        then:
        dehydrated instanceof ParameterizedType
        dehydrated.rawType == expectedRawType
        dehydrated.actualTypeArguments == expectedActualTypeArguments

        where:
        givenJavaType                                     || expectedRawType  || expectedActualTypeArguments
        new TypeToken<Set<String>>(){}.type               || Set              || [String]
        new TypeToken<Set<DummyUser>>(){}.type            || Set              || [GlobalId]
        new TypeToken<List<String>>(){}.type              || List             || [String]
        new TypeToken<List<DummyUser>>(){}.type           || List             || [GlobalId]
        new TypeToken<Map<String, DummyUser>>(){}.type    || Map              || [String, GlobalId]
    }

    @Unroll
    def "should spawn concrete Array type for #givenJavaType"() {
        when:
        def jType = mapper.getJaversType(givenJavaType)

        then:
        jType.baseJavaType == givenJavaType
        jType.class == ArrayType
        jType.itemClass == expectedItemClass

        where:
        givenJavaType               || expectedItemClass
        ([] as int[]).class         || int
        ([] as String[]).class      || String
        ([] as DummyUser[]).class   || DummyUser
    }

    def "should spawn concrete Enum type"() {
        when:
        def jType = mapper.getJaversType(DummyEnum)

        then:
        jType.baseJavaType == DummyEnum
        jType.class == PrimitiveType
    }

    @Unroll
    def "should map Container #expectedColType.simpleName by default"() {
        when:
        def jType = mapper.getJaversType(givenJavaType)

        then:
        jType.baseJavaType == givenJavaType
        jType.class == expectedColType

        where:
        givenJavaType | expectedColType
        Set  | SetType
        List | ListType
        Map  | MapType
        Optional | OptionalType
        Collection | CollectionType
    }

    @Unroll
    def "should spawn concrete Container #expectedColType.simpleName from prototype interface for #givenJavaType.simpleName"() {
        when:
        def jType = mapper.getJaversType(givenJavaType)

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
        when:
        def jType = mapper.getJaversType( givenJavaType )

        then:
        jType.class == expectedJaversType
        jType.baseJavaType == givenJavaType
        jType.itemClass == String

        where:
        givenJavaType                             | expectedJaversType
        new TypeToken<Collection<String>>(){}.type| CollectionType
        new TypeToken<Set<String>>(){}.type       | SetType
        new TypeToken<HashSet<String>>(){}.type   | SetType
        new TypeToken<List<String>>(){}.type      | ListType
        new TypeToken<ArrayList<String>>(){}.type | ListType
        new TypeToken<Optional<String>>(){}.type  | OptionalType
    }

    @Unroll
    def "should spawn generic Map #givenJavaType from non-generic prototype interface"() {
        when:
        def jType = mapper.getJaversType(givenJavaType)

        then:
        jType.baseJavaType == givenJavaType
        jType.keyType == String
        jType.valueType == Integer

        where:
        givenJavaType << [new TypeToken<Map<String, Integer>>(){}.type,new TypeToken<HashMap<String, Integer>>(){}.type]
    }

    def "should spawn ValueType from mapped superclass"() {
        given:
        def mapper = JaversTestBuilder.javersTestAssembly().typeMapper
        mapper.registerValueType(AbstractDummyUser)

        when:
        def jType = mapper.getJaversType(DummyUser)

        then:
        jType.class == ValueType
        jType.baseJavaClass == DummyUser
    }

    def "should spawn generic types as distinct javers types"() {
        when:
        def setWithStringJaversType  = mapper.getJaversType(new TypeToken<Set<String>>(){}.type)
        def hashSetWithIntJaversType = mapper.getJaversType(new TypeToken<HashSet<Integer>>(){}.type)

        then:
        setWithStringJaversType != hashSetWithIntJaversType
        setWithStringJaversType.baseJavaType  ==  new TypeToken<Set<String>>(){}.type
        hashSetWithIntJaversType.baseJavaType ==  new TypeToken<HashSet<Integer>>(){}.type
    }

    def "should recognize Object.class as empty ValueType"() {
        when:
        def jType = mapper.getJaversType(Object)

        then:
        jType instanceof ValueType
    }
}
