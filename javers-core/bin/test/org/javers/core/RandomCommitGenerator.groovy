package org.javers.core

import org.javers.core.commit.CommitId

import java.util.function.Supplier

class RandomCommitGenerator implements Supplier<CommitId> {
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
