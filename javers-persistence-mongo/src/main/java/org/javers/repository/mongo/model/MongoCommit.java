package org.javers.repository.mongo.model;

import com.mongodb.BasicDBObject;
import org.javers.core.commit.CommitId;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.joda.time.LocalDateTime;

import java.util.List;

public class MongoCommit extends BasicDBObject {

    public static final String COLLECTION_NAME = "Commits";

    private static final String COMMIT_ID = "commitId";

    private CommitId id;
    private List<CdoSnapshot> snapshots;
    private String author;
    private LocalDateTime commitDate;

    public String getCommitId() {
        return get(COMMIT_ID).toString();
    }

    public static class Builder {

        private MongoCommit mongoCommit;

        public Builder() {
            mongoCommit = new MongoCommit();
        }

        public static Builder mongoCommit() {
            return new Builder();
        }

        public Builder withId(String id) {
            mongoCommit.append(COMMIT_ID, id);
            return this;
        }

        public Builder withSnapshots(List<CdoSnapshot> snapshots) {
            return this;
        }

        public Builder withAuthor(String author) {
            return this;
        }

        public Builder withDate(LocalDateTime date) {
            return this;
        }

        public MongoCommit build() {
            return mongoCommit;
        }
    }
}
