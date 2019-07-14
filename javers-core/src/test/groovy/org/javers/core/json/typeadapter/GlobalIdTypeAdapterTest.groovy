package org.javers.core.json.typeadapter

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.javers.core.examples.typeNames.NewEntityWithTypeAlias
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.annotation.ValueObject
import org.javers.core.metamodel.clazz.JaversEntity
import org.javers.core.metamodel.object.GlobalId
import org.javers.core.metamodel.object.InstanceId
import org.javers.core.metamodel.object.UnboundedValueObjectId
import org.javers.core.metamodel.object.ValueObjectId
import org.javers.core.model.*
import spock.lang.Specification
import spock.lang.Unroll

import static org.javers.core.GlobalIdTestBuilder.valueObjectId
import static org.javers.core.JaversTestBuilder.javersTestAssembly
import static org.javers.core.GlobalIdTestBuilder.instanceId

/**
 * @author bartosz walacik
 */
class GlobalIdTypeAdapterTest extends Specification {

    def class IdHolder{
        GlobalId id
    }

    class DummyWithEntityId {
        @Id EntityAsId entityAsId
        int value
    }

    class EntityAsId {
        @Id
        int id
        int value
    }


    def "should deserialize InstanceId with nested Entity Id -- legacy format"(){
      given:
      def instanceIdLegacyJson = '''
            {
              "entity": "org.javers.core.json.typeadapter.GlobalIdTypeAdapterTest$DummyWithEntityId",
              "cdoId": {
                "id": 1,
                "value": 5
              }
            }
            '''

      when:
      InstanceId instanceId = javersTestAssembly().jsonConverter.fromJson(instanceIdLegacyJson, InstanceId)

      then:
      println instanceId.value()
      instanceId.value().endsWith("DummyWithEntityId/1")
    }

    class EntityWithVOId {
        @Id ValueObjectAsId id
        int value
    }

    @ValueObject
    class ValueObjectAsId {
        int id
        int value
    }

    def "should deserialize InstanceId with ValueObject Id -- legacy format"(){
        given:
        def instanceIdLegacyJson = '''
            {
              "entity": "org.javers.core.json.typeadapter.GlobalIdTypeAdapterTest$EntityWithVOId",
              "cdoId": {
                "id": 1,
                "value": 5
              }
            }
            '''

        when:
        InstanceId instanceId = javersTestAssembly().jsonConverter.fromJson(instanceIdLegacyJson, InstanceId)

        then:
        println instanceId.value()
        instanceId.value().endsWith("EntityWithVOId/1,5")
    }


    @Unroll
    def "should deserialize InstanceId with #type cdoId"() {
        when:
        def idHolder = javersTestAssembly().jsonConverter.fromJson(givenJson, IdHolder)

        then:
        idHolder.id instanceof InstanceId
        idHolder.id == expectedId

        where:
        type << ["String", "Long"]
        givenJson << [
                '{"id":{"entity":"org.javers.core.model.DummyUser","cdoId":"kaz"}}',
                '{"id":{"entity":"org.javers.core.model.DummyUserDetails","cdoId":1}}'
                ]
        expectedId <<[
                instanceId("kaz", DummyUser),
                instanceId(1L, DummyUserDetails)
        ]
    }

    def "should serialize Instance @EmbeddedId using json fields"(){
        given:
        def javers = javersTestAssembly()
        def id = javers.instanceId(new DummyPoint(2,3),DummyEntityWithEmbeddedId)

        when:
        def jsonText = javers.jsonConverter.toJson(id)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.cdoId.x == 2
        json.cdoId.y == 3
    }

    @Unroll
    def "should serialize InstanceId with #what name"() {
        given:
        def javers = javersTestAssembly()
        def id = javers.instanceId("kaz",clazz)

        when:
        def jsonText = javers.jsonConverter.toJson(id)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.cdoId == "kaz"
        json.entity == expectedName

        where:
        what <<  ["default", "@TypeName"]
        clazz << [JaversEntity, NewEntityWithTypeAlias]
        expectedName << [JaversEntity.name, "myName"]
    }

    def "should serialize UnboundedValueObjectId"() {
        given:
        def javers = javersTestAssembly()
        def id = javers.unboundedValueObjectId(DummyAddress)

        when:
        def jsonText = javers.jsonConverter.toJson(id)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.valueObject == "org.javers.core.model.DummyAddress"
    }

    def "should deserialize UnboundedValueObjectId from JSON"() {
        given:
        def json = '{"id":{"valueObject":"org.javers.core.model.DummyAddress","cdoId":"/"}}'
        def javers = javersTestAssembly()

        when:
        def idHolder = javers.jsonConverter.fromJson(json, IdHolder)

        then:
        idHolder.id instanceof UnboundedValueObjectId
        idHolder.id == javers.unboundedValueObjectId(DummyAddress)
    }

    def "should deserialize InstanceId with @EmbeddedId to original Type"(){
        given:
        def json =
        '''
        { "entity": "org.javers.core.model.DummyEntityWithEmbeddedId",
          "cdoId": {
            "x": 2,
            "y": 3
          }}
        '''
        def javers = javersTestAssembly()

        when:
        def id = javers.jsonConverter.fromJson(json, GlobalId)

        then:
        id instanceof InstanceId
        id.cdoId instanceof DummyPoint
        id.cdoId.x == 2
        id.cdoId.y == 3
    }

    def "should deserialize InstanceId with @TypeName when EntityType is mapped"(){
        given:
        def json = '{ "entity": "myName", "cdoId": 1}'
        def javers = javersTestAssembly()
        javers.typeMapper.getJaversType(NewEntityWithTypeAlias)

        when:
        def id = javers.jsonConverter.fromJson(json, GlobalId)

        then:
        id instanceof InstanceId
        id.cdoId instanceof BigDecimal
        id.cdoId == 1
    }

    def "should serialize ValueObjectId"() {
        given:
        def javers = javersTestAssembly()
        def id = javers.valueObjectId(5,DummyUserDetails,"dummyAddress")

        when:
        def jsonText = javers.jsonConverter.toJson(id)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.ownerId.entity == "org.javers.core.model.DummyUserDetails"
        json.ownerId.cdoId ==  5
        json.valueObject == "org.javers.core.model.DummyAddress"
        json.fragment == "dummyAddress"
    }

    def "should deserialize ValueObjectId"() {
        given:
        def json = new JsonBuilder()
        json.id {
            fragment "dummyAddress"
            valueObject "org.javers.core.model.DummyAddress"
            ownerId {
                entity "org.javers.core.model.DummyUserDetails"
                cdoId 5
            }
        }

        when:
        def idHolder = javersTestAssembly().jsonConverter.fromJson(json.toString(), IdHolder)

        then:
        idHolder.id instanceof ValueObjectId
        idHolder.id == valueObjectId(5,DummyUserDetails,"dummyAddress")
    }
}
