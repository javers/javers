package org.javers.repository.sql.schema;

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.dialect.DialectRegistry;
import org.polyjdbc.core.schema.model.RelationBuilder;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.util.StringUtils;

import java.util.Map;
import java.util.TreeMap;

/**
 * non-configurable schema factory, gives you schema with default table names
 *
 * @author bartosz walacik
 */
public class FixedSchemaFactory {

    public static final String GLOBAL_ID_TABLE_NAME = "jv_global_id";
    public static final String GLOBAL_ID_PK =         "global_id_pk";
    public static final String GLOBAL_ID_LOCAL_ID =   "local_id";
    public static final String GLOBAL_ID_FRAGMENT =   "fragment";     //since 1.2
    public static final String GLOBAL_ID_TYPE_NAME =  "type_name";    //since 2.0
    public static final String GLOBAL_ID_OWNER_ID_FK ="owner_id_fk";  //since 1.2
    public static final String GLOBAL_ID_PK_SEQ =     "jv_global_id_pk_seq";

    public static final String COMMIT_TABLE_NAME =    "jv_commit";
    public static final String COMMIT_PK =            "commit_pk";
    public static final String COMMIT_AUTHOR =        "author";
    public static final String COMMIT_COMMIT_DATE =   "commit_date";
    public static final String COMMIT_COMMIT_ID =     "commit_id";
    public static final String COMMIT_PK_SEQ =        "jv_commit_pk_seq";

    public static final String COMMIT_PROPERTY_TABLE_NAME = "jv_commit_property";
    public static final String COMMIT_PROPERTY_COMMIT_FK =  "commit_fk";
    public static final String COMMIT_PROPERTY_NAME =       "property_name";
    public static final String COMMIT_PROPERTY_VALUE =      "property_value";

    public static final String SNAPSHOT_TABLE_NAME =   "jv_snapshot";
    public static final String SNAPSHOT_PK =           "snapshot_pk";
    public static final String SNAPSHOT_COMMIT_FK =    "commit_fk";
    public static final String SNAPSHOT_GLOBAL_ID_FK = "global_id_fk";
    public static final String SNAPSHOT_TYPE =         "type";
    public static final String SNAPSHOT_VERSION =      "version";
    public static final String SNAPSHOT_TABLE_PK_SEQ = "jv_snapshot_pk_seq";
    public static final String SNAPSHOT_STATE =        "state";
    public static final String SNAPSHOT_CHANGED =      "changed_properties"; //since v 1.2
    public static final String SNAPSHOT_MANAGED_TYPE = "managed_type";       //since 2.0

    private Schema snapshotTableSchema(Dialect dialect, String tableName){
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName);
        primaryKey(SNAPSHOT_PK, schema, relationBuilder);
        relationBuilder.withAttribute().string(SNAPSHOT_TYPE).withMaxLength(200).and()
                       .withAttribute().longAttr(SNAPSHOT_VERSION).and()
                       .withAttribute().text(SNAPSHOT_STATE).and()
                       .withAttribute().text(SNAPSHOT_CHANGED).and()
                       .withAttribute().string(SNAPSHOT_MANAGED_TYPE).withMaxLength(200).and();
        foreignKey(tableName, SNAPSHOT_GLOBAL_ID_FK, GLOBAL_ID_TABLE_NAME, GLOBAL_ID_PK, relationBuilder);
        foreignKey(tableName, SNAPSHOT_COMMIT_FK, COMMIT_TABLE_NAME, COMMIT_PK, relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, SNAPSHOT_GLOBAL_ID_FK);
        columnsIndex(tableName, schema, SNAPSHOT_COMMIT_FK);

        return schema;
    }

    private Schema commitTableSchema(Dialect dialect, String tableName) {
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName);
        primaryKey(COMMIT_PK,schema,relationBuilder);
        relationBuilder
                .withAttribute().string(COMMIT_AUTHOR).withMaxLength(200).and()
                .withAttribute().timestamp(COMMIT_COMMIT_DATE).and()
                .withAttribute().number(COMMIT_COMMIT_ID).withIntegerPrecision(12).withDecimalPrecision(2).and()
                .build();

        columnsIndex(tableName, schema, COMMIT_COMMIT_ID);

        return schema;
    }

    private Schema commitPropertiesTableSchema(Dialect dialect, String tableName) {
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName);
        relationBuilder
            .primaryKey(tableName + "_pk").using(COMMIT_PROPERTY_COMMIT_FK, COMMIT_PROPERTY_NAME).and()
            .withAttribute().string(COMMIT_PROPERTY_NAME).withMaxLength(200).and()
            .withAttribute().string(COMMIT_PROPERTY_VALUE).withMaxLength(200).and();
        foreignKey(tableName, COMMIT_PROPERTY_COMMIT_FK, COMMIT_TABLE_NAME, COMMIT_PK, relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, COMMIT_PROPERTY_COMMIT_FK);
        columnsIndex(tableName, schema, COMMIT_PROPERTY_NAME, COMMIT_PROPERTY_VALUE);

        return schema;
    }

    private Schema globalIdTableSchema(Dialect dialect, String tableName){
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName);
        primaryKey(GLOBAL_ID_PK, schema,relationBuilder);
        relationBuilder
                .withAttribute().string(GLOBAL_ID_LOCAL_ID).withMaxLength(200).and()
                .withAttribute().string(GLOBAL_ID_FRAGMENT).withMaxLength(200).and()
                .withAttribute().string(GLOBAL_ID_TYPE_NAME).withMaxLength(200).and();
        foreignKey(tableName, GLOBAL_ID_OWNER_ID_FK, GLOBAL_ID_TABLE_NAME, GLOBAL_ID_PK, relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, GLOBAL_ID_LOCAL_ID);

        return schema;
    }

    private void foreignKey(String tableName, String fkColName, String targetTableName, String targetPkColName, RelationBuilder relationBuilder){
        relationBuilder
                .withAttribute().longAttr(fkColName).and()
                .foreignKey(tableName + "_" + fkColName).on(fkColName).references(targetTableName, targetPkColName).and();
    }

    private void columnsIndex(String tableName, Schema schema, String... colNames){
        String concatenatedColumnNames = StringUtils.concatenate('_', (Object[]) colNames);
        String indexName = tableName + "_" + concatenatedColumnNames + "_idx";
        if (schema.getDialect().getCode().equals(DialectRegistry.ORACLE.name()))
        {
            if (indexName.length() > 30)
            {
                indexName = indexName.substring(0, 31);
            }
        }
        schema
                .addIndex(indexName)
                .indexing(colNames)
                .on(tableName)
                .build();
    }

    private void primaryKey(String pkColName, Schema schema, RelationBuilder relationBuilder) {
        relationBuilder.withAttribute().longAttr(pkColName).withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                .primaryKey("jv_"+pkColName).using(pkColName).and();
        schema.addSequence("jv_"+pkColName+"_seq").build();
    }

    public Map<String, Schema> allTablesSchema(Dialect dialect) {
        Map<String, Schema> schema = new TreeMap<>();

        schema.put(GLOBAL_ID_TABLE_NAME, globalIdTableSchema(dialect, GLOBAL_ID_TABLE_NAME));
        schema.put(COMMIT_TABLE_NAME,    commitTableSchema(dialect, COMMIT_TABLE_NAME));
        schema.put(COMMIT_PROPERTY_TABLE_NAME, commitPropertiesTableSchema(dialect, COMMIT_PROPERTY_TABLE_NAME));
        schema.put(SNAPSHOT_TABLE_NAME,  snapshotTableSchema(dialect, SNAPSHOT_TABLE_NAME));

        return schema;
    }
}
