package org.javers.core.metamodel.type

import com.google.gson.reflect.TypeToken
import org.javers.core.metamodel.type.ArrayType
import org.javers.core.metamodel.type.CollectionType
import org.javers.core.metamodel.type.JaversType
import org.javers.core.metamodel.type.MapType
import org.javers.core.metamodel.type.PrimitiveType
import org.javers.core.metamodel.type.TypeMapper
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
        TypeMapper mapper = new TypeMapper();
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
        TypeMapper mapper = new TypeMapper();

        when:
        JaversType jType = mapper.getJaversType(DummyEnum)

        then:
        jType.baseJavaType == DummyEnum
        jType.class == PrimitiveType
    }

    @Unroll
    def "should map Container #expectedColType.simpleName by default"() {
        given:
        TypeMapper mapper = new TypeMapper();

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
        TypeMapper mapper = new TypeMapper()

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
    def "should spawn generic Collection #expectedJaversType.simpleName from non-generic prototype interface for #givenJavaType"() {
        given:
        TypeMapper mapper = new TypeMapper()

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
    def "should spawn generic MapType from non-generic prototype interface for #givenJavaType"() {
        given:
        TypeMapper mapper = new TypeMapper()

        when:
        MapType jType = mapper.getJaversType(givenJavaType)

        then:
        jType.baseJavaType == givenJavaType
        jType.entryClass.key == String
        jType.entryClass.value == Integer

        where:
        givenJavaType << [new TypeToken<Map<String, Integer>>(){}.type,new TypeToken<HashMap<String, Integer>>(){}.type]

    }

    def "should spawn generic types as distinct javers types"() {
        given:
        TypeMapper mapper = new TypeMapper();
        int colPrototypes  = mapper.getMappedTypes(CollectionType).size()

        when:
        JaversType setWithStringJaversType  = mapper.getJaversType(new TypeToken<Set<String>>(){}.type)
        JaversType hashSetWithIntJaversType = mapper.getJaversType(new TypeToken<HashSet<Integer>>(){}.type)

        then:
        setWithStringJaversType.baseJavaType  ==  new TypeToken<Set<String>>(){}.type
        hashSetWithIntJaversType.baseJavaType ==  new TypeToken<HashSet<Integer>>(){}.type
        mapper.getMappedTypes(CollectionType).size() == colPrototypes + 2
    }


}