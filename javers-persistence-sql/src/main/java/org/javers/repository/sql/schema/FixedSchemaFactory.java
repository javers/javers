package org.javers.repository.sql.schema;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.schema.model.RelationBuilder;
import org.polyjdbc.core.schema.model.Schema;

import java.util.Map;
import java.util.TreeMap;

/**
 * non-configurable schema factory, gives you schema with default table names
 *
 * @author bartosz walacik
 */
public class FixedSchemaFactory {

    public static final String CDO_CLASS_TABLE_NAME = "jv_cdo_class";
    public static final String CDO_CLASS_PK =         "cdo_class_pk";

    public static final String GLOBAL_ID_TABLE_NAME = "jv_global_id";
    public static final String GLOBAL_ID_PK =         "global_id_pk";
    public static final String GLOBAL_ID_CLASS_FK =   "cdo_class_fk";

    public static final String COMMIT_TABLE_NAME =    "jv_commit";
    public static final String COMMIT_TABLE_PK =      "commit_pk";

    public static final String SNAPSHOT_TABLE_NAME = "jv_snapshot";
    public static final String SNAPSHOT_TABLE_PK =   "snapshot_pk";
    public static final String SNAPSHOT_TABLE_COMMIT_FK = "commit_fk";
    public static final String SNAPSHOT_TABLE_GLOBAL_ID_FK = "global_id_fk";

    private Schema snapshotTableSchema(Dialect dialect, String tableName){
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName);
        primaryKey(tableName, SNAPSHOT_TABLE_PK, schema,relationBuilder);
        foreignKey(tableName, SNAPSHOT_TABLE_GLOBAL_ID_FK, GLOBAL_ID_TABLE_NAME, GLOBAL_ID_PK, relationBuilder, schema);
        foreignKey(tableName, SNAPSHOT_TABLE_COMMIT_FK, SNAPSHOT_TABLE_NAME, SNAPSHOT_TABLE_PK, relationBuilder, schema);
        relationBuilder.build();

        return schema;
    }

    private Schema commitTableSchema(Dialect dialect, String tableName) {
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName);
        primaryKey(tableName, COMMIT_TABLE_PK,schema,relationBuilder);
        relationBuilder
                .withAttribute().string("author").withMaxLength(200).and()
                .withAttribute().timestamp("commit_date").notNull().and()
                .withAttribute().string("commit_id").withMaxLength(10).and()
                .build();
        return schema;
    }

    private Schema cdoClassTableSchema(Dialect dialect, String tableName) {
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName);
        primaryKey(tableName, CDO_CLASS_PK, schema,relationBuilder);
        relationBuilder.withAttribute().string("qualified_name").withMaxLength(100).and()
                .build();
        return schema;
    }

    private Schema globalIdTableSchema(Dialect dialect, String tableName){
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName);
        primaryKey(tableName, GLOBAL_ID_PK, schema,relationBuilder);
        relationBuilder
                .withAttribute().text("local_id").notNull();
        foreignKey(tableName, GLOBAL_ID_CLASS_FK, CDO_CLASS_TABLE_NAME, CDO_CLASS_PK, relationBuilder, schema);
        relationBuilder.build();

        return schema;
    }

    private void foreignKey(String tableName, String fkColName, String targetTableName, String targetPkColName, RelationBuilder relationBuilder, Schema schema){
        relationBuilder
                .withAttribute().longAttr(fkColName).notNull().and()
                .foreignKey(tableName + "_" + fkColName).on(fkColName).references(targetTableName, targetPkColName);

        schema.addIndex(tableName+"_"+ fkColName +"_idx").indexing(fkColName).on(tableName).build();
    }

    private void primaryKey(String tableName, String pkColName, Schema schema, RelationBuilder relationBuilder) {
        relationBuilder.withAttribute().longAttr(pkColName).withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                .primaryKey("jv_"+pkColName).using(pkColName).and();
        schema.addSequence("jv_"+pkColName+"_seq").build();
        //schema.addIndex(tableName+"_pk_idx").indexing(pkColName).on(tableName).build();
    }

    public Map<String, Schema> allTablesSchema(Dialect dialect) {
        Map<String, Schema> schema = new TreeMap<>();

        schema.put(CDO_CLASS_TABLE_NAME, cdoClassTableSchema(dialect, CDO_CLASS_TABLE_NAME));
        schema.put(GLOBAL_ID_TABLE_NAME, globalIdTableSchema(dialect, GLOBAL_ID_TABLE_NAME));
        schema.put(COMMIT_TABLE_NAME,    commitTableSchema(dialect, COMMIT_TABLE_NAME));
        schema.put(SNAPSHOT_TABLE_NAME,  snapshotTableSchema(dialect, SNAPSHOT_TABLE_NAME));

        return schema;
    }
}
