package org.javers.guava.multimap

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap
import com.google.gson.reflect.TypeToken
import org.javers.common.exception.JaversException
import org.javers.core.diff.appenders.AbstractDiffAppendersTest
import org.javers.core.model.DummyAddress
import org.javers.core.model.SnapshotEntity
import spock.lang.Unroll

import static org.javers.core.diff.appenders.MapChangeAssert.getAssertThat
import static org.javers.guavasupport.MultimapBuilder.create
import static org.javers.repository.jql.InstanceIdDTO.instanceId
import static org.javers.repository.jql.ValueObjectIdDTO.valueObjectId
import static org.javers.common.exception.JaversExceptionCode.VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY

/**
 * @author akrystian
 */
class MultimapChangeAppenderTest extends AbstractDiffAppendersTest {

    @Unroll
    def "should append EntryAdded for #what"() {
        when:
        def change = multimapChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, property))

        then:
        assertThat(change).hasSize(1)
                .hasEntryAdded(expectedKey, expectedVal)

        where:
        what << ["Multimap<Entity,Entity>", "Multimap<Primitive,Entity>", "Multimap<Primitive,ValueObject>"]
        leftCdo << [new SnapshotEntity(id: 1)] * 3
        rightCdo << [
                new SnapshotEntity(id: 1, multiMapEntityToEntity: create((new SnapshotEntity(id: 2)): [new SnapshotEntity(id: 3)])),
                new SnapshotEntity(id: 1, multiMapPrimitiveToEntity: create(a: [new SnapshotEntity(id: 2)])),
                new SnapshotEntity(id: 1, multimapPrimitiveToValueObject: create(a: [new DummyAddress("London")]))
        ]
        property << ["multiMapEntityToEntity", "multiMapPrimitiveToEntity", "multimapPrimitiveToValueObject"]
        expectedKey << [instanceId(2, SnapshotEntity), "a", "a"]
        expectedVal << [instanceId(3, SnapshotEntity),
                        instanceId(2, SnapshotEntity),
                        valueObjectId(1, SnapshotEntity, "multimapPrimitiveToValueObject/a/fe9f8f0d164b49489301b7eaefc00c13")]
    }

    @Unroll
    def "should append EntryRemoved for #what"() {
        when:
        def change = multimapChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, property))

        then:
        assertThat(change).hasSize(1)
                .hasEntryRemoved(expectedKey, expectedVal)

        where:
        what << ["Multimap<Entity,Entity>", "Multimap<Primitive,Entity>", "Multimap<Primitive,ValueObject>"]
        leftCdo << [
                new SnapshotEntity(id: 1, multiMapEntityToEntity: create((new SnapshotEntity(id: 2)): [new SnapshotEntity(id: 3)])),
                new SnapshotEntity(id: 1, multiMapPrimitiveToEntity: create(a: [new SnapshotEntity(id: 2)])),
                new SnapshotEntity(id: 1, multimapPrimitiveToValueObject: create(a: [new DummyAddress("London")]))
        ]
        rightCdo << [new SnapshotEntity(id: 1)] * 3
        property << ["multiMapEntityToEntity", "multiMapPrimitiveToEntity", "multimapPrimitiveToValueObject"]
        expectedKey << [instanceId(2, SnapshotEntity), "a", "a"]
        expectedVal << [instanceId(3, SnapshotEntity),
                        instanceId(2, SnapshotEntity),
                        valueObjectId(1, SnapshotEntity, "multimapPrimitiveToValueObject/a/fe9f8f0d164b49489301b7eaefc00c13")]
    }


    def "should not support Map<ValueObject,?>"() {
        when:
        multimapChangeAppender().supports(getJaversType(new TypeToken<Multimap<DummyAddress, String>>() {}.getType()))

        then:
        def e = thrown(JaversException)
        e.code == VALUE_OBJECT_IS_NOT_SUPPORTED_AS_MAP_KEY
    }

    def "should EntryAdded & EntryRemoved when key is changed"() {
        given:
        def leftCdo = new SnapshotEntity(id: 1, multiMapEntityToEntity: create((new SnapshotEntity(id: 10)): [new SnapshotEntity(id: 5)]))
        def rightCdo = new SnapshotEntity(id: 1, multiMapEntityToEntity: create((new SnapshotEntity(id: 12)): [new SnapshotEntity(id: 5)]))
        when:
        def change = multimapChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "multiMapEntityToEntity"))

        then:
        assertThat(change).hasSize(2)
                .hasEntryRemoved(instanceId(10, SnapshotEntity), instanceId(5, SnapshotEntity))
                .hasEntryAdded(instanceId(12, SnapshotEntity), instanceId(5, SnapshotEntity))
    }

    def "should handle different multimap implementation"(){
        given:
        def leftCdo = new SnapshotEntity(id: 1, multiMapEntityToEntity: ArrayListMultimap.create(
                create((new SnapshotEntity(id: 10)): [new SnapshotEntity(id: 5)]))
        )
        def rightCdo = new SnapshotEntity(id: 1, multiMapEntityToEntity: HashMultimap.create(
                create((new SnapshotEntity(id: 12)): [new SnapshotEntity(id: 5)]))
        )
        when:
        def change = multimapChangeAppender()
                .calculateChanges(realNodePair(leftCdo, rightCdo), getProperty(SnapshotEntity, "multiMapEntityToEntity"))

        then:
        assertThat(change).hasSize(2)
                .hasEntryRemoved(instanceId(10, SnapshotEntity), instanceId(5, SnapshotEntity))
                .hasEntryAdded(instanceId(12, SnapshotEntity), instanceId(5, SnapshotEntity))
    }
}

