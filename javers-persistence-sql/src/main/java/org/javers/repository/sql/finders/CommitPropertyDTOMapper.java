package org.javers.repository.sql.finders;

import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class CommitPropertyDTOMapper implements ObjectMapper<CommitPropertyDTO> {

    @Override
    public CommitPropertyDTO createObject(ResultSet resultSet) throws SQLException {
        return new CommitPropertyDTO(
            resultSet.getLong(COMMIT_PROPERTY_COMMIT_FK),
            resultSet.getString(COMMIT_PROPERTY_NAME),
            resultSet.getString(COMMIT_PROPERTY_VALUE)
        );
    }

}
