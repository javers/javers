package org.javers.repository.mongo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import org.javers.core.commit.Commit;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * L1 cache: snapshots written in one transaction.
 * Bounded, ids of snapshots dropped because of the size limit are kept in the overflow set,
 * so they can be evicted from L2 when the transaction commits.
 * <br/><br/>
 *
 * Why: on commit, L1 is copied to L2, so L2 gets the snapshots written by the transaction.
 * A dropped snapshot can't be copied, and L2 may still hold its older, committed version.
 * Without the eviction, L2 would keep serving that stale version after the commit.
 * Not thread-safe.
 *
 * See also {@link LatestSnapshotCache} for L2 (global) cache.
 */
class TransactionLocalLatestSnapshotCache {
    private final Set<GlobalId> overflow = new HashSet<>();
    private final Cache<GlobalId, CdoSnapshot> cache;

    TransactionLocalLatestSnapshotCache(int maxSize) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(maxSize)
                .recordStats()
                .<GlobalId, CdoSnapshot>removalListener(notification -> {
                    if (notification.getCause() == RemovalCause.SIZE) {
                        overflow.add(notification.getKey());
                    }
                })
                .build();
    }

    Optional<CdoSnapshot> get(GlobalId globalId) {
        return Optional.ofNullable(cache.getIfPresent(globalId));
    }

    void putAll(Commit commit) {
        commit.getSnapshots().forEach(this::put);
    }

    void put(CdoSnapshot snapshot) {
        cache.put(snapshot.getGlobalId(), snapshot);
    }

    boolean isOverflowed(GlobalId globalId) {
        return overflow.contains(globalId);
    }

    /**
     * Copies L1 to L2. Snapshots dropped from L1 (overflow) can't be copied,
     * so their ids are evicted from L2, which may still hold older versions.
     */
    void promoteToL2(LatestSnapshotCache l2) {
        overflow.forEach(l2::evict);
        cache.asMap().values().forEach(l2::put);
    }

    CacheStats stats() {
        var stats = cache.stats();
        return CacheStats.l1(stats.hitCount(), stats.missCount(), cache.size());
    }
}
