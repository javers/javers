package org.javers.repository.sql.finders;

import java.util.Optional;
import org.javers.repository.api.SnapshotIdentifier;
import org.javers.repository.sql.schema.TableNameProvider;
import org.javers.repository.sql.repositories.GlobalIdRepository;
import org.javers.repository.sql.session.Session;
import org.polyjdbc.core.query.SelectQuery;

import java.util.Collection;

import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_GLOBAL_ID_FK;
import static org.javers.repository.sql.schema.FixedSchemaFactory.SNAPSHOT_VERSION;

class SnapshotIdentifiersFilter extends SnapshotFilter {
    private final Collection<SnapshotIdentifier> snapshotIdentifiers;
    private final GlobalIdRepository globalIdRepository;
    private final Session session;

    public SnapshotIdentifiersFilter(TableNameProvider tableNameProvider, GlobalIdRepository globalIdRepository, Collection<SnapshotIdentifier> snapshotIdentifiers, Session session) {
        super(tableNameProvider);
        this.globalIdRepository = globalIdRepository;
        this.snapshotIdentifiers = snapshotIdentifiers;
        this.session = session;
    }

    @Override
    void addWhere(SelectQuery query) {
        query.where("1!=1");
        for (SnapshotIdentifier snapshotIdentifier : snapshotIdentifiers) {
            Optional<Long> globalIdPk = globalIdRepository.findGlobalIdPk(snapshotIdentifier.getGlobalId(), session);
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