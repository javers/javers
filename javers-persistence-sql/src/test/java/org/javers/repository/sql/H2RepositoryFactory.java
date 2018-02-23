package org.javers.repository.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author bartosz.walacik
 */
public class H2RepositoryFactory {
    public static JaversSqlRepository create() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:mem:test;");

            return SqlRepositoryBuilder.sqlRepository().
                            withConnectionProvider(new ConnectionProvider() {
                                public Connection getConnection() throws SQLException {
                                    return DriverManager.getConnection("jdbc:h2:mem:test;");
                                }
                            }).
                            withDialect(DialectName.H2).
                    build();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
