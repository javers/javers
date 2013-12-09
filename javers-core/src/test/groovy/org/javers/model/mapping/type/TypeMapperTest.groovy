package org.javers.model.mapping.type

import com.google.gson.reflect.TypeToken
import spock.lang.Specification

import java.lang.reflect.Array
import java.lang.reflect.Field
import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class TypeMapperTest extends Specification {

    enum DummyEnum {A,B}

    class DummySet extends HashSet{}

    class Dummy <T,X> {
        Set set
        DummySet dummySet
        HashSet<String> hashSetWithString
        DummyEnum dummyEnum
        int[] intArray
        Set<String>  setWithString
        HashSet<Integer> hashSetWithInt
    }

    def "should spawn concrete Array type"() {
        given:
        TypeMapper mapper = new TypeMapper();
        int arrayPrototypes  = mapper.getMappedTypes(ArrayType).size()
        Type intArray   = getFieldFromClass(Dummy, "intArray").genericType

        when:
        JaversType jType = mapper.getJavesrType(intArray)

        then:
        jType.baseJavaType == int[]
        jType.class == ArrayType
        mapper.getMappedTypes(ArrayType).size() == arrayPrototypes + 1
    }

    def "should spawn concrete Enum type"() {
        given:
        TypeMapper mapper = new TypeMapper();
        Type dummyEnum   = getFieldFromClass(Dummy, "dummyEnum").genericType

        when:
        JaversType jType = mapper.getJavesrType(dummyEnum)

        then:
        jType.baseJavaType == DummyEnum
        jType.class == PrimitiveType
    }

    def "should map Set & List by default"() {
        given:
        TypeMapper mapper = new TypeMapper();
        Type set   = getFieldFromClass(Dummy, "set").genericType

        when:
        JaversType jType = mapper.getJavesrType(set)

        then:
        jType.baseJavaType == Set
        mapper.getMappedTypes(CollectionType).size() == 2
    }


    def "should spawn impl from interface"() {
        given:
        TypeMapper mapper = new TypeMapper()
        int colPrototypes  = mapper.getMappedTypes(CollectionType).size()
        Type dummySet   = getFieldFromClass(Dummy, "dummySet").genericType

        when:
        JaversType jType = mapper.getJavesrType(dummySet)

        then:
        jType.baseJavaType == DummySet
        jType.class == CollectionType
        mapper.getMappedTypes(CollectionType).size() == colPrototypes + 1
    }

    def "should spawn generic type from non-generic prototype"() {
        given:
        TypeMapper mapper = new TypeMapper()
        Type setWithString   = getFieldFromClass(Dummy, "setWithString").genericType

        when:
        JaversType jType = mapper.getJavesrType(setWithString)

        then:
        jType.baseJavaType == new TypeToken<Set<String>>(){}.type
    }


    def "should spawn generic types as distinct javers types"() {
        given:
        TypeMapper mapper = new TypeMapper();
        int colPrototypes  = mapper.getMappedTypes(CollectionType).size()
        Type setWithString  = getFieldFromClass(Dummy, "setWithString").genericType
        Type hashSetWithInt = getFieldFromClass(Dummy, "hashSetWithInt").genericType

        when:
        JaversType setWithStringJaversType  = mapper.getJavesrType(setWithString)
        JaversType hashSetWithIntJaversType = mapper.getJavesrType(hashSetWithInt)

        then:
        setWithStringJaversType.baseJavaType  ==  new TypeToken<Set<String>>(){}.type
        hashSetWithIntJaversType.baseJavaType ==  new TypeToken<HashSet<Integer>>(){}.type
        mapper.getMappedTypes(CollectionType).size() == colPrototypes + 2
    }


}