package org.javers.repository.sql.schema;

import org.javers.repository.sql.ConnectionProvider;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.dialect.*;
import org.polyjdbc.core.exception.SchemaInspectionException;
import org.polyjdbc.core.schema.SchemaInspector;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.model.IndexBuilder;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.util.TheCloser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;

import static org.javers.repository.sql.schema.FixedSchemaFactory.COMMIT_COMMIT_DATE_INSTANT;
import static org.javers.repository.sql.schema.FixedSchemaFactory.GLOBAL_ID_OWNER_ID_FK;

/**
 * @author bartosz walacik
 */
public class JaversSchemaManager extends SchemaNameAware {
    private static final Logger logger = LoggerFactory.getLogger(JaversSchemaManager.class);

    private SchemaInspector schemaInspector;
    private SchemaManager schemaManager;
    private final Dialect dialect;
    private final FixedSchemaFactory schemaFactory;
    private final PolyJDBC polyJDBC;
    private final ConnectionProvider connectionProvider;

    public JaversSchemaManager(Dialect dialect, FixedSchemaFactory schemaFactory, PolyJDBC polyJDBC, ConnectionProvider connectionProvider, TableNameProvider tableNameProvider) {
        super(tableNameProvider);
        this.dialect = dialect;
        this.schemaFactory = schemaFactory;
        this.polyJDBC = polyJDBC;
        this.connectionProvider = connectionProvider;
    }

    public void ensureSchema() {
        this.schemaInspector = polyJDBC.schemaInspector();
        this.schemaManager = polyJDBC.schemaManager();

        for (Map.Entry<String, Schema> e : schemaFactory.allTablesSchema(dialect).entrySet()) {
            ensureTable(e.getKey(), e.getValue());
        }

        alterCommitIdColumnIfNeeded(); // JaVers 2.5 to 2.6 schema migration

        if(dialect instanceof MsSqlDialect) {
            alterMssqlTextColumns();
        }
        
        if(dialect instanceof MysqlDialect) {
            alterMySqlCommitDateColumn();
        }

        if(!(dialect instanceof H2Dialect)) {
            addDbIndexOnOwnerId();
        }

        addCommitDateInstantColumnIfNeeded();

        TheCloser.close(schemaManager, schemaInspector);
    }

    /**
     * JaVers 5.0 to 5.1 schema migration
     */
    private void addCommitDateInstantColumnIfNeeded() {
        if (!columnExists(getCommitTableNameWithSchema(), COMMIT_COMMIT_DATE_INSTANT)){
            addStringColumn(getCommitTableNameWithSchema(), COMMIT_COMMIT_DATE_INSTANT, 30);
        } else {
            extendStringColumnIfNeeded(getCommitTableNameWithSchema(), COMMIT_COMMIT_DATE_INSTANT, 30);
        }
    }

    /**
     * JaVers 3.9.2 to 3.9.3 schema migration (MySql only)
     */
    private void alterMySqlCommitDateColumn() {
        ColumnType commitDateColType = getTypeOf(getCommitTableNameWithSchema(), "commit_date");

        if (commitDateColType.typeName.equals("TIMESTAMP") &&
            commitDateColType.precision == 19) {
            logger.info("migrating db schema from JaVers 3.9.2 to 3.9.3 (MySql only) ...");
            executeSQL("ALTER TABLE " + getCommitTableNameWithSchema() + " MODIFY commit_date TIMESTAMP(3)");
        }
    }

    /**
     * JaVers 3.11.4 to 3.11.5 schema migration
     */
    @Deprecated
    private void addDbIndexOnOwnerId() {
        if (dialect instanceof OracleDialect) {
            return;
        }

        addIndex(getGlobalIdTableName(), new FixedSchemaFactory.IndexedCols(GLOBAL_ID_OWNER_ID_FK));
    }

