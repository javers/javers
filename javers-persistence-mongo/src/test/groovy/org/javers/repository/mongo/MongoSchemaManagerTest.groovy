package org.javers.repository.mongo

import com.mongodb.client.MongoDatabase
import spock.lang.Specification

class MongoSchemaManagerTest extends Specification {
    def 'index should be dropped by default'() {
        given:
        final snapshots = new SnapshotsStub()
        final factory = { wrappedData -> snapshots }
        final useTypeNameIndex = false

        final manager = new MongoSchemaManager(Stub(MongoDatabase.class), useTypeNameIndex, factory,
                Stub(CommitIdTypeMigration.class))

        when:
        manager.ensureSchema()

        then:
        assert 1 == snapshots.invocationsCount
    }

    def 'index should not be dropped if required'() {
        given:
        final snapshots = new SnapshotsStub()
        final factory = { wrappedData -> snapshots }
        final useTypeNameIndex = true

        final manager = new MongoSchemaManager(Stub(MongoDatabase.class), useTypeNameIndex, factory,
                Stub(CommitIdTypeMigration.class))

        when:
        manager.ensureSchema()

        then:
        assert 0 == snapshots.invocationsCount
    }

    static class SnapshotsStub extends Snapshots {
        int invocationsCount

        SnapshotsStub() {
            super(null)
            invocationsCount = 0
        }

        @Override
        void dropGlobalIdEntityIndex() {
            invocationsCount++
        }
    }
}
