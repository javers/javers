package org.javers.repository.redis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.javers.core.Javers;
import org.javers.core.JaversBuilder;
import org.javers.core.metamodel.object.SnapshotType;
import org.javers.repository.jql.QueryBuilder;
import org.javers.repository.redis.domain.Device;
import org.javers.repository.redis.domain.Firmware;
import org.javers.repository.redis.domain.LabAssistant;
import org.javers.repository.redis.domain.Sensor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import redis.clients.jedis.JedisPool;

@Testcontainers
@TestMethodOrder(OrderAnnotation.class)
class JaversRedisRepositoryTest {

    @Container
    @SuppressWarnings("resource")
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
            .withExposedPorts(6379)
            .withCommand("redis-server", "/etc/redis/redis.conf")
            .withCopyFileToContainer(MountableFile.forClasspathResource("redis.conf"), "/etc/redis/redis.conf");

    static Javers javers;

    @BeforeAll
    static void setup() {
        final var jedisPool = new JedisPool(redis.getHost(), redis.getFirstMappedPort());
        final var javersRedisRepository = new JaversRedisRepository(jedisPool, Duration.of(1000, ChronoUnit.SECONDS));
        try (final var jedis = jedisPool.getResource()) {
            try {
                javers = JaversBuilder.javers().registerJaversRepository(javersRedisRepository).build();
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    @Order(1)
    void testCommitAndLatestSnapshot() {
        // given
        final var commitProperties = new HashMap<>(Map.of("foo", "bar", "baz", "qux"));

        final var labRat1 = new LabAssistant("John", "1");
        final var labRat2 = new LabAssistant("Smith", "2");

        final var sensor = new Sensor("temperature", 0);
        sensor.setLabAssistant(labRat1);
        final var device = new Device("thermometer", sensor);
        device.setFirmware(new Firmware("bootloader", "1.0"));

        final var sensor2 = new Sensor("pressure", 1021);
        sensor2.setLabAssistant(labRat1);
        final var device2 = new Device("barometer", sensor2);
        device2.setFirmware(new Firmware("bootloader", "2.0"));

        // when
        javers.commit("author", labRat1, commitProperties);
        javers.commit("author", labRat2);

        javers.commit("author", device);
        javers.commit("author", device2);

        
        device.getSensor().setValue(20);
        javers.commit("author2", device);

        device2.getSensor().setValue(1019);
        javers.commit("author2", device2);

        labRat1.setLevel("3");
        javers.commit("author", labRat1);
        javers.commit("author", device);
        device.getFirmware().setVersion("2.0");
        javers.commit("author", device);

        // then
        var labRat1Snapshot = javers.getLatestSnapshot("John", LabAssistant.class).orElse(null);
        var labRat2Snapshot = javers.getLatestSnapshot("Smith", LabAssistant.class).orElse(null);

        assertNotNull(labRat1Snapshot);
        assertNotNull(labRat2Snapshot);
    }

    @Test
    @Order(4)
    void testShadows() {
        // given
        final var queryByClass = QueryBuilder.byClass(Device.class).build();
        final var queryByInstanceId = QueryBuilder.byInstanceId("thermometer", Device.class).build();

        // when
        final var snapshots = javers.findSnapshots(queryByClass);
        final var deviceShadows = javers.findShadows(queryByInstanceId);

        // then
        assertEquals(6, snapshots.size());
        assertEquals(20, ((Device) deviceShadows.get(0).get()).getSensor().getValue().intValue());
        assertEquals(0, ((Device) deviceShadows.get(3).get()).getSensor().getValue().intValue());
    }

    @Test
    @Order(5)
    void testLatestSnapshot() {
        // given
        // when
        // then
        var latestSnapshot = javers.getLatestSnapshot("thermometer", Device.class).get();
        assertEquals("thermometer", latestSnapshot.getPropertyValue("name"));
    }

    @Test
    @Order(6)
    void testHistoricalSnapshot() {
        // given
        // when
        // then
        final var historicalSnapshot = javers.getHistoricalSnapshot("thermometer", Device.class, LocalDateTime.now());
        assertNotNull(historicalSnapshot);
    }

    @Test
    @Order(7)
    void testQueryByInstance() {
        // given
        final var sensor = new Sensor("temperature", 0);
        final var query = QueryBuilder.byInstance(new Device("thermometer", sensor)).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
    }

    @Test
    @Order(8)
    void testQueryByInstanceIdWithLocalIdAndTypeName() {
        // given
        final var query = QueryBuilder.byInstanceId("thermometer", "Device").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
    }

    @Test
    @Order(9)
    void testQueryByInstanceIdWithLocalIdAndEntityClass() {
        // given
        final var query = QueryBuilder.byInstanceId("thermometer", Device.class).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
    }

    @Test
    @Order(10)
    void testAnyDomainObject() {
        // given
        final var query = QueryBuilder.anyDomainObject().build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);

    }

    @Test
    @Order(11)
    void testQueryByClass() {
        // given
        final var query = QueryBuilder.byClass(Device.class).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        /// then
        assertNotNull(changes);
        assertNotNull(snapshots);
    }

    @Test
    @Order(12)
    void testQueryByClasses() {
        // given
        final var query = QueryBuilder.byClass(Device.class, LabAssistant.class).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        /// then
        assertNotNull(changes);
        assertNotNull(snapshots);
    }

    @Test
    @Order(13)
    void testQueryByValueObject() {
        // given
        final var query = QueryBuilder.byValueObject(Device.class, "firmware").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        /// then
        assertNotNull(changes);
        assertNotNull(snapshots);
    }

    @Test
    @Order(14)
    void testQueryByValueObjectId() {
        // given
        final var query = QueryBuilder.byValueObjectId("thermometer", Device.class, "firmware").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        /// then
        assertNotNull(changes);
        assertNotNull(snapshots);
    }

    @Test
    @Order(15)
    void testQueryWithCommitProperties() {
        // given
        final var query = QueryBuilder.byClass(LabAssistant.class).withCommitProperty("foo", "bar").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(1, snapshots.size());
    }

    @Test
    @Order(16)
    void testQueryWithCommitPropertiesNonExistingKey() {
        // given
        final var query = QueryBuilder.byClass(LabAssistant.class).withCommitProperty("non-existing", "bar").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(0, snapshots.size());
    }

    @Test
    @Order(16)
    void testQueryWithCommitPropertiesNonExistingValue() {
        // given
        final var query = QueryBuilder.byClass(LabAssistant.class).withCommitProperty("foo", "non-existing").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(0, snapshots.size());
    }

    @Test
    @Order(17)
    void testQueryWithCommitPropertiesLike() {
        // given
        final var query = QueryBuilder.byClass(LabAssistant.class).withCommitPropertyLike("baz", "Q").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(1, snapshots.size());
    }

    @Test
    @Order(18)
    void testQueryWithCommitPropertiesLikeNonExistingKey() {
        // given
        final var query = QueryBuilder.byClass(LabAssistant.class).withCommitPropertyLike("non-existing", "Q").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(0, snapshots.size());
    }

    @Test
    @Order(19)
    void testQueryWithCommitPropertiesLikeNonExistingValue() {
        // given
        final var query = QueryBuilder.byClass(LabAssistant.class).withCommitPropertyLike("baz", "non-existing").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(0, snapshots.size());
    }

    @Test
    @Order(20)
    void testQueryByAuthor() {
        // given
        final var query = QueryBuilder.byClass(Device.class).byAuthor("author2").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(2, snapshots.size());
        assertEquals("author2", snapshots.get(0).getCommitMetadata().getAuthor());
    }

    @Test
    @Order(21)
    void testQueryByAuthorNonExisting() {
        // given
        final var query = QueryBuilder.byClass(Device.class).byAuthor("non-existing").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(0, snapshots.size());
    }

    @Test
    @Order(22)
    void testQueryByAuthorLike() {
        // given
        final var query = QueryBuilder.byClass(Device.class).byAuthorLikeIgnoreCase("thor2").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(2, snapshots.size());
        assertEquals("author2", snapshots.get(0).getCommitMetadata().getAuthor());
    }

    @Test
    @Order(23)
    void testQueryByAuthorLikeNonExisting() {
        // given
        final var query = QueryBuilder.byClass(Device.class).byAuthorLikeIgnoreCase("non-existing").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(0, snapshots.size());
    }

    @Test
    @Order(24)
    void testQueryWithCommitId() {
        // given
        final var query = QueryBuilder.byClass(Device.class).withCommitId(BigDecimal.valueOf(3)).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(1, snapshots.size());
    }

    @Test
    @Order(24)
    void testQueryToCommitId() {
        // given
        var commitId = javers.findSnapshots(QueryBuilder.byClass(LabAssistant.class).build()).get(0).getCommitId();
        final var query = QueryBuilder.byClass(Device.class).toCommitId(commitId).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(4, snapshots.size());
    }

    @Test
    @Order(25)
    void testQueryWithCommitIds() {
        // given
        final var commitIds = Set.of(BigDecimal.valueOf(3), BigDecimal.valueOf(4));
        final var query = QueryBuilder.byClass(Device.class).withCommitIds(commitIds).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(2, snapshots.size());
    }

    @Test
    @Order(26)
    void testQueryWithVersion() {
        // given
        final var query = QueryBuilder.byClass(Device.class).withVersion(2).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertEquals(2, changes.size());
        assertNotNull(snapshots);
        assertEquals(2, snapshots.size());
    }

    @Test
    @Order(27)
    void testQueryWithVersionNonExisting() {
        // given
        final var query = QueryBuilder.byClass(Device.class).withVersion(999_999_999).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertEquals(0, changes.size());
        assertNotNull(snapshots);
        assertEquals(0, snapshots.size());
    }

    @Test
    @Order(28)
    void testQueryWithSnapshotTypeInitial() {
        // given
        final var query = QueryBuilder.byClass(Device.class).withSnapshotType(SnapshotType.INITIAL).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(2, snapshots.size());
        assertEquals(SnapshotType.INITIAL, snapshots.get(0).getType());
    }

    @Test
    @Order(29)
    void testQueryWithSnapshotTypeUpdate() {
        // given
        final var query = QueryBuilder.byClass(Device.class).withSnapshotType(SnapshotType.UPDATE).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(4, snapshots.size());
        snapshots.forEach(ss -> assertEquals(SnapshotType.UPDATE, ss.getType()));
    }

    @Test
    @Order(30)
    void testQueryWithChangedProperty() {
        // given
        final var query = QueryBuilder.byClass(Device.class).withChangedProperty("firmware").build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
    }

    @Test
    @Order(31)
    void testQueryFromInstantInPast() {
        // given
        final var from = Instant.now().minus(Duration.ofDays(2));
        final var query = QueryBuilder.byInstanceId("thermometer", Device.class).fromInstant(from).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(4, snapshots.size());
    }

    @Test
    @Order(32)
    void testQueryFromInstantInFuture() {
        // given
        final var from = Instant.now().plus(Duration.ofDays(2));
        final var query = QueryBuilder.byInstanceId("thermometer", Device.class).fromInstant(from).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(0, snapshots.size());
    }

    @Test
    @Order(33)
    void testQueryToInstantInPast() {
        // given
        final var to = Instant.now().minus(Duration.ofDays(2));
        final var query = QueryBuilder.byInstanceId("thermometer", Device.class).toInstant(to).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(0, snapshots.size());
    }

    @Test
    @Order(34)
    void testQueryToInstantInFuture() {
        // given
        final var to = Instant.now().plus(Duration.ofDays(2));
        final var query = QueryBuilder.byInstanceId("thermometer", Device.class).toInstant(to).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(4, snapshots.size());
    }

    @Test
    @Order(31)
    void testQueryFromLocalDateTimeInPast() {
        // given
        final var from = LocalDateTime.now().minus(Duration.ofDays(2));
        final var query = QueryBuilder.byInstanceId("thermometer", Device.class).from(from).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(4, snapshots.size());
    }

    @Test
    @Order(32)
    void testQueryFromLocalDateTimeInFuture() {
        // given
        final var from = LocalDateTime.now().plus(Duration.ofDays(2));
        final var query = QueryBuilder.byInstanceId("thermometer", Device.class).from(from).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(0, snapshots.size());
    }

    @Test
    @Order(33)
    void testQueryToLocalDateTimeInPast() {
        // given
        final var to = LocalDateTime.now().minus(Duration.ofDays(2));
        final var query = QueryBuilder.byInstanceId("thermometer", Device.class).to(to).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(0, snapshots.size());
    }

    @Test
    @Order(34)
    void testQueryToLocalDateTimeInFuture() {
        // given
        final var to = LocalDateTime.now().plus(Duration.ofDays(2));
        final var query = QueryBuilder.byInstanceId("thermometer", Device.class).to(to).build();

        // when
        final var changes = javers.findChanges(query);
        final var snapshots = javers.findSnapshots(query);

        // then
        assertNotNull(changes);
        assertNotNull(snapshots);
        assertEquals(4, snapshots.size());
    }

}
