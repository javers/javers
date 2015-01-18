/*
 * Copyright 2013 Adam Dubiel, Przemek Hertel.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.polyjdbc.core.transaction;

import org.polyjdbc.core.exception.PolyJdbcException;

import java.io.Closeable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Adam Dubiel
 */
public class Transaction implements Closeable {

    private final Connection connection;

    private final List<Statement> statements = new ArrayList<Statement>();

    private final List<ResultSet> resultSets = new ArrayList<ResultSet>();

    public Transaction(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public int executeUpdate(PreparedStatement preparedStatement) throws SQLException {
        try {
            registerStatement(preparedStatement);
            return preparedStatement.executeUpdate();
        } catch (SQLException exception) {
            rollback();
            throw exception;
        }
    }

    public boolean execute(PreparedStatement preparedStatement) throws SQLException {
        try {
            registerStatement(preparedStatement);
            return preparedStatement.execute();
        } catch (SQLException exception) {
            rollback();
            throw exception;
        }
    }

    public ResultSet executeQuery(PreparedStatement preparedStatement) throws SQLException {
        try {
            registerStatement(preparedStatement);
            ResultSet resultSet = preparedStatement.executeQuery();
            registerCursor(resultSet);
            return resultSet;
        } catch (SQLException exception) {
            rollback();
            throw exception;
        }
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
        try {
            return connection.prepareStatement(query);
        } catch (SQLException exception) {
            rollback();
            throw exception;
        }
    }

    public Statement createStatement() throws SQLException {
        try {
            Statement statement = connection.createStatement();
            registerStatement(statement);
            return statement;
        } catch (SQLException exception) {
            rollback();
            throw exception;
        }
    }

    public void registerStatement(Statement statement) {
        statements.add(0, statement);
    }

    public void registerCursor(ResultSet resultSet) {
        resultSets.add(0, resultSet);
    }

    public void commit() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.commit();
            }
        } catch (SQLException exception) {
            throw new PolyJdbcException("TRANSACTION_COMMIT_ERROR", "Failed to commit transaction transaction.", exception);
        }
    }

    public void rollback() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
        } catch (SQLException exception) {
            throw new PolyJdbcException("TRANSACTION_ROLLBACK_ERROR", "Failed to rollback transaction.", exception);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && connection.isClosed()) {
                throw new PolyJdbcException("CLOSING_CLOSED_CONNECTION", "Tried to close already closed connection! Check for some unwanted close() in your code.");
            }

            for (ResultSet resultSet : resultSets) {
                resultSet.close();
            }
            for (Statement statement : statements) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException exception) {
            throw new PolyJdbcException("TRANSACTION_CLOSE_ERROR", "Failed to close transaction.", exception);
        }
    }
}
