package org.javers.repository.sql.domain;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.repository.sql.poly.JaversPolyJDBC;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.javers.repository.sql.schema.FixedSchemaFactory.*;

public class GlobalIdRepository {

    public static final String NATIVE_QUERY = GLOBAL_ID_TABLE_NAME + "." + GLOBAL_ID_CLASS_FK + " = " + CDO_CLASS_TABLE_NAME + "." + CDO_CLASS_PK
            + " AND " + GLOBAL_ID_TABLE_NAME + "." + GLOBAL_ID_LOCAL_ID + " = '%s'";
    private JaversPolyJDBC javersPolyjdbc;
    private JsonConverter jsonConverter;

    public GlobalIdRepository(JaversPolyJDBC javersPolyjdbc, JsonConverter jsonConverter) {
        this.javersPolyjdbc = javersPolyjdbc;
        this.jsonConverter = jsonConverter;
    }

    public Long save(GlobalId globalId) {
        Long lookup = getIfExists(globalId);
        return lookup != null ? lookup : insert(globalId);
    }

    private Long getIfExists(GlobalId globalId) {
        SelectQuery selectQuery = javersPolyjdbc.query()
                .select(GLOBAL_ID_PK)
                .from(GLOBAL_ID_TABLE_NAME + "," + CDO_CLASS_TABLE_NAME)
                .where(String.format(NATIVE_QUERY, jsonConverter.toJson(globalId.getCdoId())));

        List<Long> globalIds = javersPolyjdbc.queryRunner().queryList(selectQuery, new ObjectMapper<Long>() {
            @Override
            public Long createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getLong(GLOBAL_ID_PK);
            }
        });

        if (javersPolyjdbc.queryRunner().queryExistence(selectQuery)) {
            return globalIds.get(0);
        }
        return null;
    }

    private Long insert(GlobalId globalId) {
        InsertQuery insertClassQuery = javersPolyjdbc.query()
                .insert()
                .into(CDO_CLASS_TABLE_NAME)
                .value(CDO_CLASS_QUALIFIED_NAME, globalId.getCdoClass().getClientsClass().getName())
                .sequence(CDO_CLASS_PK, CDO_PK_SEQ_NAME);

        long insertedClassId = javersPolyjdbc.queryRunner().insert(insertClassQuery);

        InsertQuery insertGlobalIdQuery = javersPolyjdbc.query()
                .insert()
                .into(GLOBAL_ID_TABLE_NAME)
                .value(GLOBAL_ID_LOCAL_ID, jsonConverter.toJson(globalId.getCdoId()))
                .value(GLOBAL_ID_CLASS_FK, insertedClassId)
                .sequence(GLOBAL_ID_PK, GLOBAL_ID_PK_SEQ);

        return javersPolyjdbc.queryRunner().insert(insertGlobalIdQuery);
    }
}
