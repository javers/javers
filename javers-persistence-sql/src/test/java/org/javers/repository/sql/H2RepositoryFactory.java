package org.javers.repository.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author bartosz.walacik
 */
public class H2RepositoryFactory {
    public static JaversSqlRepository create(boolean schemaManagement) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;");

            return SqlRepositoryBuilder.sqlRepository().
                            withConnectionProvider(new ConnectionProvider() {
                                public Connection getConnection() throws SQLException {
                                    return DriverManager.getConnection("jdbc:h2:mem:test;");
                                }
                            }).
                            withDialect(DialectName.H2).
                            withSchemaManagementEnabled(schemaManagement).
                    build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static JaversSqlRepository emptySchema(boolean schemaManagement) {
        try {
            final Connection conn = DriverManager.getConnection("jdbc:h2:mem:empty;");

            return SqlRepositoryBuilder.sqlRepository().
                    withConnectionProvider(new ConnectionProvider() {
                        public Connection getConnection() throws SQLException {
                            return conn;
                        }
                    }).
                    withDialect(DialectName.H2).
                    withSchemaManagementEnabled(schemaManagement).
                    build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
