package org.javers.repository.sql;

import java.sql.Connection;

public interface ConnectionProvider {
    
    Connection getConnection();
}
