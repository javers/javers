package org.javers.repository.redis;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.SerializationUtils.deserialize;
import static org.apache.commons.lang3.SerializationUtils.serialize;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.*;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.core.metamodel.type.ValueObjectType;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.SnapshotIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.params.ZRangeParams;
import redis.clients.jedis.util.Pool;

public class JaversRedisRepository implements JaversRepository {

    private static final Logger log = LoggerFactory.getLogger(JaversRedisRepository.class);
    public static final String JV_SNAPSHOTS = "jv_snapshots:";
    public static final String JV_SNAPSHOTS_ENTITY_KEYS = "jv_snapshots_keys";
    public static final String JV_SNAPSHOTS_ENTITY_KEYS_SET = "jv_snapshots_keys_set";
    private static final byte[] JV_HEAD_ID = "jv_head_id".getBytes();

    private JsonConverter jsonConverter;
    private final Pool<Jedis> jedisPool;
    private final long duration;
    private final ExecutorService executor;

    public JaversRedisRepository(final JedisPool jedisPool, final Duration duration) {
        this.jedisPool = jedisPool;
        this.duration = duration.toSeconds();
        this.executor = Executors.newSingleThreadExecutor();
        initializeSubscriber();
    }

    public JaversRedisRepository(final JedisSentinelPool jedisSentinelPool, final Duration duration) {
        this.jedisPool = jedisSentinelPool;
        this.duration = duration.toSeconds();
        this.executor = Executors.newSingleThreadExecutor();
        initializeSubscriber();
    }

    public void shutdown() {
        executor.shutdown();
    }

    @Override
    public CommitId getHeadId() {
        try (final var jedis = jedisPool.getResource()) {
            final var headIdByteArray = jedis.get(JV_HEAD_ID);
            return (CommitId) Optional.ofNullable(headIdByteArray)
                    .map(a -> deserialize(a))
                    .orElse(new CommitId(0, 0));
        }
    }

    @Override
    public void persist(final Commit commit) {
        Validate.conditionFulfilled(Objects.nonNull(jsonConverter), "jsonConverter is null");
        Validate.argumentsAreNotNull(commit);
        commit.getSnapshots().forEach(this::persist);
        try (final var jedis = jedisPool.getResource()) {
            jedis.set(JV_HEAD_ID, serialize(commit.getId()));
        }
    }

    @Override
    public Optional<CdoSnapshot> getLatest(final GlobalId globalId) {
        final var key = key(globalId);
        try (final var jedis = jedisPool.getResource()) {
            final var cdoSnapshotJson = jedis.lrange(key, 0l, 1l);
            return cdoSnapshotJson.stream().findFirst().map(v -> jsonConverter.fromJson(v, CdoSnapshot.class));
        }
    }

    @Override
    public List<CdoSnapshot> getStateHistory(final GlobalId globalId, final QueryParams queryParams) {
        try (final var jedis = jedisPool.getResource()) {
            if (queryParams.isAggregate()) {
                final var entityKey = key(globalId);
                final var setKey = JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(globalId.getTypeName());
                final var keys = jedis.zrange(setKey, 0, -1).stream()
                        .filter(v -> v.startsWith(entityKey)).toList();
                return keys.stream().map(key -> getStateHistory(key, queryParams)).flatMap(List::stream).sorted(inReverseChronologicalOrder()).toList();
            } else {
                final var key = key(globalId);
                return getStateHistory(key, queryParams);
            }
        }
    }

    @Override
    public List<CdoSnapshot> getStateHistory(final Set<ManagedType> givenClasses, final QueryParams queryParams) {
        Validate.argumentsAreNotNull(givenClasses, queryParams);
        try (final var jedis = jedisPool.getResource()) {
            List<CdoSnapshot> result = new ArrayList<>();
            for (var givenClass : givenClasses) {

                if (givenClass instanceof ValueObjectType) {
                    var snapshotsF = getSnapshots(queryParams);
                    var snapshots = snapshotsF.stream().filter(snapshot -> snapshot.getManagedType().getName().equals(givenClass.getName())).toList();
                    result.addAll(snapshots);
                } else {
                    // Construct the Redis set key based on the given class name
                    var setKey = JV_SNAPSHOTS_ENTITY_KEYS + ":" + givenClass.getName();

                    // Define the range for fetching records
                    var start = queryParams.skip();
                    var stop = queryParams.limit() - 1;

                    // Fetch the range from Redis
                    var range = jedis.zrange(setKey, start, stop);
                    System.out.println("Range for " + setKey + ": " + range);

                    // Process each key based on its type
                    for (var key : range) {
                        if (key.contains("#")) {
                            continue;
                        }
                        List<CdoSnapshot> stateHistory;
                        var instanceId = instanceId(key);
                        stateHistory = getStateHistory(instanceId, queryParams);
                        result.addAll(stateHistory);
                    }
                }
            }
            result.sort(inReverseChronologicalOrder());
            return applyQueryParams(result, queryParams);
        }
    }