    /**
     * JaVers 2.5 to 2.6 schema migration
     */
    private void alterCommitIdColumnIfNeeded() {
        ColumnType commitIdColType = getTypeOf(getCommitTableNameWithSchema(), "commit_id");

        if (commitIdColType.precision == 12) {
            logger.info("migrating db schema from JaVers 2.5 to 2.6 ...");
            if (dialect instanceof PostgresDialect) {
                executeSQL("ALTER TABLE " + getCommitTableNameWithSchema() + " ALTER COLUMN commit_id TYPE numeric(22,2)");
            } else if (dialect instanceof H2Dialect) {
                executeSQL("ALTER TABLE " + getCommitTableNameWithSchema() + " ALTER COLUMN commit_id numeric(22,2)");
            } else if (dialect instanceof MysqlDialect) {
                executeSQL("ALTER TABLE " + getCommitTableNameWithSchema() + " MODIFY commit_id numeric(22,2)");
            } else if (dialect instanceof OracleDialect) {
                executeSQL("ALTER TABLE " + getCommitTableNameWithSchema() + " MODIFY commit_id number(22,2)");
            } else if (dialect instanceof MsSqlDialect) {
                executeSQL("drop index jv_commit_commit_id_idx on " + getCommitTableNameWithSchema());
                executeSQL("ALTER TABLE " + getCommitTableNameWithSchema() + " ALTER COLUMN commit_id numeric(22,2)");
                executeSQL("CREATE INDEX jv_commit_commit_id_idx ON " + getCommitTableNameWithSchema() + " (commit_id)");
            } else {
                handleUnsupportedDialect();
            }
        }
    }

    /**
     * JaVers 3.3.0 to 3.3.1 MsSql schema migration
     *
     * This method is needed for upgrading TEXT columns to VARCHAR(MAX) since TEXT is deprecated.
     */
    private void alterMssqlTextColumns() {
        ColumnType stateColType = getTypeOf(getSnapshotTableNameWithSchema(), "state");
        ColumnType changedPropertiesColType = getTypeOf(getSnapshotTableNameWithSchema(), "state");

        if(stateColType.typeName.equals("text")) {
            executeSQL("ALTER TABLE " + getSnapshotTableNameWithSchema() + " ALTER COLUMN state VARCHAR(MAX)");
        }

        if(changedPropertiesColType.typeName.equals("text")) {
            executeSQL("ALTER TABLE " + getSnapshotTableNameWithSchema() + " ALTER COLUMN changed_properties VARCHAR(MAX)");
        }
    }

    private void handleUnsupportedDialect() {
        logger.error("\nno DB schema migration script for {} :(\nplease contact with JaVers team, javers@javers.org",
                dialect.getCode());
    }

