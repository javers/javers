package org.javers.repository.mongo;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.Optional;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;

import java.util.function.Function;

/**
 * @author bartosz.walacik
 */
class LatestSnapshotCache {
    private final Cache<GlobalId, Optional<CdoSnapshot>> cache;
    private final Function<GlobalId, Optional<CdoSnapshot>> source;
    private final boolean disabled;

    LatestSnapshotCache(int size, Function<GlobalId, Optional<CdoSnapshot>> source) {
        cache = CacheBuilder.newBuilder()
                .maximumSize(size)
                .build();

        this.source = source;
        this.disabled = size == 0;
    }

    Optional<CdoSnapshot> getLatest(GlobalId globalId) {
        if (disabled) {
            return source.apply(globalId);
        }

        Optional<CdoSnapshot> fromCache = cache.getIfPresent(globalId);

        if (fromCache != null) {
            return fromCache;
        }

        Optional<CdoSnapshot> fromDb = source.apply(globalId);
        cache.put(globalId, fromDb);
        return fromDb;
    }

    void put(CdoSnapshot cdoSnapshot) {
        if (disabled) {
            return;
        }
        cache.put(cdoSnapshot.getGlobalId(), Optional.of(cdoSnapshot));
    }
}
