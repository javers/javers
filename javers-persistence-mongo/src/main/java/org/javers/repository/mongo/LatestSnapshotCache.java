package org.javers.repository.mongo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Optional;

import com.mongodb.client.ClientSession;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.jspecify.annotations.Nullable;

import java.util.function.BiFunction;

/**
 * Warning: does <b>not</b> support transactions.<br/>
 * If the cache was changed within a transaction, rolling back the transaction won't roll back the cache.
 *
 * @author bartosz.walacik
 */
class LatestSnapshotCache {
    private final Cache<GlobalId, Optional<CdoSnapshot>> cache;
    private final BiFunction<GlobalId, @Nullable ClientSession, Optional<CdoSnapshot>> source;
    private final boolean disabled;

    LatestSnapshotCache(int size, BiFunction<GlobalId, @Nullable ClientSession, Optional<CdoSnapshot>> source) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(size)
                .build();

        this.source = source;
        this.disabled = size == 0;
    }

    Optional<CdoSnapshot> getLatest(GlobalId globalId, @Nullable ClientSession clientSession) {
        if (disabled) {
            return getValueFromSource(globalId, clientSession);
        }

        Optional<CdoSnapshot> fromCache = cache.getIfPresent(globalId);

        if (fromCache != null) {
            return fromCache;
        }

        Optional<CdoSnapshot> fromDb = getValueFromSource(globalId, clientSession);
        cache.put(globalId, fromDb);
        return fromDb;
    }

    private Optional<CdoSnapshot> getValueFromSource(final GlobalId globalId, final @Nullable ClientSession clientSession) {
        return source.apply(globalId, clientSession);
    }

    void put(CdoSnapshot cdoSnapshot) {
        if (disabled) {
            return;
        }
        cache.put(cdoSnapshot.getGlobalId(), Optional.of(cdoSnapshot));
    }
}