    @Override
    public List<CdoSnapshot> getValueObjectStateHistory(final EntityType ownerEntity, final String path, final QueryParams queryParams) {
        Validate.argumentsAreNotNull(ownerEntity, path, queryParams);
        try (final var jedis = jedisPool.getResource()) {
            final var setKey = JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(ownerEntity.getName());
            final var start = queryParams.skip();
            final var stop = queryParams.limit() - 1;
            final var entityKeys = jedis.zrange(setKey, start, stop);
            final var valueObjectKeys = entityKeys.stream().filter(k -> k.contains("#".concat(path))).toList();
            final var result = valueObjectKeys.stream().map(key -> getStateHistory(key, queryParams)).flatMap(List::stream)
                    .sorted(inReverseChronologicalOrder()).toList();
            return applyQueryParams(result, queryParams);
        }
    }

    @Override
    public List<CdoSnapshot> getSnapshots(final QueryParams queryParams) {
        Validate.argumentIsNotNull(queryParams);
        try (final var jedis = jedisPool.getResource()) {
            final var keys = jedis.zrange(JV_SNAPSHOTS_ENTITY_KEYS, 0, -1);
            final var allCdoSnapshots = keys.stream()
                    //.filter(k -> !k.contains("#"))
                    .map(this::instanceId)
                    .map(instanceId -> getStateHistory(instanceId, queryParams))
                    .flatMap(List::stream)
                    .skip(queryParams.skip())
                    // .limit(queryParams.limit()) // test to do fix
                    // .sorted(inReverseChronologicalOrder())
                    .toList();
            return applyQueryParams(allCdoSnapshots, queryParams);
        }
    }

    @Override
    public List<CdoSnapshot> getSnapshots(final Collection<SnapshotIdentifier> snapshotIdentifiers) {
        try (final var jedis = jedisPool.getResource()) {
            return snapshotIdentifiers.stream().map(snapshotIdentifier -> {
                final var key = key(snapshotIdentifier);
                final var size = jedis.llen(key);
                final var index = size - snapshotIdentifier.getVersion();
                return Optional.ofNullable(jedis.lrange(key, index, index)).orElse(emptyList()).stream().map(v -> jsonConverter.fromJson(v, CdoSnapshot.class))
                        .findFirst();
            }).filter(Optional::isPresent).map(Optional::get).toList();
        }
    }

    @Override
    public void setJsonConverter(final JsonConverter jsonConverter) {
        this.jsonConverter = jsonConverter;
    }

    @Override
    public void ensureSchema() {
        // NOOP
    }

    public long cleanExpiredSnapshotsKeysSets() {
        try (final var jedis = jedisPool.getResource()) {
            final var snapshotKeys = jedis.keys(JV_SNAPSHOTS.concat("*"));
            final var expiredSnapshotKeys = jedis.zrange(JV_SNAPSHOTS_ENTITY_KEYS, ZRangeParams.zrangeParams(0, -1));
            expiredSnapshotKeys.removeAll(snapshotKeys);
            log.debug("expired snapshot keys: {}", expiredSnapshotKeys.size());
            expiredSnapshotKeys.forEach(expiredSnapshotKey -> {
                jedis.zrem(JV_SNAPSHOTS_ENTITY_KEYS, expiredSnapshotKey);
                log.debug("{} removed from jv_snapshots_keys", expiredSnapshotKey);
                final var entitySnapshotKeysSet = expiredSnapshotKey.substring(0, expiredSnapshotKey.indexOf('/')).replace("jv_snapshots",
                        JV_SNAPSHOTS_ENTITY_KEYS);
                jedis.zrem(entitySnapshotKeysSet, expiredSnapshotKey);
                log.debug("{} removed from {}", expiredSnapshotKey, entitySnapshotKeysSet);
            });
            return expiredSnapshotKeys.size();
        } catch (final Exception e) {
            log.error(e.getMessage());
        }
        return 0;
    }

    private void initializeSubscriber() {
        executor.execute(() -> {
            try (final var jedis = jedisPool.getResource()) {
                jedis.psubscribe(new CdoSnapshotKeyExpireListener(jedisPool), "__key*__:jv_snapshots:*");
            } catch (final Exception e) {
                log.warn("Redis subscription failed: {}", e.getMessage());
            }
        });
    }

