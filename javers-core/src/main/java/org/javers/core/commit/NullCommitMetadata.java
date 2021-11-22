package org.javers.core.commit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;

class NullCommitMetadata extends CommitMetadata {

    NullCommitMetadata(String author, Map<String, String> properties, LocalDateTime commitDate, Instant commitDateInstant, CommitId id) {
        super(author, properties, commitDate, commitDateInstant, id);
    }

    static NullCommitMetadata instance() {
        return new NullCommitMetadata(
                "anonymous",
                Collections.emptyMap(),
                LocalDateTime.now(),
                Instant.now(),
                new CommitId(0,0)
                );
    }
}
