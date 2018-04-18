package org.javers.core

import org.javers.common.date.DateProvider
import org.javers.core.commit.CommitId
import org.javers.core.commit.CommitMetadata
import org.javers.repository.api.JaversRepository
import org.javers.repository.inmemory.InMemoryRepository

import java.time.LocalDateTime
import java.util.function.Supplier

import static org.javers.core.JaversBuilder.javers

class JaversRepositoryRandomCommitIdE2ETest extends JaversRepositoryShadowE2ETest {

    private CustomGenerator customGenerator = new CustomGenerator()
    private TikDateProvider tikDateProvider = new TikDateProvider()

    @Override
    def setup() {
        repository = prepareJaversRepository()
        javers = javers().withDateTimeProvider(tikDateProvider)
                .withCustomCommitIdGenerator(customGenerator)
                .registerJaversRepository(repository).build()
    }

    @Override
    protected int commitSeq(CommitMetadata commit) {
        customGenerator.getSeq(commit.id)
    }

    protected setNow(LocalDateTime localDateTime) {
        tikDateProvider.localDateTime = localDateTime
    }

    protected JaversRepository prepareJaversRepository() {
        return new InMemoryRepository(CommitIdGenerator.CUSTOM)
    }

    class CustomGenerator implements Supplier<CommitId> {
        private Map<CommitId, Integer> commits = [:]
        private int counter

        int getSeq(CommitId commitId) {
            return commits.get(commitId)
        }

        @Override
        synchronized CommitId get() {
            counter++

            def next = new CommitId(Math.abs(UUID.randomUUID().getLeastSignificantBits()), 0)

            commits.put(next, counter)

            return next
        }
    }

    class TikDateProvider implements DateProvider {
        private LocalDateTime localDateTime = LocalDateTime.now()

        @Override
        synchronized LocalDateTime now() {
            def now = localDateTime
            localDateTime = localDateTime.plusSeconds(1)
            now
        }
    }
}
