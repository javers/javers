package org.javers.repository.sql.schema;

import org.javers.repository.sql.ConnectionProvider;
import org.polyjdbc.core.PolyJDBC;
import org.polyjdbc.core.dialect.*;
import org.polyjdbc.core.query.UpdateQuery;
import org.polyjdbc.core.schema.SchemaInspector;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.model.Schema;
import org.polyjdbc.core.util.TheCloser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Map;

/**
 * @author bartosz walacik
 */
public class JaversSchemaManager {
    private static final Logger logger = LoggerFactory.getLogger(JaversSchemaManager.class);

    private SchemaInspector schemaInspector;
    private SchemaManager schemaManager;
    private final Dialect dialect;
    private final FixedSchemaFactory schemaFactory;
    private final PolyJDBC polyJDBC;
    private final ConnectionProvider connectionProvider;

    public JaversSchemaManager(Dialect dialect, FixedSchemaFactory schemaFactory, PolyJDBC polyJDBC, ConnectionProvider connectionProvider) {
        this.dialect = dialect;
        this.schemaFactory = schemaFactory;
        this.polyJDBC = polyJDBC;
        this.connectionProvider = connectionProvider;
    }

    public void ensureSchema() {
        this.schemaInspector = polyJDBC.schemaInspector();
        this.schemaManager = polyJDBC.schemaManager();

        for (Map.Entry<String, Schema> e : schemaFactory.allTablesSchema(dialect).entrySet()){
            ensureTable(e.getKey(), e.getValue());
        }

        alterCommitIdColumnIfNeeded();
        addSnapshotVersionColumnIfNeeded();
        addSnapshotManagedTypeColumnIfNeeded();
        addGlobalIdTypeNameColumnIfNeeded();

        TheCloser.close(schemaManager, schemaInspector);
    }

    /**
     * JaVers 1.3.15 to 1.3.16 schema migration
     */
    private void alterCommitIdColumnIfNeeded() {

        if (getTypeOf("jv_commit", "commit_id") == Types.VARCHAR){
            logger.info("migrating db schema from JaVers 1.3.15 to 1.3.16 ...");

            if (dialect instanceof PostgresDialect){
                executeSQL("ALTER TABLE jv_commit ALTER COLUMN commit_id TYPE numeric(12,2) USING commit_id::numeric");
            } else if (dialect instanceof H2Dialect){
                executeSQL("ALTER TABLE jv_commit ALTER COLUMN commit_id numeric(12,2)");
            } else if (dialect instanceof MysqlDialect){
                executeSQL("ALTER TABLE jv_commit MODIFY commit_id numeric(12,2)");
            } else if (dialect instanceof OracleDialect){
                executeSQL("ALTER TABLE jv_commit MODIFY commit_id number(12,2)");
            } else if (dialect instanceof MsSqlDialect) {
                executeSQL("drop index jv_commit_commit_id_idx on jv_commit");
                executeSQL("ALTER TABLE jv_commit ALTER COLUMN commit_id numeric(12,2)");
                executeSQL("CREATE INDEX jv_commit_commit_id_idx ON jv_commit (commit_id)");
            } else {
                handleUnsupportedDialect();
            }
        }
    }

    private void handleUnsupportedDialect() {
        logger.error("\nno DB schema migration script for {} :(\nplease contact with JaVers team, javers@javers.org",
                dialect.getCode());
    }

    /**
     * JaVers 1.4.3 to 1.4.4 schema migration
     */
    private void addSnapshotVersionColumnIfNeeded() {

        if (!columnExists("jv_snapshot", "version")) {
            logger.warn("column jv_snapshot.version not exists, running ALTER TABLE ...");

            if ( dialect instanceof PostgresDialect ||
                 dialect instanceof MysqlDialect ||
                 dialect instanceof H2Dialect ||
                 dialect instanceof MsSqlDialect) {
                executeSQL("ALTER TABLE jv_snapshot ADD COLUMN version BIGINT");
            } else if (dialect instanceof OracleDialect) {
                executeSQL("ALTER TABLE jv_snapshot ADD version NUMBER");
            } else {
                handleUnsupportedDialect();
            }
        }
    }

    /**
     * JaVers 1.6.x to 2.0 schema migration
     */
    private void addSnapshotManagedTypeColumnIfNeeded() {
        if (!columnExists("jv_snapshot", "managed_type")) {
            addStringColumn("jv_snapshot", "managed_type", 200);

            populateSnapshotManagedType();
        }
    }

    /**
     * JaVers 1.6.x to 2.0 schema migration
     */
    private void addGlobalIdTypeNameColumnIfNeeded() {
        if (!columnExists("jv_global_id", "type_name")) {
            addStringColumn("jv_global_id", "type_name", 200);

            populateGlobalIdTypeName();
        }
    }

    private void populateSnapshotManagedType() {
        String updateStmt =
                "UPDATE jv_snapshot" +
                        "  SET managed_type = (SELECT qualified_name" +
                        "                      FROM jv_cdo_class," +
                        "                           jv_global_id" +
                        "                      WHERE cdo_class_pk = cdo_class_fk " +
                        "                      AND   global_id_pk = global_id_fk" +
                        "                     )";
        int cnt = executeUpdate(updateStmt);
        logger.info("populate jv_snapshot.managed_type - " + cnt +" row(s) updated");
    }

    private void populateGlobalIdTypeName() {
        String updateStmt =
            "UPDATE jv_global_id " +
            "  SET type_name = (SELECT qualified_name" +
            "                   FROM jv_cdo_class" +
            "                   WHERE cdo_class_pk = cdo_class_fk" +
            "                   )" +
            "  WHERE owner_id_fk IS NULL";
        int cnt = executeUpdate(updateStmt);

        logger.info("populate jv_global_id.type_name - " + cnt +" row(s) updated");
    }

    private boolean executeSQL(String sql) {
        try {
            Statement stmt = connectionProvider.getConnection().createStatement();

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

    private int getTypeOf(String tableName, String colName) {
        try {
            Statement stmt = connectionProvider.getConnection().createStatement();

            ResultSet res = stmt.executeQuery("select " + colName + " from " + tableName + " where 1<0");
            int colType = res.getMetaData().getColumnType(1);

            stmt.close();
            res.close();

            return colType;
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
        if (schemaInspector.relationExists(tableName)) {
            return;
        }
        logger.info("creating javers table {} ...", tableName);
        schemaManager.create(schema);
    }

    private void addStringColumn(String tableName, String colName, int len){
        logger.warn("column "+tableName+"."+colName+" not exists, running ALTER TABLE ...");

        String sqlType = dialect.types().string(len);

        if (dialect instanceof OracleDialect) {
            executeSQL("ALTER TABLE "+tableName+" ADD "+colName+" "+sqlType);
        } else {
            executeSQL("ALTER TABLE "+tableName+" ADD COLUMN "+colName+" "+sqlType);
        }
    }

    public void dropSchema(){
        throw new RuntimeException("not implemented");
    }
}
