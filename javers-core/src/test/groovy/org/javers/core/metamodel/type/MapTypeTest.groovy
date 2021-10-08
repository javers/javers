package org.javers.core.metamodel.type

import com.google.gson.reflect.TypeToken
import org.javers.common.collections.EnumerableFunction
import org.javers.core.JaversTestBuilder
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.object.EnumerationAwareOwnerContext
import org.javers.core.metamodel.object.PropertyOwnerContext
import org.javers.core.model.DummyAddress
import spock.lang.Specification
import spock.lang.Unroll

import java.lang.reflect.Type

import static org.javers.common.reflection.ReflectionTestHelper.getFieldFromClass

/**
 * @author bartosz walacik
 */
class MapTypeTest extends Specification{

    enum DummyEnum{}

    class Dummy <T> {
        @Id String id
        Map             noGeneric
        Map<?, ?>       wildcardGeneric
        Map<T, T>       parametrizedGeneric
        Map<String, Integer> genericWithArgument
        Map<String, List<DummyAddress>> mapWithListOfValueObjects
        Map<String, EnumSet<DummyEnum>> mapWithNestedParametrizedType
    }

    def "should apply map recursively to Container values" () {
        given:
        def javers = JaversTestBuilder.javersTestAssembly()
        MapType mapType = javers.getTypeMapper().getJaversManagedType(Dummy)
                .getProperty('mapWithListOfValueObjects').getType()
        def entity = new Dummy(
                id: '1',
                mapWithListOfValueObjects:
                ['key.a':[new DummyAddress("London"), new DummyAddress("Paris")]])

        def cdoFactory = javers.getLiveCdoFactory()
        def globalId = javers.globalIdFactory.createIdFromInstance(entity)
        def owner = new PropertyOwnerContext(globalId, 'mapWithListOfValueObjects')
        println 'owner.id'
        println globalId
        def fun = new EnumerableFunction() {
            Object apply(Object input, EnumerationAwareOwnerContext ownerContext) {
                if (input instanceof String) {
                    return input
                }
                return cdoFactory.createId(input, ownerContext)
            }
        }

        when:
        def mapped = mapType.map(
                entity.mapWithListOfValueObjects,
                fun,
                owner
        )
        println 'mapped'
        println mapped

        then:
        mapped['key.a'][0].value().endsWith('MapTypeTest$Dummy/1#mapWithListOfValueObjects/key.a/0')
        mapped['key.a'][1].value().endsWith('MapTypeTest$Dummy/1#mapWithListOfValueObjects/key.a/1')
    }

    @Unroll
    def "should replace non-concrete type parameters with Object for type: #genericKind"(){
        given:
        def genericType = getFieldFromClass(Dummy, genericKind).genericType

        when:
        def mType = mapType(genericType)

        then:
        mType.getKeyJavaType() == Object
        mType.getValueJavaType() == Object

        where:
        genericKind << ["noGeneric","wildcardGeneric","parametrizedGeneric"]
    }

    def "should return key & value Class if baseJavaType is generic with actual Class argument"(){
        given:
        Type noGeneric = getFieldFromClass(Dummy, "genericWithArgument").genericType

        when:
        MapType mType = mapType(noGeneric)

        then:
        mType.baseJavaType == new TypeToken<Map<String,Integer>>(){}.type
        mType.keyJavaType == String
        mType.valueJavaType == Integer
    }

    def "should scan nested generic type from Map value type parameter" () {
        given:
        def genericWithGenericArgument = getFieldFromClass(Dummy, "mapWithNestedParametrizedType").genericType

        when:
        def mType = mapType(genericWithGenericArgument)

        then:
        mType.baseJavaType == new TypeToken<Map<String,EnumSet<DummyEnum>>>(){}.type
        mType.keyJavaType == String
        mType.valueJavaType == new TypeToken< EnumSet<DummyEnum> >(){}.type
    }

    MapType mapType(Type type) {
        new MapType(type, { it -> new ValueType(Object) })
    }
}
