package org.javers.repository.sql.session;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ObjectMapper<T> {
    T get(ResultSet resultSet) throws SQLException;
}
