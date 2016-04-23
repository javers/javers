package org.javers.repository.sql.finders;

import com.google.common.base.Joiner;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.SelectQuery;

import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CommitPropertyFinder {

    private final PolyJDBC polyJDBC;

    public CommitPropertyFinder(PolyJDBC polyJDBC) {
        this.polyJDBC = polyJDBC;
    }

    List<CommitPropertyDTO> findPropertiesOfCommits(List<Long> commitPKs) {
        SelectQuery query = polyJDBC.query()
            .select(COMMIT_PROPERTY_COMMIT_FK + ", " + COMMIT_PROPERTY_NAME + ", " + COMMIT_PROPERTY_VALUE)
            .from(COMMIT_PROPERTY_TABLE_NAME)
            .where(COMMIT_PROPERTY_COMMIT_FK + " in (" + Joiner.on(",").join(commitPKs) + ")");
        return polyJDBC.queryRunner().queryList(query, new CommitPropertyDTOMapper());
    }

}
