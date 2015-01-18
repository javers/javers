package org.javers.repository.sql.domain;

import org.javers.core.json.JsonConverter;
import org.javers.core.metamodel.object.GlobalId;
import org.javers.repository.sql.poly.JaversPolyJDBC;
import org.javers.repository.sql.schema.FixedSchemaFactory;
import org.polyjdbc.core.query.InsertQuery;
import org.polyjdbc.core.query.SelectQuery;
import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GlobalIdRepository {

    private JaversPolyJDBC javersPolyjdbc;
    private JsonConverter jsonConverter;

    public GlobalIdRepository(JaversPolyJDBC javersPolyjdbc, JsonConverter jsonConverter) {

        this.javersPolyjdbc = javersPolyjdbc;
        this.jsonConverter = jsonConverter;
    }

    public Object save(GlobalId globalId) {

        SelectQuery selectQuery = javersPolyjdbc.query().select().query("SELECT COUNT (" + FixedSchemaFactory.GLOBAL_ID_PK + ") \n" +
                "FROM \n" +
                "  public.jv_global_id, \n" +
                "  public.jv_cdo_class\n" +
                "WHERE \n" +
                "  jv_global_id.cdo_class_fk = jv_cdo_class.cdo_class_pk AND\n" +
                "  jv_global_id.local_id = '" + jsonConverter.toJson(globalId.getCdoId()) + "'");


        javersPolyjdbc.queryRunner().queryList(selectQuery, new ObjectMapper<Integer>() {
            @Override
            public Integer createObject(ResultSet resultSet) throws SQLException {
                return resultSet.getInt("global_id_pk");
            }
        });

        if (javersPolyjdbc.queryRunner().queryExistence(selectQuery)) {
            return globalId;
        }

        return insert(globalId);
    }

    private Object insert(GlobalId globalId) {
        InsertQuery insertClassQuery = javersPolyjdbc.query()
                .insert()
                .into(FixedSchemaFactory.CDO_CLASS_TABLE_NAME)
                .value(FixedSchemaFactory.CDO_CLASS_QUALIFIED_NAME, globalId.getCdoClass().getClientsClass().getName());

        long insertedClassId = javersPolyjdbc.queryRunner().insert(insertClassQuery);

        InsertQuery insertGlobalIdQuery = javersPolyjdbc.query()
                .insert()
                .into(FixedSchemaFactory.GLOBAL_ID_TABLE_NAME)
                .value("local_id", jsonConverter.toJson(globalId.getCdoId()))
                .value(FixedSchemaFactory.GLOBAL_ID_CLASS_FK, insertedClassId);

        return javersPolyjdbc.queryRunner().insert(insertGlobalIdQuery);
    }
}
