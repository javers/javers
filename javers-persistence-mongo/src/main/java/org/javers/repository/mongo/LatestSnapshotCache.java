package org.javers.repository.mongo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.util.concurrent.UncheckedExecutionException;
import java.util.Optional;

import org.javers.core.commit.Commit;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;

import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * L2 (global) cache. Should contain only snapshots committed to DB.
 *
 * See also {@link TransactionLocalLatestSnapshotCache} for L1 (transaction-local) cache.
 *
 * @author bartosz.walacik
 */
class LatestSnapshotCache {
    private final Cache<GlobalId, Optional<CdoSnapshot>> cache;
    private final Function<GlobalId, Optional<CdoSnapshot>> source;
    private final boolean disabled;
    private final int maxSize;

    LatestSnapshotCache(int size, Function<GlobalId, Optional<CdoSnapshot>> source) {
        this.maxSize = size;
        cache = CacheBuilder.newBuilder()
                .maximumSize(size)
                .recordStats()
                .build();

        this.source = source;
        this.disabled = size == 0;
    }

    Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        if (disabled) {
            return source.apply(globalId);
        }

        // Guava loads it atomically: if a newer snapshot is put while the old one is loaded from DB,
        // the loaded one is discarded instead of overwriting the newer one
        try {
            return cache.get(globalId, () -> source.apply(globalId));
        } catch (ExecutionException | UncheckedExecutionException e) {
            Throwable cause = e.getCause();
            throw cause instanceof RuntimeException ? (RuntimeException) cause : new RuntimeException(cause);
        }
    }

    int maxSize() {
        return maxSize;
    }

    CacheStats stats() {
        var stats = cache.stats();
        return CacheStats.l2(stats.hitCount(), stats.missCount(), cache.size());
    }

    void putAll(Commit commit) {
        if (disabled) {
            return;
        }
        commit.getSnapshots().forEach(this::put);
    }
    void put(CdoSnapshot cdoSnapshot) {
        if (disabled) {
            return;
        }
        cache.put(cdoSnapshot.getGlobalId(), Optional.of(cdoSnapshot));
    }

    void evict(GlobalId globalId) {
        cache.invalidate(globalId);
    }
}
