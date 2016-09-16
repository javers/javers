package org.javers.repository.sql.finders;

import com.google.common.base.Joiner;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CommitPropertyFinder {

    private final PolyJDBC polyJDBC;

    public CommitPropertyFinder(PolyJDBC polyJDBC) {
        this.polyJDBC = polyJDBC;
    }

    List<CommitPropertyDTO> findCommitPropertiesOfSnaphots(Collection<Long> commitPKs) {
        if (commitPKs.isEmpty()) {
            return Collections.emptyList();
        }

        SelectQuery query = polyJDBC.query()
            .select(COMMIT_PROPERTY_COMMIT_FK + ", " + COMMIT_PROPERTY_NAME + ", " + COMMIT_PROPERTY_VALUE)
            .from(getCommitPropertyTableName())
            .where(COMMIT_PROPERTY_COMMIT_FK + " in (" + Joiner.on(",").join(commitPKs) + ")");
        return polyJDBC.queryRunner().queryList(query, new ObjectMapper<CommitPropertyDTO>() {
            @Override
            public CommitPropertyDTO createObject(ResultSet resultSet) throws SQLException {
                return new CommitPropertyDTO(
                    resultSet.getLong(COMMIT_PROPERTY_COMMIT_FK),
                    resultSet.getString(COMMIT_PROPERTY_NAME),
                    resultSet.getString(COMMIT_PROPERTY_VALUE)
                );
            }
        });
    }
}
