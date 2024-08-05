package org.javers.repository.redis;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.SerializationUtils.deserialize;
import static org.apache.commons.lang3.SerializationUtils.serialize;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.javers.common.collections.Lists;
import org.javers.common.validation.Validate;
import org.javers.core.commit.Commit;
import org.javers.core.commit.CommitId;
import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.core.metamodel.object.InstanceId;
import org.javers.core.metamodel.object.ValueObjectId;
import org.javers.core.metamodel.type.EntityType;
import org.javers.core.metamodel.type.ManagedType;
import org.javers.repository.api.JaversRepository;
import org.javers.repository.api.QueryParams;
import org.javers.repository.api.SnapshotIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPool;

public class JaversRedisRepository implements JaversRepository {
	private static final Logger log = LoggerFactory.getLogger(JaversRedisRepository.class);
	public static final String JV_SNAPSHOTS = "jv_snapshots:";
	public static final String JV_SNAPSHOTS_ENTITY_KEYS = "jv_snapshots_keys";
	public static final String JV_ENTITY_TYPE_NAME = "jv_entity_type_name";
	private static final byte[] JV_HEAD_ID = "jv_head_id".getBytes();

	private JsonConverter jsonConverter;
	private final JedisPool jedisPool;
	private final long duration;
	private final ExecutorService executor;

	public JaversRedisRepository(final JedisPool jedisPool, final Duration duration) {
		this.jedisPool = jedisPool;
		this.duration = duration.toSeconds();
		this.executor = Executors.newSingleThreadExecutor();
		executor.execute(() -> {
			try (final var jedis = jedisPool.getResource()) {
				jedis.psubscribe(new CdoSnapshotKeyExpireListener(jedisPool), "__key*__:jv_snapshots:*");
			} catch (final Exception e) {
				log.warn("Redis subscription failed: {}", e.getMessage());
			}
		});
	}

	public void shutdown() {
		executor.shutdown();
	}

	@Override
	public CommitId getHeadId() {
		try (final var jedis = jedisPool.getResource()) {
			final var headIdByteArray = jedis.get(JV_HEAD_ID);
			return (CommitId) Optional.ofNullable(headIdByteArray).map(a -> deserialize(a)).orElse(null);
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
				// final var keys = jedis.keys(entityKey.concat("*"));
				final var keys = jedis.smembers(JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(globalId.getTypeName())).stream()
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
			final var result = givenClasses.stream()
					// .map(givenClass -> JV_SNAPSHOTS.concat(givenClass.getName()).concat("/*"))
					// .map(jedis::keys)
					.map(givenClass -> jedis.smembers(JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(givenClass.getName()))).flatMap(Set::stream)
					.filter(key -> !key.contains("#")) // skip keys of value objects
					.map(this::instanceId).map(instanceId -> getStateHistory(instanceId, queryParams)).flatMap(List::stream)
					.sorted(inReverseChronologicalOrder()).toList();
			return applyQueryParams(result, queryParams);
		}
	}

	@Override
	public List<CdoSnapshot> getValueObjectStateHistory(final EntityType ownerEntity, final String path, final QueryParams queryParams) {
		Validate.argumentsAreNotNull(ownerEntity, path, queryParams);
		try (final var jedis = jedisPool.getResource()) {
			// final var query =
			// JV_SNAPSHOTS.concat(ownerEntity.getName()).concat("/*#").concat(path);
			// final var keys = Optional.ofNullable(jedis.keys(query)).orElse(emptySet());
			final var entityKeys = jedis.smembers(JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(ownerEntity.getName()));
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
			// final var keys = jedis.keys(JV_SNAPSHOTS.concat("*/*"));
			final var keys = jedis.smembers(JV_SNAPSHOTS_ENTITY_KEYS);
			final var allCdoSnapshots = keys.stream().map(this::instanceId).map(instanceId -> getStateHistory(instanceId, queryParams)).flatMap(List::stream)
					.sorted(inReverseChronologicalOrder()).toList();
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

	public void cleanExpiredSnapshots() {
		try (final var jedis = jedisPool.getResource(); final var resource = this.getClass().getResourceAsStream("/scripts/cleanExpiredSnapshots.lua")) {
			final var luaScript = new String(resource.readAllBytes());
			jedis.eval(luaScript);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void persist(final CdoSnapshot snapshot) {
		final var key = key(snapshot);
		final var value = jsonConverter.toJson(snapshot);
		final var entityNameKey = JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(snapshot.getGlobalId().getTypeName());
		try (final var jedis = jedisPool.getResource()) {
			jedis.sadd(JV_SNAPSHOTS_ENTITY_KEYS, key);
			if (snapshot.getGlobalId() instanceof InstanceId) {
				jedis.sadd(entityNameKey, key);
				final var entityTypeName = snapshot.getGlobalId().getTypeName();
				jedis.sadd(JV_ENTITY_TYPE_NAME, JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(entityTypeName));
			} else if (snapshot.getGlobalId() instanceof ValueObjectId) {
				final var valueObjectId = (ValueObjectId) snapshot.getGlobalId();
				final var ownerId = valueObjectId.getOwnerId();
				final var ownerEntityNameKey = JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(ownerId.getTypeName());
				jedis.sadd(ownerEntityNameKey, key);
				jedis.sadd(JV_ENTITY_TYPE_NAME, JV_SNAPSHOTS_ENTITY_KEYS.concat(":").concat(ownerId.getTypeName()));
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
			final var size = jedis.llen(key);
			final var start = Math.min(queryParams.skip(), size);
			final var stop = Math.min(start + queryParams.limit() - 1, size);
			final var cdoSnapshotJsonList = jedis.lrange(key, start, stop);
			final var cdoSnapshots = cdoSnapshotJsonList.stream().map(cdoSnapshotJson -> jsonConverter.fromJson(cdoSnapshotJson, CdoSnapshot.class)).toList();
			return applyQueryParams(cdoSnapshots, queryParams);
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

		return trimResultsToRequestedSlice(snapshots, queryParams.skip(), queryParams.limit());
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

	private List<CdoSnapshot> trimResultsToRequestedSlice(final List<CdoSnapshot> snapshots, final int from, final int size) {
		final int fromIndex = Math.min(from, snapshots.size());
		final int toIndex = Math.min(from + size, snapshots.size());
		return new ArrayList<>(snapshots.subList(fromIndex, toIndex));
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
}
