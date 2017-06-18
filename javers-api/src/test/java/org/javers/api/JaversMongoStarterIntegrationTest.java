package org.javers.api;

import org.javers.core.Javers;
import org.javers.core.commit.CommitMetadata;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;

@RunWith(value = SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class}, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class JaversMongoStarterIntegrationTest {

    @Autowired
    private DummyEntityRepository dummyEntityRepository;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Javers javers;

    @Before
    public void setup() {
        restTemplate.getRestTemplate().getMessageConverters().add(0, new SnapshotResponseMessageConverter(javers));
    }

    @Test
    public void shouldGetSnapshotForGivenInstanceId() {
        //given
        DummyEntity dummyEntity = new DummyEntity(1);

        dummyEntityRepository.save(dummyEntity);

        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/javers/v1/snapshots")
                .queryParam("instanceId", "1")
                .queryParam("className", DummyEntity.class.getName());

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", MediaType.APPLICATION_JSON_VALUE);

        //when
        ResponseEntity<SnapshotsResponse> response = restTemplate.<SnapshotsResponse>exchange(builder.build().encode().toUri(),
                HttpMethod.GET,
                new HttpEntity<>(headers),
                SnapshotsResponse.class);

        //then
        assertThat(response.getStatusCodeValue()).isEqualTo(200);

        SnapshotsResponse snapshotsResponse = response.getBody();
        assertThat(snapshotsResponse.getSnapshots()).hasSize(1);

        CommitMetadata commitMetadata = snapshotsResponse.getSnapshots().get(0).getCommitMetadata();
        assertThat(commitMetadata.getProperties()).contains(entry("key", "ok"));
        assertThat(commitMetadata.getAuthor()).isEqualTo("unauthenticated");
    }
}