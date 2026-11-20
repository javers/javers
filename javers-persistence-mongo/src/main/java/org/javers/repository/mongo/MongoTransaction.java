package org.javers.repository.mongo;

import com.mongodb.client.ClientSession;
import org.javers.core.commit.Commit;

import java.util.Optional;

/**
 * One Mongo transaction as seen by Javers: the ClientSession + the transaction-local latest snapshot cache (L1).
 * Created by a {@link MongoSessionProvider}, which must call {@link #afterCommit()} after the transaction commits.
 * Not thread-safe; bound to a single transaction on a single thread.
 *
 * @since 7.12
 */
public final class MongoTransaction {
    private final ClientSession clientSession;
    private TransactionLocalLatestSnapshotCache localCache;
    private Runnable commitListener = () -> {};

    public MongoTransaction(ClientSession clientSession) {
        this.clientSession = clientSession;
    }

    public ClientSession clientSession() {
        return clientSession;
    }

    /**
     * Provider MUST call it once, after the transaction is committed. Runs the commit listener.
     */
    public void afterCommit() {
        commitListener.run();
    }

    /**
     * Empty until the transaction writes its first commit.
     */
    Optional<TransactionLocalLatestSnapshotCache> localLatestSnapshotCache() {
        return Optional.ofNullable(localCache);
    }

    /**
     * Snapshots written in a transaction go to L1 only.
     * L2 gets them after the transaction commits, so it never holds uncommitted data.
     */
    void cacheLocallyAndPromoteOnCommit(Commit commit, LatestSnapshotCache l2) {
        if (localCache == null) {
            localCache = new TransactionLocalLatestSnapshotCache(l2.maxSize());
        }
        TransactionLocalLatestSnapshotCache l1 = localCache;
        l1.putAll(commit);
        setCommitListener(() -> l1.promoteToL2(l2));
    }

    void setCommitListener(Runnable listener) {
        this.commitListener = listener;
    }
}