    private void persist(final CdoSnapshot snapshot) {
        final var key = key(snapshot);
        final var value = jsonConverter.toJson(snapshot);
        final var entityNameKey = JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(snapshot.getGlobalId().getTypeName());
        try (final var jedis = jedisPool.getResource()) {
            final var commitInstantMs = (double) -snapshot.getCommitMetadata().getCommitDateInstant().toEpochMilli();
            jedis.zadd(JV_SNAPSHOTS_ENTITY_KEYS, commitInstantMs, key); // all snapshot keys
            if (snapshot.getGlobalId() instanceof InstanceId || snapshot.getGlobalId() instanceof UnboundedValueObjectId) {
                jedis.zadd(entityNameKey, commitInstantMs, key);
                final var entityTypeName = snapshot.getGlobalId().getTypeName();
                jedis.zadd(JV_SNAPSHOTS_ENTITY_KEYS_SET, 0.0, JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(entityTypeName));
            }
            if (snapshot.getGlobalId() instanceof final ValueObjectId valueObjectId) {
                final var ownerId = valueObjectId.getOwnerId();
                final var ownerEntityNameKey = JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(ownerId.getTypeName());
                jedis.zadd(ownerEntityNameKey, commitInstantMs, key);
            }
            jedis.lpush(key, value);
            jedis.expire(key, duration);
        }
    }

    private String key(final CdoSnapshot snapshot) {
        return key(snapshot.getGlobalId());
    }

    private String key(final SnapshotIdentifier snapshotIdentifier) {
        return key(snapshotIdentifier.getGlobalId());
    }

    private String key(final GlobalId globalId) {
        return JV_SNAPSHOTS.concat(globalId.value());
    }

    private List<CdoSnapshot> getStateHistory(final String key, final QueryParams queryParams) {
        try (final var jedis = jedisPool.getResource()) {
            final var cdoSnapshotJsonList = jedis.lrange(key, 0, -1);
            final var cdoSnapshots = cdoSnapshotJsonList.stream()
                    .map(cdoSnapshotJson -> jsonConverter.fromJson(cdoSnapshotJson, CdoSnapshot.class))
                    .toList();
            final var filteredCdoSnapshots = applyQueryParams(cdoSnapshots, queryParams);
            return trimResultsToRequestedSlice(filteredCdoSnapshots, queryParams);
        }
    }

    private List<CdoSnapshot> applyQueryParams(List<CdoSnapshot> snapshots, final QueryParams queryParams) {
        if (!queryParams.commitIds().isEmpty()) {
            snapshots = filterSnapshotsByCommitIds(snapshots, queryParams.commitIds());
        }
        if (queryParams.toCommitId().isPresent()) {
            snapshots = filterSnapshotsByToCommitId(snapshots, queryParams.toCommitId().get());
        }
        if (queryParams.version().isPresent()) {
            snapshots = Lists.positiveFilter(snapshots, snapshot -> snapshot.getVersion() == queryParams.version().get());
        }
        if (queryParams.fromVersion().isPresent()) {
            snapshots = Lists.positiveFilter(snapshots, snapshot -> snapshot.getVersion() >= queryParams.fromVersion().get());
        }
        if (queryParams.toVersion().isPresent()) {
            snapshots = Lists.positiveFilter(snapshots, snapshot -> snapshot.getVersion() <= queryParams.toVersion().get());
        }
        if (queryParams.author().isPresent()) {
            snapshots = filterSnapshotsByAuthor(snapshots, queryParams.author().get());
        }
        if (queryParams.authorLikeIgnoreCase().isPresent()) {
            snapshots = filterSnapshotsByAuthorLikeIgnoreCase(snapshots, queryParams.authorLikeIgnoreCase().get());
        }
        if (hasDates(queryParams)) {
            snapshots = filterSnapshotsByCommitDate(snapshots, queryParams);
        }
        if (hasInstants(queryParams)) {
            snapshots = filterSnapshotsByCommitDateInstant(snapshots, queryParams);
        }
        if (!queryParams.changedProperties().isEmpty()) {
            snapshots = filterByPropertyNames(snapshots, queryParams.changedProperties());
        }
        if (queryParams.snapshotType().isPresent()) {
            snapshots = Lists.positiveFilter(snapshots, snapshot -> snapshot.getType() == queryParams.snapshotType().get());
        }
        snapshots = filterSnapshotsByCommitProperties(snapshots, queryParams.commitProperties());
        snapshots = filterSnapshotsByCommitPropertiesLike(snapshots, queryParams.commitPropertiesLike());

        return snapshots;
    }

    private List<CdoSnapshot> filterByPropertyNames(final List<CdoSnapshot> snapshots, final Set<String> propertyNames) {
        return Lists.positiveFilter(snapshots, input -> propertyNames.stream().anyMatch(input::hasChangeAt));
    }

