package org.javers.repository.sql.domain;

import org.javers.core.commit.CommitMetadata;
import org.javers.core.json.JsonConverter;
import org.javers.repository.sql.poly.JaversPolyJDBC;
import org.javers.repository.sql.schema.FixedSchemaFactory;
import org.polyjdbc.core.query.InsertQuery;

public class CommitRepository {

    private JaversPolyJDBC javersPolyjdbc;
    private JsonConverter jsonConverter;

    public CommitRepository(JaversPolyJDBC javersPolyjdbc, JsonConverter jsonConverter) {
        this.javersPolyjdbc = javersPolyjdbc;
        this.jsonConverter = jsonConverter;
    }

    public Long save(CommitMetadata commitMetadata) {
        InsertQuery query = javersPolyjdbc.query().insert().into(FixedSchemaFactory.COMMIT_TABLE_NAME)
                .value(FixedSchemaFactory.COMMIT_TABLE_AUTHOR, commitMetadata.getAuthor())
                .value(FixedSchemaFactory.COMMIT_TABLE_COMMIT_DATE, jsonConverter.toJson(commitMetadata.getCommitDate()))
                .value(FixedSchemaFactory.COMMIT_TABLE_COMMIT_ID, commitMetadata.getId().toString());

        javersPolyjdbc.queryRunner().insert(query);

        return 1L;
    }
}
