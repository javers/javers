package org.javers.core

import groovy.json.JsonSlurper
import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.DiffAssert
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.core.diff.changetype.NewObject
import org.javers.core.diff.changetype.PropertyChange
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.diff.changetype.ValueChange
import org.javers.core.diff.changetype.container.ListChange
import org.javers.core.diff.changetype.container.ValueAdded
import org.javers.core.examples.model.Person
import org.javers.core.json.DummyPointJsonTypeAdapter
import org.javers.core.json.DummyPointNativeTypeAdapter
import org.javers.core.metamodel.annotation.DiffInclude
import org.javers.core.metamodel.annotation.Id
import org.javers.core.metamodel.property.Property
import org.javers.core.model.*
import spock.lang.Unroll

import javax.persistence.EmbeddedId

import static org.javers.core.JaversBuilder.javers
import static org.javers.core.MappingStyle.BEAN
import static org.javers.core.MappingStyle.FIELD
import static org.javers.core.metamodel.clazz.EntityDefinitionBuilder.entityDefinition
import static org.javers.core.metamodel.clazz.ValueObjectDefinitionBuilder.valueObjectDefinition
import static org.javers.core.model.DummyUser.Sex.FEMALE
import static org.javers.core.model.DummyUser.Sex.MALE
import static org.javers.core.model.DummyUser.dummyUser
import static org.javers.core.model.DummyUserWithPoint.userWithPoint
import static org.javers.repository.jql.InstanceIdDTO.instanceId

/**
 * @author bartosz walacik
 */
class JaversDiffE2ETest extends AbstractDiffTest {

    class PropsClass {
        @DiffInclude int id
        int a
        int b
    }

    @Unroll
    def "should ignore all props of #classType which are not in the 'included' list of properties"(){
      given:
      def javers = JaversBuilder.javers().registerType(definition).build()

      when:
      def left =  new PropsClass(id:1, a:2, b:3)
      def right = new PropsClass(id:1, a:4, b:6)
      def diff = javers.compare(left, right)

      then:
      !diff.changes.size()

      where:
      definition << [entityDefinition(PropsClass)
                             .withIdPropertyName("id").build(),
                     valueObjectDefinition(PropsClass)
                             .withIncludedProperties(["id"]).build()
      ]
      classType << ["EntityType", "ValueObjectType"]
    }

    def "should extract Property from PropertyChange"(){
      given:
      def javers = JaversTestBuilder.newInstance()

      when:
      def diff = javers.compare(new Person('1','bob'), new Person('1','bobby'))
      PropertyChange propertyChange = diff.changes[0]

      Property property = javers.getProperty( propertyChange )

      then:
      property.name == 'name'
      !property.looksLikeId()
    }

    def "should use reflectiveToString() to build InstanceId"(){
        given:
        def javers = JaversTestBuilder.newInstance()
        def left  = new DummyEntityWithEmbeddedId(point: new DummyPoint(1,2), someVal: 5)
        def right = new DummyEntityWithEmbeddedId(point: new DummyPoint(1,2), someVal: 6)

        when:
        def diff = javers.compare(left,right)

        then:
        DiffAssert.assertThat(diff).hasChanges(1).hasValueChangeAt("someVal",5,6)

        diff.changes[0].affectedGlobalId.value().endsWith("DummyEntityWithEmbeddedId/1,2")
    }

    class DummyCompositePoint {
        @EmbeddedId DummyPoint dummyPoint
        int value
    }

