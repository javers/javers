package org.javers.repository.sql.schema;

import org.polyjdbc.core.dialect.*;
import org.polyjdbc.core.schema.model.LongAttributeBuilder;
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
public class FixedSchemaFactory extends SchemaNameAware {
    private static final int MAX_INDEX_KEY_LEN_IN_MYSQL = 191;

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

    private final static int ORACLE_MAX_NAME_LEN = 30;

    private final Dialect dialect;

    public FixedSchemaFactory(Dialect dialect, TableNameProvider tableNameProvider) {
        super(tableNameProvider);
        this.dialect = dialect;
    }

    Map<String, Schema> allTablesSchema(Dialect dialect) {
        Map<String, Schema> schema = new TreeMap<>();

        schema.put(GLOBAL_ID_TABLE_NAME, globalIdTableSchema(dialect));
        schema.put(COMMIT_TABLE_NAME,    commitTableSchema(dialect));
        schema.put(COMMIT_PROPERTY_TABLE_NAME, commitPropertiesTableSchema(dialect));
        schema.put(SNAPSHOT_TABLE_NAME,  snapshotTableSchema(dialect));

        return schema;
    }

    private Schema snapshotTableSchema(Dialect dialect){
        DBObjectName tableName = getSnapshotTableName();
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName.nameWithSchema());
        primaryKey(SNAPSHOT_PK, schema, relationBuilder);
        relationBuilder.withAttribute().string(SNAPSHOT_TYPE).withMaxLength(200).and()
                       .withAttribute().longAttr(SNAPSHOT_VERSION).and()
                       .withAttribute().text(SNAPSHOT_STATE).and()
                       .withAttribute().text(SNAPSHOT_CHANGED).and()
                       .withAttribute().string(SNAPSHOT_MANAGED_TYPE).withMaxLength(200).and();
        foreignKey(tableName, SNAPSHOT_GLOBAL_ID_FK, false, getGlobalIdTableNameWithSchema(), GLOBAL_ID_PK, relationBuilder);
        foreignKey(tableName, SNAPSHOT_COMMIT_FK, false, getCommitTableNameWithSchema(), COMMIT_PK, relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, SNAPSHOT_GLOBAL_ID_FK);
        columnsIndex(tableName, schema, SNAPSHOT_COMMIT_FK);

        return schema;
    }

    private Schema commitTableSchema(Dialect dialect) {
        DBObjectName tableName = getCommitTableName();
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName.nameWithSchema());
        primaryKey(COMMIT_PK,schema,relationBuilder);
        relationBuilder
                .withAttribute().string(COMMIT_AUTHOR).withMaxLength(200).and()
                .withAttribute().timestamp(COMMIT_COMMIT_DATE).and()
                .withAttribute().number(COMMIT_COMMIT_ID).withIntegerPrecision(22).withDecimalPrecision(2).and()
                .build();

        columnsIndex(tableName, schema, COMMIT_COMMIT_ID);

        return schema;
    }

    private Schema commitPropertiesTableSchema(Dialect dialect) {
        DBObjectName tableName = getCommitPropertyTableName();
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName.nameWithSchema());
        relationBuilder
            .primaryKey(tableName.localName() + "_pk").using(COMMIT_PROPERTY_COMMIT_FK, COMMIT_PROPERTY_NAME).and()
            .withAttribute().string(COMMIT_PROPERTY_NAME).withMaxLength(MAX_INDEX_KEY_LEN_IN_MYSQL).notNull().and()
            .withAttribute().string(COMMIT_PROPERTY_VALUE).withMaxLength(600).and();
        foreignKey(tableName, COMMIT_PROPERTY_COMMIT_FK, true, getCommitTableNameWithSchema(), COMMIT_PK, relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, COMMIT_PROPERTY_COMMIT_FK);

        // Add index prefix length for MySql
        if (dialect instanceof MysqlDialect) {
            columnsIndex(tableName, schema, new IndexedCols(
                    new String[]{COMMIT_PROPERTY_NAME, COMMIT_PROPERTY_VALUE},
                    new int[]{0, MAX_INDEX_KEY_LEN_IN_MYSQL}));
        }
        else {
            columnsIndex(tableName, schema, COMMIT_PROPERTY_NAME, COMMIT_PROPERTY_VALUE);
        }

        return schema;
    }

    private Schema globalIdTableSchema(Dialect dialect){
        DBObjectName tableName = getGlobalIdTableName();
        Schema schema = new Schema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName.nameWithSchema());
        primaryKey(GLOBAL_ID_PK, schema,relationBuilder);
        relationBuilder
                .withAttribute().string(GLOBAL_ID_LOCAL_ID).withMaxLength(MAX_INDEX_KEY_LEN_IN_MYSQL).and()
                .withAttribute().string(GLOBAL_ID_FRAGMENT).withMaxLength(200).and()
                .withAttribute().string(GLOBAL_ID_TYPE_NAME).withMaxLength(200).and();
        foreignKey(tableName, GLOBAL_ID_OWNER_ID_FK, false, getGlobalIdTableNameWithSchema(), GLOBAL_ID_PK, relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, GLOBAL_ID_LOCAL_ID);

        return schema;
    }

    private void foreignKey(DBObjectName tableName, String fkColName, boolean isPartOfPrimaryKey, String targetTableName, String targetPkColName, RelationBuilder relationBuilder){
        LongAttributeBuilder longAttributeBuilder = relationBuilder
                .withAttribute().longAttr(fkColName);
        if (isPartOfPrimaryKey && (dialect instanceof DB2Dialect || dialect instanceof DB2400Dialect)) {
            longAttributeBuilder.notNull();
        }
        longAttributeBuilder.and()
                .foreignKey(tableName.localName() + "_" + fkColName).on(fkColName).references(targetTableName, targetPkColName).and();
    }

    private void columnsIndex(DBObjectName tableName, Schema schema, String... colNames){
        columnsIndex(tableName, schema, new IndexedCols(colNames));
    }

    private void columnsIndex(DBObjectName tableName, Schema schema, IndexedCols indexedCols){
        String indexName = tableName.localName() + "_" + indexedCols.concatenatedColNames() + "_idx";
        if (dialect instanceof OracleDialect &&
                indexName.length() > ORACLE_MAX_NAME_LEN)
        {
            indexName = indexName.substring(0, ORACLE_MAX_NAME_LEN);
        }
        schema.addIndex(indexName)
                .indexing(indexedCols.indexedColNames())
                .on(tableName.nameWithSchema())
                .build();
    }

    private void primaryKey(String pkColName, Schema schema, RelationBuilder relationBuilder) {
        relationBuilder.withAttribute().longAttr(pkColName).withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                .primaryKey("jv_"+pkColName).using(pkColName).and();
        schema.addSequence(getSequenceNameWithSchema(pkColName)).build();
    }

    private static class IndexedCols {
        private final String[] colNames;
        private final int[] prefixLengths;

        IndexedCols(String... colNames) {
            this.colNames = colNames;
            this.prefixLengths = new int[colNames.length];
        }

        IndexedCols(String[] colNames, int[] prefixLengths) {
            this.colNames = colNames;
            this.prefixLengths = prefixLengths;
        }

        String concatenatedColNames() {
            return StringUtils.concatenate('_', (Object[]) colNames);
        }

        String[] indexedColNames() {
            String[] indexedNames = new String[colNames.length];

            for (int i=0; i<colNames.length; i++) {
                indexedNames[i] = colNames[i];
                if (prefixLengths[i] > 0) {
                    indexedNames[i] += "("+prefixLengths[i]+")";
                }
            }
            return indexedNames;
        }
    }
}