    private List<CdoSnapshot> filterSnapshotsByToCommitId(final List<CdoSnapshot> snapshots, final CommitId commitId) {
        return Lists.positiveFilter(snapshots, snapshot -> snapshot.getCommitMetadata().getId().isBeforeOrEqual(commitId));
    }

    private List<CdoSnapshot> filterSnapshotsByCommitIds(final List<CdoSnapshot> snapshots, final Set<CommitId> commitIds) {
        return Lists.positiveFilter(snapshots, snapshot -> commitIds.contains(snapshot.getCommitId()));
    }

    private List<CdoSnapshot> filterSnapshotsByAuthor(final List<CdoSnapshot> snapshots, final String author) {
        return Lists.positiveFilter(snapshots, snapshot -> author.equals(snapshot.getCommitMetadata().getAuthor()));
    }

    private List<CdoSnapshot> filterSnapshotsByAuthorLikeIgnoreCase(final List<CdoSnapshot> snapshots, final String author) {
        return Lists.positiveFilter(snapshots,
                snapshot -> snapshot.getCommitMetadata().getAuthor().toLowerCase(Locale.ROOT).contains(author.toLowerCase(Locale.ROOT)));
    }

    private List<CdoSnapshot> filterSnapshotsByCommitDate(final List<CdoSnapshot> snapshots, final QueryParams queryParams) {
        return Lists.positiveFilter(snapshots, snapshot -> isDateInRange(queryParams, snapshot.getCommitMetadata().getCommitDate()));
    }

    public boolean isDateInRange(final QueryParams q, final LocalDateTime date) {
        if (q.from().map(from -> from.isAfter(date)).orElse(false)) {
            return false;
        } else {
            return !(q.to().map(to -> to.isBefore(date)).orElse(false));
        }
    }

    private List<CdoSnapshot> filterSnapshotsByCommitDateInstant(final List<CdoSnapshot> snapshots, final QueryParams queryParams) {
        return Lists.positiveFilter(snapshots, snapshot -> isInstantInRange(queryParams, snapshot.getCommitMetadata().getCommitDateInstant()));
    }

    private boolean isInstantInRange(final QueryParams q, final Instant instant) {
        if (q.fromInstant().map(from -> from.isAfter(instant)).orElse(false)) {
            return false;
        } else {
            return !(q.toInstant().map(to -> to.isBefore(instant)).orElse(false));
        }
    }

    private List<CdoSnapshot> filterSnapshotsByCommitProperties(final List<CdoSnapshot> snapshots, final Map<String, Collection<String>> commitProperties) {
        return Lists.positiveFilter(snapshots, snapshot -> commitProperties.entrySet().stream().allMatch(commitProperty -> {
            final Map<String, String> actualCommitProperties = snapshot.getCommitMetadata().getProperties();
            return actualCommitProperties.containsKey(commitProperty.getKey())
                    && commitProperty.getValue().contains(actualCommitProperties.get(commitProperty.getKey()));
        }));
    }

    private List<CdoSnapshot> filterSnapshotsByCommitPropertiesLike(final List<CdoSnapshot> snapshots, final Map<String, String> commitPropertiesLike) {
        return Lists.positiveFilter(snapshots, snapshot -> commitPropertiesLike.entrySet().stream().allMatch(commitProperty -> {
            final Map<String, String> actualCommitProperties = snapshot.getCommitMetadata().getProperties();
            return actualCommitProperties.containsKey(commitProperty.getKey()) && actualCommitProperties.get(commitProperty.getKey()).toLowerCase(Locale.ROOT)
                    .contains(commitProperty.getValue().toLowerCase(Locale.ROOT));
        }));
    }

    private boolean hasDates(final QueryParams q) {
        return q.from().isPresent() || q.to().isPresent();
    }

    public boolean hasInstants(final QueryParams q) {
        return q.fromInstant().isPresent() || q.toInstant().isPresent();
    }

    private InstanceId instanceId(final String key) {
        final var index = key.indexOf("/");
        final var typeName = key.substring(13, index);
        final var codId = key.substring(index + 1);
        return new InstanceId(typeName, codId, codId);
    }

    private Comparator<? super CdoSnapshot> inReverseChronologicalOrder() {
        return (s1, s2) -> Long.compare(s2.getCommitId().getMajorId(), s1.getCommitId().getMajorId());
    }

    private List<CdoSnapshot> trimResultsToRequestedSlice(List<CdoSnapshot> snapshots, QueryParams queryParams) {
        final var start = Math.min(queryParams.skip(), snapshots.size());
        final var stop = Math.min(queryParams.skip() + queryParams.limit(), snapshots.size());
        return new ArrayList<>(snapshots.subList(start, stop));
    }
}
