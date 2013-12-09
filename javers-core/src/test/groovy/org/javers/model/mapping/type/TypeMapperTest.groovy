package org.javers.model.mapping.type

import com.google.gson.reflect.TypeToken
import spock.lang.Specification

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

        Set<String>  setWithString
        HashSet<Integer> hashSetWithInt
    }

    def "should spawn concrete Enum type"() {
        given:
        TypeMapper mapper = new TypeMapper();
        Type dummyEnum   = getFieldFromClass(Dummy, "dummyEnum").type

        when:
        JaversType jType = mapper.getJavesrType(dummyEnum)

        then:
        jType.baseJavaType == DummyEnum
        jType.class == PrimitiveType
    }

    def "should map Set & List by default"() {
        given:
        TypeMapper mapper = new TypeMapper();
        Type set   = getFieldFromClass(Dummy, "set").type

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
        Type dummySet   = getFieldFromClass(Dummy, "dummySet").type

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