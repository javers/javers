package org.javers.core.diff.appenders

import org.javers.core.diff.AbstractDiffTest
import org.javers.core.diff.ChangeAssert
import org.javers.core.diff.RealNodePair
import org.javers.core.diff.changetype.ReferenceChange
import org.javers.core.model.DummyAddress
import org.javers.core.model.DummyUser
import org.javers.core.model.DummyUserDetails
import org.javers.core.metamodel.property.Property
import org.javers.core.graph.ObjectNode
import org.javers.core.model.SnapshotEntity

import static ReferenceChangeAssert.assertThat
import static org.javers.test.builder.DummyUserBuilder.dummyUser

class ReferenceChangeAppenderTest extends AbstractDiffTest{

    def "should not append change when the same references"() {
        given:
        def leftCdo =   new SnapshotEntity(id:1, entityRef: new SnapshotEntity(id:2))
        def rightCdo =  new SnapshotEntity(id:1, entityRef: new SnapshotEntity(id:2))
        def property = getProperty(SnapshotEntity, "entityRef")

        when:
        def change = new ReferenceChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), property)

        then:
        !change
    }

    def "should compare refs null safely"() {
        given:
        def leftCdo =   new SnapshotEntity(id:1, entityRef: new SnapshotEntity(id:2))
        def rightCdo =  new SnapshotEntity(id:1, entityRef: null)
        def property = getProperty(SnapshotEntity, "entityRef")

        when:
        def change = new ReferenceChangeAppender()
                        .calculateChanges(realNodePair(leftCdo, rightCdo), property)

        then:
        assertThat(change)
                  .hasLeftReference(SnapshotEntity,2)
                  .hasRightReference(null)
                  .hasProperty(property)
    }

    def "should append Entity reference change"() {
        given:
        def leftCdo =   new SnapshotEntity(id:1, entityRef: new SnapshotEntity(id:2))
        def rightCdo =  new SnapshotEntity(id:1, entityRef: new SnapshotEntity(id:3))
        def property = getProperty(SnapshotEntity, "entityRef")

        when:
        def change = new ReferenceChangeAppender()
                    .calculateChanges(realNodePair(leftCdo, rightCdo), property)

        then:
        ChangeAssert.assertThat(change)
                    .hasInstanceId(SnapshotEntity, 1)
        assertThat(change)
                  .hasLeftReference(SnapshotEntity,2)
                  .hasRightReference(SnapshotEntity,3)
                  .hasProperty(property)
    }

    def "should NOT append ValueObject reference change"() {
        given:
            def leftCdo =  new SnapshotEntity(id:1, valueObjectRef: new DummyAddress("London"))
            def rightCdo = new SnapshotEntity(id:1, valueObjectRef: new DummyAddress("London","City"))
            def property = getProperty(SnapshotEntity, "valueObjectRef")

        when:
            def change = new ReferenceChangeAppender()
                            .calculateChanges(realNodePair(leftCdo, rightCdo), property)

        then:
            !change
    }

}
