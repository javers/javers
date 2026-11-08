package org.javers.repository.mongo

import com.mongodb.client.ClientSession
import org.javers.core.model.SnapshotEntity
import org.javers.core.metamodel.object.GlobalId
import spock.lang.Specification

import java.util.function.BiFunction

import static org.javers.core.JaversBuilder.javers

/**
 * Pins down the current behavior of {@link LatestSnapshotCache} with respect to the
 * {@link ClientSession} parameter introduced to thread Mongo transactions through read queries.
 * <br/><br/>
 *
 * IMPORTANT: the cache key is only the {@link GlobalId}; it does not take the
 * {@link ClientSession} into account. This means a value cached from a read made outside
 * (or inside a different) transaction can be served for a read made inside an active
 * transaction, instead of the source being re-queried with that transaction's session.
 */
class LatestSnapshotCacheTest extends Specification {

    static def someId() {
        def javers = javers().build()
        javers.commit("author", new SnapshotEntity(id: 1))
        javers.getLatestSnapshot(1, SnapshotEntity).get().globalId
    }

    def "cache should bypass the source entirely once warmed, even when a ClientSession is given on a later call"() {
        given:
        def id = someId()
        def session = Stub(ClientSession)
        def outOfTransactionSnapshot = new Object()
        def inTransactionSnapshot = new Object()
        int callCount = 0
        BiFunction source = { givenId, clientSession ->
            callCount++
            // the source would return a different (fresher, in-transaction) value when a session is given
            clientSession == null ? Optional.of(outOfTransactionSnapshot) : Optional.of(inTransactionSnapshot)
        }
        def cache = new LatestSnapshotCache(100, source)

        when: "first read happens outside of any transaction and gets cached"
        def firstResult = cache.getLatest(id, null)

        then:
        firstResult.get().is(outOfTransactionSnapshot)
        callCount == 1

        when: "second read happens inside an active transaction"
        def secondResult = cache.getLatest(id, session)

        then:
        // KNOWN LIMITATION: the stale snapshot is returned instead of
        // re-querying the source with the active or a different session.
        secondResult.get().is(outOfTransactionSnapshot)
        callCount == 1
    }

    def "a disabled cache (size 0) should always call the source with the given session and never retain state"() {
        given:
        def id = someId()
        List seenSessions = []
        def session = Stub(ClientSession)
        BiFunction source = { givenId, clientSession ->
            seenSessions << clientSession
            Optional.of(new Object())
        }
        def cache = new LatestSnapshotCache(0, source)

        when:
        cache.getLatest(id, null)
        cache.getLatest(id, session)

        then:
        seenSessions == [null, session]
    }

    def "put() should warm the cache independently of any transaction, and later session-scoped reads still get the stale value"() {
        given:
        def javers = javers().build()
        javers.commit("author", new SnapshotEntity(id: 2))
        def snapshot = javers.getLatestSnapshot(2, SnapshotEntity).get()
        int callCount = 0
        BiFunction source = { givenId, clientSession ->
            callCount++
            Optional.of(new Object())
        }
        def cache = new LatestSnapshotCache(100, source)

        when:
        cache.put(snapshot)
        def result = cache.getLatest(snapshot.globalId, Stub(ClientSession))

        then:
        result.get().is(snapshot)
        callCount == 0
    }
}
