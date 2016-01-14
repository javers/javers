package org.javers.repository.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Implementation should provide working JDBC connection,
 * exactly the same which is used by user's application in the current thread.
 * <br/>
 *
 * Usually, connections come from some thread-safe connection pool.
 */
public interface ConnectionProvider {
    
    Connection getConnection() throws SQLException;
}