    def "should use custom toString function when provided for building InstanceId"(){
        given:
        def javers = JaversBuilder.javers().registerValueWithCustomToString(DummyPoint, {x -> x.getStringId()}).build()
        def left  = new DummyCompositePoint(dummyPoint: new DummyPoint(1,2), value:5)
        def right = new DummyCompositePoint(dummyPoint: new DummyPoint(1,2), value:6)

        when:
        def diff = javers.compare(left,right)

        then:
        DiffAssert.assertThat(diff).hasChanges(1).hasValueChangeAt("value",5,6)
        diff.changes.get(0).affectedGlobalId.value() == DummyCompositePoint.class.name+"/(1,2)"
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

    def "should use nested Entity Id value as Id of parent Entity"(){
        given:
        def javers = javers().build()
        def left  = new DummyWithEntityId(entityAsId: new EntityAsId(id: 1, value:5), value:5)
        def right = new DummyWithEntityId(entityAsId: new EntityAsId(id: 1, value:5), value:6)

        when:
        def diff = javers.compare(left,right)

        then:

        println javers.getTypeMapping(DummyWithEntityId).prettyPrint()
        println javers.getTypeMapping(EntityAsId).prettyPrint()

        DiffAssert.assertThat(diff).hasChanges(1).hasValueChangeAt("value",5,6)
        diff.changes[0].affectedGlobalId.value() == DummyWithEntityId.name+"/1"
    }

    def "should create NewObject for all nodes in initial diff"() {
        given:
        def javers = JaversTestBuilder.newInstance()
        DummyUser left = dummyUser().withDetails()

        when:
        def diff = javers.initial(left)

        then:
        DiffAssert.assertThat(diff).has(2, NewObject)
    }

    def "should not create properties snapshot of NewObject by default"() {
        given:
        def javers = JaversBuilder.javers().build()
        def left =  new DummyUser(name: "kazik")
        def right = new DummyUser(name: "kazik", dummyUserDetails: new DummyUserDetails(id: 1, someValue: "some"))

        when:
        def diff = javers.compare(left, right)

        then:
        DiffAssert.assertThat(diff).hasChanges(2)
                  .hasNewObject(instanceId(1,DummyUserDetails))
                  .hasReferenceChangeAt("dummyUserDetails",null,instanceId(1,DummyUserDetails))
    }

    def "should create properties snapshot of NewObject only when configured"() {
        given:
        def javers = JaversBuilder.javers().withNewObjectsSnapshot(true).build()
        def left =  new DummyUser(name: "kazik")
        def right = new DummyUser(name: "kazik", dummyUserDetails: new DummyUserDetails(id: 1, someValue: "some"))

        when:
        def diff = javers.compare(left, right)

        then:
        DiffAssert.assertThat(diff)
                .hasNewObject(instanceId(1,DummyUserDetails))
                .hasValueChangeAt("id",null,1)
                .hasValueChangeAt("someValue",null,"some")
    }

    def "should create valueChange with Enum" () {
        given:
        def user =  dummyUser().withSex(FEMALE)
        def user2 = dummyUser().withSex(MALE)
        def javers = JaversTestBuilder.newInstance()

        when:
        def diff = javers.compare(user, user2)

        then:
        diff.changes.size() == 1
        def change = diff.changes[0]
        change.left == FEMALE
        change.right == MALE
    }

    def "should serialize whole Diff"() {
        given:
        def user =  dummyUser().withSex(FEMALE)
        def user2 = dummyUser().withSex(MALE).withDetails()
        def javers = JaversTestBuilder.newInstance()

        when:
        def diff = javers.compare(user, user2)
        def jsonText = javers.getJsonConverter().toJson(diff)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.changes.size() == 3
        json.changes[0].changeType == "NewObject"
        json.changes[1].changeType == "ValueChange"
        json.changes[2].changeType == "ReferenceChange"
    }

    def "should support custom JsonTypeAdapter for ValueChange"() {
        given:
        def javers = javers().registerValueTypeAdapter( new DummyPointJsonTypeAdapter() )
                             .build()

        when:
        def diff = javers.compare(userWithPoint(1,2), userWithPoint(1,3))
        def jsonText = javers.getJsonConverter().toJson(diff)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        def change = json.changes[0];
        change.globalId.valueObject == "org.javers.core.model.DummyUserWithPoint"
        change.changeType == "ValueChange"
        change.property == "point"
        change.left == "1,2" //this is most important in this test
        change.right == "1,3" //this is most important in this test
    }

    def "should support custom native Gson TypeAdapter"() {
        given:
        def javers = javers()
                .registerValueGsonTypeAdapter(DummyPoint, new DummyPointNativeTypeAdapter() )
                .build()

        when:
        def diff = javers.compare(userWithPoint(1,2), userWithPoint(1,3))
        def jsonText = javers.getJsonConverter().toJson(diff)
        //println("jsonText:\n"+jsonText)

        then:
        def json = new JsonSlurper().parseText(jsonText)
        json.changes[0].left == "1,2"
        json.changes[0].right == "1,3"
    }

    def "should understand primitive default values when creating NewObject snapshot"() {
        given:
        def javers = javers().build()

        when:
        def diff = javers.initial(new PrimitiveEntity())

        then:
        DiffAssert.assertThat(diff).hasOnly(1, NewObject)
    }

    def "should understand primitive default values when creating ValueChange"() {
        given:
        def javers = javers().build()

        when:
        def diff = javers.compare(new PrimitiveEntity(), new PrimitiveEntity())

        then:
        DiffAssert.assertThat(diff).hasChanges(0)
    }

    def "should serialize the Diff object"() {
        given:
        def javers = javers().build()
        def user =  new DummyUser(name:"id", sex: MALE,   age: 5, stringSet: ["a"])
        def user2 = new DummyUser(name:"id", sex: FEMALE, age: 6, stringSet: ["b"])
        def tmpFile = File.createTempFile("serializedDiff", ".ser")

        when:
        def diff = javers.compare(user, user2)

        //serialize diff
        new ObjectOutputStream(new FileOutputStream(tmpFile.path)).writeObject(diff)

        //deserialize diff
        def deserializedDiff = new ObjectInputStream(new FileInputStream(tmpFile.path)).readObject()

        then:
        List changes = deserializedDiff.changes
        changes.size() == 3

        def ageChange = changes.find {it.propertyName == "age"}
        ageChange.left == 5
        ageChange.right == 6
        ageChange.affectedGlobalId.cdoId == "id"
        ageChange.affectedGlobalId.typeName == "org.javers.core.model.DummyUser"

        changes.count{ it.propertyName == "age" } // == 1

        changes.count{ it.propertyName == "stringSet" } // == 1
    }

    def "should compare ShallowReferences using regular ReferenceChange"() {
        given:
        def javers = javers().build()
        def left =  new SnapshotEntity(id:1, shallowPhone: new ShallowPhone(1))
        def right = new SnapshotEntity(id:1, shallowPhone: new ShallowPhone(2))

        when:
        ReferenceChange change = javers.compare(left, right).changes.find{it instanceof  ReferenceChange}

        then:
        change.left.value() == ShallowPhone.name+"/1"
        change.right.value() == ShallowPhone.name+"/2"
    }

    def "should not compare properties when class is mapped as ShallowReference"() {
        given:
        def javers = javers().build()
        def left =  new SnapshotEntity(id:1, shallowPhone: new ShallowPhone(1, "123", new CategoryC(1)))
        def right = new SnapshotEntity(id:1, shallowPhone: new ShallowPhone(1, "321", new CategoryC(2)))

        expect:
        javers.compare(left, right).hasChanges() == false
    }

    @Unroll
    def "should use ReferenceChange when #propType is annotated as ShallowReferences"() {
        given:
        def javers = javers().withMappingStyle(mapping).build()
        def left =  new PhoneWithShallowCategory(id:1, shallowCategory:new CategoryC(1, "old shallow"))
        def right = new PhoneWithShallowCategory(id:1, shallowCategory:new CategoryC(2, "new shallow"))

        when:
        def changes = javers.compare(left, right).getChangesByType(ReferenceChange)

        then:
        changes.size() == 1
        changes[0] instanceof ReferenceChange
        changes[0].left.value() == CategoryC.name+"/1"
        changes[0].right.value() == CategoryC.name+"/2"

        where:
        propType << ["field", "getter"]
        mapping <<  [FIELD, BEAN]
    }

    @Unroll
    def "should not compare properties when #propType is annotated as ShallowReference"() {
        given:
        def javers = javers().withMappingStyle(mapping).build()
        def left =  new PhoneWithShallowCategory(id:1, shallowCategory:new CategoryC(1, "old shallow"), deepCategory:new CategoryC(2, "old deep"))
        def right = new PhoneWithShallowCategory(id:1, shallowCategory:new CategoryC(1, "new shallow"), deepCategory:new CategoryC(2, "new deep"))

        when:
        def changes = javers.compare(left, right).changes

        then:
        changes.size() == 1
        changes[0] instanceof ValueChange
        changes[0].left == "old deep"
        changes[0].right == "new deep"

        where:
        propType << ["field", "getter"]
        mapping <<  [FIELD, BEAN]
    }

    def "should ignore properties with @DiffIgnore or @Transient"(){
        given:
        def javers = javers().build()
        def left =  new DummyUser(name:'name', propertyWithTransientAnn:1, propertyWithDiffIgnoreAnn:1)
        def right = new DummyUser(name:'name', propertyWithTransientAnn:2, propertyWithDiffIgnoreAnn:2)

        expect:
        javers.compare(left, right).changes.size() == 0
    }

    def "should ignore properties declared in a class with @IgnoreDeclaredProperties"(){
        given:
        def javers = javers().build()
        def left =  new DummyIgnoredPropertiesType(name:"bob", age: 15, propertyThatShouldBeIgnored: 1, anotherIgnored: 1)
        def right = new DummyIgnoredPropertiesType(name:"bob", age: 16, propertyThatShouldBeIgnored: 2, anotherIgnored: 2)

        when:
        def diff = javers.compare(left, right)

        then:
        diff.changes.size() == 1
        diff.changes[0].propertyName == "age"
    }

    def "should compare Lists as Sets when ListCompareAlgorithm.SET is enabled"(){
      given:
      def javers = javers().withListCompareAlgorithm(ListCompareAlgorithm.AS_SET).build()
      def left =  new DummyUser(name:"bob", stringList: ['z', 'a', 'b'])
      def right = new DummyUser(name:"bob", stringList: ['cc', 'b', 'z', 'a'])

      when:
      def diff = javers.compare(left, right)

      then:
      diff.changes.size() == 1
      def change = diff.changes[0]
      change instanceof ListChange
      change.changes.size() == 1
      change.changes[0] instanceof ValueAdded
      change.changes[0].addedValue == 'cc'
    }

    class SetValueObject {
        String some
        SnapshotEntity ref
    }

    class ValueObjectHolder {
        @Id int id
        Set<SetValueObject> valueObjects
    }

    def "should follow and deeply compare entities referenced from ValueObjects inside Set"(){
      given:
      def javers = javers().build()
      def left = new ValueObjectHolder(id:1, valueObjects:
                [new SetValueObject(some:'a'),
                 new SetValueObject(some:'b', ref: new SnapshotEntity(id:1, intProperty:5))
                ])
      def right= new ValueObjectHolder(id:1, valueObjects:
                [new SetValueObject(some:'b', ref: new SnapshotEntity(id:1, intProperty:6)),
                 new SetValueObject(some:'a')
                ])

      when:
      def changes = javers.compare(left, right).changes

      then:
      changes.size() == 1
      changes[0].affectedGlobalId.value() == SnapshotEntity.getName()+"/1"
      changes[0].left == 5
      changes[0].right == 6
    }
}
