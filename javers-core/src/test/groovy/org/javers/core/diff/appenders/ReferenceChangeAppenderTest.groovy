package org.javers.core.diff.appenders

import org.javers.core.model.DummyAddress
import org.javers.core.diff.ChangeAssert
import org.javers.core.model.SnapshotEntity
import static ReferenceChangeAssert.assertThat

class ReferenceChangeAppenderTest extends AbstractDiffAppendersTest {

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

    def "should compare null refs safely"() {
        given:
        def entityRef = new SnapshotEntity(id: 2)
        def leftCdo =   new SnapshotEntity(id:1, entityRef: entityRef)
        def rightCdo =  new SnapshotEntity(id:1, entityRef: null)
        def property = getProperty(SnapshotEntity, "entityRef")

        when:
        def change = new ReferenceChangeAppender()
                        .calculateChanges(realNodePair(leftCdo, rightCdo), property)

        then:
        assertThat(change)
                  .hasLeftReference(SnapshotEntity,2)
                  .hasRightReference(null)
                  .hasLeftObject(entityRef)
                  .hasRightObject(null)
                  .hasPropertyName("entityRef")
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
                   .hasLeftObject(leftCdo.entityRef)
                   .hasRightObject(rightCdo.entityRef)
                   .hasPropertyName("entityRef")
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
