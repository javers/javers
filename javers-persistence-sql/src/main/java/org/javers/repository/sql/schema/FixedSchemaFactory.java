package org.javers.repository.sql.schema;

import com.google.gson.internal.LinkedHashTreeMap;
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
    private final static int ORACLE_MAX_NAME_LEN = 30;

    private final Dialect dialect;

    public FixedSchemaFactory(Dialect dialect, DBNameProvider dbNameProvider) {
        super(dbNameProvider);
        this.dialect = dialect;
    }

    Map<String, Schema> allTablesSchema(Dialect dialect) {
        Map<String, Schema> schema = new LinkedHashTreeMap<>();
        schema.put(getGlobalIdTableName().localName()      , globalIdTableSchema(dialect));
        schema.put(getCommitTableName().localName()        , commitTableSchema(dialect));
        schema.put(getCommitPropertyTableName().localName(), commitPropertiesTableSchema(dialect));
        schema.put(getSnapshotTableName().localName()      , snapshotTableSchema(dialect));
        return schema;
    }

    private Schema snapshotTableSchema(Dialect dialect){
        DBObjectName tableName = getSnapshotTableName();
        Schema schema = emptySchema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName.localName());
        primaryKey(getSnapshotPKColumnName(), schema, relationBuilder, getSnapshotTablePkSeqName().localName());
        relationBuilder.withAttribute().string(getSnapshotTypeColumnName()).withMaxLength(200).and()
                       .withAttribute().longAttr(getSnapshotVersionColumnName()).and()
                       .withAttribute().text(getSnapshotStateColumnName()).and()
                       .withAttribute().text(getSnapshotChangedColumnName()).and()
                       .withAttribute().string(getSnapshotManagedTypeColumnName()).withMaxLength(200).and();
        foreignKey(tableName, getSnapshotGlobalIdFKColumnName(), false, getGlobalIdTableNameWithSchema(), getGlobalIdPKColumnName(), relationBuilder);
        foreignKey(tableName, getSnapshotCommitFKColumnName(), false, getCommitTableNameWithSchema(), getCommitPKColumnName(), relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, getSnapshotGlobalIdFKColumnName());
        columnsIndex(tableName, schema, getSnapshotCommitFKColumnName());

        return schema;
    }

    private Schema commitTableSchema(Dialect dialect) {
        DBObjectName tableName = getCommitTableName();
        Schema schema = emptySchema(dialect);
        RelationBuilder relationBuilder = schema.addRelation(tableName.localName());
        primaryKey(getCommitPKColumnName(), schema, relationBuilder, getCommitPkSeqName().localName());
        relationBuilder
                .withAttribute().string(getCommitAuthorColumnName()).withMaxLength(200).and()
                .withAttribute().timestamp(getCommitCommitDateColumnName()).and()
                .withAttribute().string(getCommitCommitDateInstantColumnName()).withMaxLength(30).and()
                .withAttribute().number(getCommitCommitIdColumName()).withIntegerPrecision(22).withDecimalPrecision(2).and()
                .build();

        columnsIndex(tableName, schema, getCommitCommitIdColumName());

        return schema;
    }

    private Schema commitPropertiesTableSchema(Dialect dialect) {
        DBObjectName tableName = getCommitPropertyTableName();
        Schema schema = emptySchema(dialect);

    	String pkName = tableName.localName();
    	if(getIsSuffix()) {
    		pkName = pkName + "_" + tableName.localName();
    	} else {
    		pkName = tableName.localName() + "_" + pkName;
    	}
    	
        RelationBuilder relationBuilder = schema.addRelation(tableName.localName());
        relationBuilder
            .primaryKey(pkName).using(getCommitPropertyCommitFKColumnName(), getCommitPropertyNameColumnName()).and()
            .withAttribute().string(getCommitPropertyNameColumnName()).withMaxLength(MAX_INDEX_KEY_LEN_IN_MYSQL).notNull().and()
            .withAttribute().string(getCommitPropertyValueColumnName()).withMaxLength(600).and();
        foreignKey(tableName, getCommitPropertyCommitFKColumnName(), true, getCommitTableNameWithSchema(), getCommitPKColumnName(), relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, getCommitPropertyCommitFKColumnName());

        // Add index prefix length for MySql
        if (dialect instanceof MysqlDialect) {
            columnsIndex(tableName, schema, new IndexedCols(
                    new String[]{getCommitPropertyNameColumnName(), getCommitPropertyValueColumnName()},
                    new int[]{0, MAX_INDEX_KEY_LEN_IN_MYSQL}));
        }
        else {
            columnsIndex(tableName, schema, getCommitPropertyNameColumnName(), getCommitPropertyValueColumnName());
        }

        return schema;
    }

    private Schema globalIdTableSchema(Dialect dialect){
        DBObjectName tableName = getGlobalIdTableName();

        Schema schema = emptySchema(dialect);

        RelationBuilder relationBuilder = schema.addRelation(tableName.localName());
        primaryKey(getGlobalIdPKColumnName() , schema, relationBuilder, getGlobalIdPkSeqName().localName());
        relationBuilder
                .withAttribute().string(getGlobalIdLocalIdColumnName()).withMaxLength(MAX_INDEX_KEY_LEN_IN_MYSQL).and()
                .withAttribute().string(getGlobalIdFragmentColumnName()).withMaxLength(200).and()
                .withAttribute().string(getGlobalIdTypeNameColumnName()).withMaxLength(200).and();
        foreignKey(tableName, getGlobalIdOwnerIDFKColumnName(), false, getGlobalIdTableNameWithSchema(), getGlobalIdPKColumnName(), relationBuilder);
        relationBuilder.build();

        columnsIndex(tableName, schema, getGlobalIdLocalIdColumnName());
        columnsIndex(tableName, schema, getGlobalIdOwnerIDFKColumnName());

        return schema;
    }
    
    String getSchemaNameUsedForSchemaInspection() {
        String schemaName = getSchemaName().orElse("");
        return schemaName.isEmpty() ? "" : schemaName;
    }

    Schema emptySchema(Dialect dialect) {
        return getSchemaName().map(s -> new Schema(dialect, s)).orElse(new Schema(dialect));
    }
    
    private void primaryKey(String pkColName, Schema schema, RelationBuilder relationBuilder, String seqNameLocal) {

    	String pkName = pkColName;
    	if(getIsSuffix()) {
    		pkName = pkName + "_" + getPrimaryKeyIndicator();
    	} else {
    		pkName = getPrimaryKeyIndicator() + "_" + pkName;
    	}

        relationBuilder.withAttribute().longAttr(pkColName).withAdditionalModifiers("AUTO_INCREMENT")
        			   .notNull().and().primaryKey(pkColName).using(pkColName).and();
        schema.addSequence(seqNameLocal).build();
    }

    private void foreignKey(DBObjectName tableName, String fkColName, boolean isPartOfPrimaryKey, 
    						String targetTableName, String targetPkColName, RelationBuilder relationBuilder){

    	String fkName = tableName.localName() + "_" + fkColName;
    	if(getIsSuffix()) {
    		fkName = fkName + "_" + getForeignKeyIndicator();
    	} else {
    		fkName = getForeignKeyIndicator() + "_" + fkName;
    	}

        LongAttributeBuilder longAttributeBuilder = relationBuilder.withAttribute().longAttr(fkColName);
        if (isPartOfPrimaryKey && (dialect instanceof DB2Dialect || dialect instanceof DB2400Dialect)) {
            longAttributeBuilder.notNull();
        }
        longAttributeBuilder.and()
                .foreignKey(fkName).on(fkColName).references(targetTableName, targetPkColName).and();
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

    String createIndexName(DBObjectName tableName, IndexedCols indexedCols) {
    	String indexName = tableName.localName() + "_" + indexedCols.concatenatedColNames();
    	if(getIsSuffix()) {
    		indexName = indexName + "_" + getIndexIndicator();
    	} else {
    		indexName = getIndexIndicator() + "_" + indexName; 
    	}
        
        if (dialect instanceof OracleDialect && indexName.length() > ORACLE_MAX_NAME_LEN)
        {
            return indexName.substring(0, ORACLE_MAX_NAME_LEN);
        }
        return indexName;
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