    private boolean executeSQL(String sql) {
        try {
            Statement stmt = connectionProvider.getConnection().createStatement();

            logger.info("executing schema migration SQL:\n" + sql);

            boolean b = stmt.execute(sql);
            stmt.close();

            return b;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private int executeUpdate(String sql) {
        try {
            Statement stmt = connectionProvider.getConnection().createStatement();

            int cnt = stmt.executeUpdate(sql);
            stmt.close();

            return cnt;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ColumnType getTypeOf(String tableName, String colName) {
        try {
            Statement stmt = connectionProvider.getConnection().createStatement();

            ResultSet res = stmt.executeQuery("select " + colName + " from " + tableName + " where 1<0");
            int colType = res.getMetaData().getColumnType(1);
            int colPrec = res.getMetaData().getPrecision(1);
            String colTypeName = res.getMetaData().getColumnTypeName(1);

            stmt.close();
            res.close();

            return new ColumnType(colType, colPrec, colTypeName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean columnExists(String tableName, String colName) {
        try {
            Statement stmt = connectionProvider.getConnection().createStatement();

            ResultSet res = stmt.executeQuery("select * from " + tableName + " where 1<0");
            ResultSetMetaData metaData = res.getMetaData();

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (metaData.getColumnName(i).equalsIgnoreCase(colName)) {
                    return true;
                }
            }

            res.close();
            stmt.close();

            return false;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void ensureTable(String tableName, Schema schema) {
        String schemaName = schemaFactory.getSchemaNameUsedForSchemaInspection();
        if (relationExists(tableName, schema.getSchemaName())) {
            logger.debug("table {}.{} exists", schemaName, tableName);
            return;
        }
        logger.debug("creating javers table {}.{} ...", schemaName, tableName);
        schemaManager.create(schema);

    }

    boolean relationExists(String name, String schemaName) {
        try {
            Connection connection = connectionProvider.getConnection();

            DatabaseMetaData metadata = connection.getMetaData();
            String catalog = connection.getCatalog();

            ResultSet resultSet = metadata.getTables(
                    catalog,
                    convertCase(schemaName, metadata),
                    convertCase(name, metadata),
                    new String[]{"TABLE"});

            if (schemaName != null) {
                return resultSet.next();
            } else {
                String tableSchemaName;
                do {
                    if (!resultSet.next()) {
                        return false;
                    }

                    tableSchemaName = resultSet.getString("TABLE_SCHEM");
                } while(tableSchemaName != null
                        && !tableSchemaName.equalsIgnoreCase("public")
                        && !tableSchemaName.equals("")
                        && (!(this.dialect instanceof MsSqlDialect) || !tableSchemaName.equalsIgnoreCase("dbo"))
                        && (!(this.dialect instanceof OracleDialect) || !tableSchemaName.equalsIgnoreCase("system"))
                );

                return true;
            }
        } catch (SQLException var4) {
            throw new SchemaInspectionException("RELATION_LOOKUP_ERROR", "Failed to obtain tables metadata when checking table " + name, var4);
        }
    }

    private String convertCase(String identifier, DatabaseMetaData metadata) throws SQLException {
        if (identifier == null || identifier.isEmpty()) {
            return identifier;
        }
        if (metadata.storesLowerCaseIdentifiers()) {
            return identifier.toLowerCase();
        } else {
            return metadata.storesUpperCaseIdentifiers() ? identifier.toUpperCase() : identifier;
        }
    }

    private void addStringColumn(String tableName, String colName, int len) {
        logger.warn("column " + tableName + "." + colName + " not exists, running ALTER TABLE ...");

        String sqlType = dialect.types().string(len);

        if (dialect instanceof OracleDialect ||
                dialect instanceof MsSqlDialect) {
            executeSQL("ALTER TABLE " + tableName + " ADD " + colName + " " + sqlType);
        } else {
            executeSQL("ALTER TABLE " + tableName + " ADD COLUMN " + colName + " " + sqlType);
        }
    }

    private void addLongColumn(String tableName, String colName) {
        logger.warn("column " + tableName + "." + colName + " not exists, running ALTER TABLE ...");

        String sqlType = dialect.types().bigint(0);

        if (dialect instanceof OracleDialect ||
                dialect instanceof MsSqlDialect) {
            executeSQL("ALTER TABLE " + tableName + " ADD " + colName + " " + sqlType);
        } else {
            executeSQL("ALTER TABLE " + tableName + " ADD COLUMN " + colName + " " + sqlType);
        }
    }

    private void extendStringColumnIfNeeded(String tableName, String colName, int len) {
        ColumnType colType = getTypeOf(tableName, colName);
        String newType = colType.typeName + "(" + len + ")";

        if (colType.precision < len) {
            logger.info("extending {}.{} COLUMN length from {} to {}", tableName, colName, colType.precision, len);
            if (dialect instanceof PostgresDialect) {
                executeSQL("ALTER TABLE " + tableName + " ALTER COLUMN " + colName + " TYPE "+newType);
            } else if (dialect instanceof H2Dialect) {
                executeSQL("ALTER TABLE " + tableName + " ALTER COLUMN " + colName + " "+newType);
            } else if (dialect instanceof MysqlDialect) {
                executeSQL("ALTER TABLE " + tableName + " MODIFY " + colName + " "+newType);
            } else if (dialect instanceof OracleDialect) {
                executeSQL("ALTER TABLE " + tableName + " MODIFY " + colName + " "+newType);
            } else if (dialect instanceof MsSqlDialect) {
                executeSQL("ALTER TABLE " + tableName + " ALTER COLUMN " + colName + " "+newType);
            } else {
                handleUnsupportedDialect();
            }
        }

    }

    /**
     * !! Fails on Oracle with schema
     * @deprecated
     */
    @Deprecated
    private void addIndex(DBObjectName tableName, FixedSchemaFactory.IndexedCols indexedCols) {
        String indexName = schemaFactory.createIndexName(tableName, indexedCols);

        if (!schemaInspector.indexExists(tableName.localName(), indexName)) {
            String ddl = IndexBuilder.index(dialect, indexName)
                    .indexing(indexedCols.indexedColNames())
                    .on(tableName.nameWithSchema()).build().ddl();

            executeSQL(ddl);
        }
    }

    public void dropSchema() {
        throw new RuntimeException("not implemented");
    }

    static class ColumnType {
        final int type;
        final int precision;
        final String typeName;

        ColumnType(int type, int precision, String typeName) {
            this.type = type;
            this.precision = precision;
            this.typeName = typeName;
        }

        @Override
        public String toString() {
            return "ColumnType{" +
                    "type=" + type +
                    ", precision=" + precision +
                    ", typeName='" + typeName + '\'' +
                    '}';
        }
    }
}
