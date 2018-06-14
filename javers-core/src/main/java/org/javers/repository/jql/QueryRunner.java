package org.javers.repository.jql;

import org.javers.core.diff.Change;
import org.javers.core.metamodel.object.CdoSnapshot;
import org.javers.shadow.Shadow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author bartosz.walacik
 */
public class QueryRunner {
    private static final Logger logger = LoggerFactory.getLogger(JqlQuery.JQL_LOGGER_NAME);

    private final ChangesQueryRunner changesQueryRunner;
    private final SnapshotQueryRunner snapshotQueryRunner;
    private final ShadowQueryRunner shadowQueryRunner;
    private final ShadowStreamQueryRunner shadowStreamQueryRunner;

    QueryRunner(ChangesQueryRunner changesQueryRunner, SnapshotQueryRunner snapshotQueryRunner, ShadowQueryRunner shadowQueryRunner, ShadowStreamQueryRunner shadowStreamQueryRunner) {
        this.changesQueryRunner = changesQueryRunner;
        this.snapshotQueryRunner = snapshotQueryRunner;
        this.shadowQueryRunner = shadowQueryRunner;
        this.shadowStreamQueryRunner = shadowStreamQueryRunner;
    }

    public Stream<Shadow> queryForShadowsStream(JqlQuery query) {
        return shadowStreamQueryRunner.queryForShadowsStream(query);
    }

    public List<Shadow> queryForShadows(JqlQuery query) {
        return shadowQueryRunner.queryForShadows(query);
    }

    public Optional<CdoSnapshot> runQueryForLatestSnapshot(GlobalIdDTO globalId) {
        return snapshotQueryRunner.runQueryForLatestSnapshot(globalId);
    }

    public List<CdoSnapshot> queryForSnapshots(JqlQuery query){
        return snapshotQueryRunner.queryForSnapshots(query);
    }

    public List<Change> queryForChanges(JqlQuery query) {
        return changesQueryRunner.queryForChanges(query);
    }
}
