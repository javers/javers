package org.javers.repository.mongo;

public final class CacheStats {

    public enum CacheType { L1, L2 }

    private final CacheType type;
    private final long hits;
    private final long misses;
    private final long size;

    CacheStats(CacheType type, long hits, long misses, long size) {
        this.type = type;
        this.hits = hits;
        this.misses = misses;
        this.size = size;
    }

    static CacheStats l2(long hits, long misses, long size) {
        return new CacheStats(CacheType.L2, hits, misses, size);
    }

    public static CacheStats l1(long hits, long misses, long size) {
        return new CacheStats(CacheType.L1, hits, misses, size);
    }

    public CacheType type() { return type; }
    public long hits() { return hits; }
    public long misses() { return misses; }
    public long size() { return size; }

    @Override
    public String toString() {
        return type + "{hits=" + hits + ", misses=" + misses + ", size=" + size + "}";
    }
}
