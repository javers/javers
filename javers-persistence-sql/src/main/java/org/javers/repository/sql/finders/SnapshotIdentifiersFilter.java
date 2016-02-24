package org.javers.repository.sql.finders;

import org.javers.common.collections.Optional;
import org.javers.repository.api.SnapshotIdentifier;
import org.javers.repository.sql.repositories.GlobalIdRepository;
import org.polyjdbc.core.query.SelectQuery;

import java.util.Collection;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

class SnapshotIdentifiersFilter extends SnapshotFilter {
    private final Collection<SnapshotIdentifier> snapshotIdentifiers;
    private final GlobalIdRepository globalIdRepository;

    public SnapshotIdentifiersFilter(GlobalIdRepository globalIdRepository, Collection<SnapshotIdentifier> snapshotIdentifiers) {
        this.globalIdRepository = globalIdRepository;
        this.snapshotIdentifiers = snapshotIdentifiers;
    }

    @Override
    String select() {
        return BASE_AND_GLOBAL_ID_FIELDS;
    }

    @Override
    void addFrom(SelectQuery query) {
        query.from(COMMIT_WITH_SNAPSHOT_GLOBAL_ID);
    }

    @Override
    void addWhere(SelectQuery query) {
        query.where("1!=1");
        for (SnapshotIdentifier snapshotIdentifier : snapshotIdentifiers) {
            Optional<Long> globalIdPk = globalIdRepository.findGlobalIdPk(snapshotIdentifier.getGlobalId());
            if (globalIdPk.isPresent()) {
                query.append(" OR (")
                    .append(SNAPSHOT_GLOBAL_ID_FK).append(" = ").append(globalIdPk.get().toString())
                    .append(" AND ")
                    .append(SNAPSHOT_VERSION).append(" = ").append(Long.toString(snapshotIdentifier.getVersion()))
                    .append(")");
            }
        }
    }

}