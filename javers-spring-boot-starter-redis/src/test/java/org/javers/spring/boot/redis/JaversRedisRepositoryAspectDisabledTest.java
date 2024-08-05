package org.javers.spring.boot.redis;

import org.javers.core.Javers;
import org.javers.repository.jql.QueryBuilder;
import org.javers.spring.boot.redis.domain.LabAssistant;
import org.javers.spring.boot.redis.repository.LabAssistantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author ivansimeonov
 */

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Testcontainers
@ActiveProfiles("auditdisabled")
class JaversRedisRepositoryAspectDisabledTest {

  @Container
  @SuppressWarnings("resource")
  static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7-alpine"))
      .withExposedPorts(6379)
      .withEnv("REDIS_PASSWORD", "secret")
      .withCommand("redis-server", "/etc/redis/redis.conf")
      .withCopyFileToContainer(MountableFile.forClasspathResource("redis.conf"), "/etc/redis/redis.conf");

  @Configuration
  @EnableAutoConfiguration
  static class TestConfiguration {
  }

  @Autowired
  Javers javers;

  @Autowired
  LabAssistantRepository labAssistantRepository;

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.redis.host", redis::getHost);
    registry.add("spring.data.redis.port", redis::getFirstMappedPort);
    registry.add("javers.redis.host", redis::getHost);
    registry.add("javers.redis.port", redis::getFirstMappedPort);
  }

  @Test
  void testBuildJaversInstanceWithoutAutoAuditAspectWhenDisabled() {
    // given
    final var labRat1 = new LabAssistant("John", "1");

    // when
    labAssistantRepository.save(labRat1);

    // then
    var query = QueryBuilder.byClass(LabAssistant.class).build();
    var snapshots = javers.findSnapshots(query);

    assertNotNull(snapshots);
    assertEquals(0, snapshots.size());
  }

}
