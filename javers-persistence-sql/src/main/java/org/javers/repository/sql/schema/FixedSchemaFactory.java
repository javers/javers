package org.javers.repository.sql.schema;

import com.google.gson.internal.LinkedHashTreeMap;

import org.javers.repository.sql.SqlRepositoryConfiguration;
import org.polyjdbc.core.dialect.*;
import org.polyjdbc.core.schema.model.LongAttributeBuilder;
import org.polyjdbc.core.schema.model.RelationBuilder;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.util.StringUtils;

import java.util.Map;

/**
 * non-configurable schema factory, gives schema with default table names
 *
 * @author bartosz walacik
 */
public class FixedSchemaFactory extends SchemaNameAware {
	
    private static final int MAX_INDEX_KEY_LEN_IN_MYSQL = 191;

    private final static int ORACLE_MAX_NAME_LEN = 40;

    private final Dialect dialect;
    private final SqlRepositoryConfiguration configuration;
    
    public FixedSchemaFactory(Dialect dialect, TableNameProvider tableNameProvider, SqlRepositoryConfiguration configuration, ColumnNameProvider columnNameProvider) {
        super(tableNameProvider, columnNameProvider);
        this.dialect = dialect;
        this.configuration = configuration;
    }

    Map<String, Schema> allTablesSchema(Dialect dialect) {
        Map<String, Schema> schema = new LinkedHashTreeMap<>();

        schema.put(getGlobalIdTableName().localName(), globalIdTableSchema(dialect));
        schema.put(getCommitTableName().localName(),    commitTableSchema(dialect));
        schema.put(getCommitPropertyTableName().localName(), commitPropertiesTableSchema(dialect));
        schema.put(getSnapshotTableName().localName(),  snapshotTableSchema(dialect));

        return schema;
    }

    private Schema snapshotTableSchema(Dialect dialect){
        DBObjectName tableName = getSnapshotTableName();
        Schema schema = emptySchema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName.localName());
        primaryKey(getSnapshotPKName(), schema, relationBuilder, getSnapshotTablePkSeqName().localName());
        relationBuilder.withAttribute().string(getSnapshotTypeName()).withMaxLength(200).and()
                       .withAttribute().longAttr(getSnapshotVersionName()).and()
                       .withAttribute().text(getSnapshotStateName()).and()
                       .withAttribute().text(getSnapshotChangedName()).and()
                       .withAttribute().string(getSnapshotManagedTypeName()).withMaxLength(200).and();
        foreignKey(tableName, getSnapshotGlobalIDName(), false, getGlobalIdTableNameWithSchema(), getGlobalIdPKName(), relationBuilder);
        foreignKey(tableName, getSnapshotCommitFKName(), false, getCommitTableNameWithSchema(), getCommitPKName(), relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, getSnapshotGlobalIDName());
        columnsIndex(tableName, schema, getSnapshotCommitFKName());

        return schema;
    }

    private Schema commitTableSchema(Dialect dialect) {
        DBObjectName tableName = getCommitTableName();
        Schema schema = emptySchema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName.localName());
        primaryKey(getCommitPKName(), schema, relationBuilder, getCommitPkSeqName().localName());
        relationBuilder
                .withAttribute().string(getCommitAuthorName()).withMaxLength(200).and()
                .withAttribute().timestamp(getCommitDateName()).and()
                .withAttribute().string(getCommitInstantName()).withMaxLength(30).and()
                .withAttribute().number(getCommitIdName()).withIntegerPrecision(22).withDecimalPrecision(2).and()
                .build();

        columnsIndex(tableName, schema, getCommitIdName());

        return schema;
    }

    private Schema commitPropertiesTableSchema(Dialect dialect) {
        DBObjectName tableName = getCommitPropertyTableName();
        Schema schema = emptySchema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName.localName());
        relationBuilder
            .primaryKey(tableName.localName() + "_pk").using(getCommitPropertyCommitFKName(), getCommitPropertyName()).and()
            .withAttribute().string(getCommitPropertyName()).withMaxLength(MAX_INDEX_KEY_LEN_IN_MYSQL).notNull().and()
            .withAttribute().string(getCommitPropertyValueName()).withMaxLength(600).and();
        foreignKey(tableName, getCommitPropertyCommitFKName(), true, getCommitTableNameWithSchema(), getCommitPKName(), relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, getCommitPropertyCommitFKName());

        // Add index prefix length for MySql
        if (dialect instanceof MysqlDialect) {
            columnsIndex(tableName, schema, new IndexedCols(
                    new String[]{getCommitPropertyName(), getCommitPropertyValueName()},
                    new int[]{0, MAX_INDEX_KEY_LEN_IN_MYSQL}));
        }
        else {
            columnsIndex(tableName, schema, getCommitPropertyName(), getCommitPropertyValueName());
        }

        return schema;
    }

    private Schema globalIdTableSchema(Dialect dialect){
        DBObjectName tableName = getGlobalIdTableName();

        Schema schema = emptySchema(dialect);

        RelationBuilder relationBuilder = schema.addRelation(tableName.localName());
        primaryKey(getGlobalIdPKName(), schema, relationBuilder, getGlobalIdPkSeqName().localName());
        relationBuilder
                .withAttribute().string(getGlobalIdLocalIdName()).withMaxLength(MAX_INDEX_KEY_LEN_IN_MYSQL).and()
                .withAttribute().string(getGlobalIdFragmentName()).withMaxLength(200).and()
                .withAttribute().string(getGlobalIdTypeName()).withMaxLength(200).and();
        foreignKey(tableName, getGlobalIdOwnerIDFKName(), false, getGlobalIdTableNameWithSchema(), getGlobalIdPKName(), relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, getGlobalIdLocalIdName());
        columnsIndex(tableName, schema, getGlobalIdOwnerIDFKName());

        return schema;
    }

    Schema emptySchema(Dialect dialect) {
        return getSchemaName().map(s -> new Schema(dialect, s)).orElse(new Schema(dialect));
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
        String indexName = createIndexName(tableName, indexedCols);

        schema.addIndex(indexName)
                .indexing(indexedCols.indexedColNames())
                .on(tableName.localName())
                .build();
    }

    String getSchemaNameUsedForSchemaInspection() {
        String schemaName = getSchemaName().orElse("");
        return schemaName.isEmpty() ? "" : schemaName;
    }

    String createIndexName(DBObjectName tableName, IndexedCols indexedCols) {
        String indexName = tableName.localName() + "_" + indexedCols.concatenatedColNames() + "_idx";

        if (dialect instanceof OracleDialect && indexName.length() > ORACLE_MAX_NAME_LEN)
        {
            return indexName.substring(0, ORACLE_MAX_NAME_LEN);
        }
        return indexName;
    }

    private void primaryKey(String pkColName, Schema schema, RelationBuilder relationBuilder, String seqNameLocal) {
        relationBuilder.withAttribute().longAttr(pkColName).withAdditionalModifiers("AUTO_INCREMENT").notNull().and()
                .primaryKey("jv_"+pkColName).using(pkColName).and();
        schema.addSequence(seqNameLocal).build();
    }

    static class IndexedCols {
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
