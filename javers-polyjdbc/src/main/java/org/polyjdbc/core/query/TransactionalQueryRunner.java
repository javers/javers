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
package org.polyjdbc.core.query;

import org.polyjdbc.core.exception.NonUniqueException;
import org.polyjdbc.core.exception.QueryExecutionException;
import org.polyjdbc.core.key.KeyGenerator;
import org.polyjdbc.core.query.mapper.EmptyMapper;
import org.polyjdbc.core.query.mapper.ObjectMapper;
import org.polyjdbc.core.transaction.Transaction;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 *
 * @author Adam Dubiel
 */
public class TransactionalQueryRunner implements QueryRunner {

    private static final EmptyMapper EMPTY_MAPPER = new EmptyMapper();

    private final Transaction transaction;

    private final KeyGenerator keyGenerator;

    public TransactionalQueryRunner(Transaction transaction, KeyGenerator keyGenerator) {
        this.transaction = transaction;
        this.keyGenerator = keyGenerator;
    }

    @Override
    public <T> T queryUnique(SelectQuery query, ObjectMapper<T> mapper) {
        return queryUnique(query, mapper, true);
    }

    @Override
    public <T> T queryUnique(SelectQuery query, ObjectMapper<T> mapper, boolean failOnNotUniqueOrNotFound) {
        // in case it is VERY non-unique item, do not fetch whole DB, 2 items is enough
        query.limit(2);

        Query rawQuery = query.build();
        List<T> results = queryCollection(rawQuery, mapper, new ArrayList<T>());

        if (results.size() != 1) {
            if (failOnNotUniqueOrNotFound) {
                transaction.rollback();
                if (results.isEmpty()) {
                    throw new NonUniqueException("NO_ITEM_FOUND", String.format("Asked for unique result but no items found for query:%n%s", rawQuery.getQuery()));
                } else {
                    throw new NonUniqueException("NON_UNIQUE_ITEM", String.format("Asked for unique result but %d items found for query:%n%s", results.size(), rawQuery.getQuery()));
                }
            } else {
                return null;
            }
        }
        return results.get(0);
    }

    @Override
    public <T> List<T> queryList(SelectQuery query, ObjectMapper<T> mapper) {
        return queryCollection(query.build(), mapper, new ArrayList<T>());
    }

    @Override
    public <T> Set<T> querySet(SelectQuery query, ObjectMapper<T> mapper) {
        return queryCollection(query.build(), mapper, new HashSet<T>());
    }

    private <T, C extends Collection<T>> C queryCollection(Query query, ObjectMapper<T> mapper, C collection) {
        try {
            PreparedStatement statement = query.createStatementWithValues(transaction);
            ResultSet resultSet = transaction.executeQuery(statement);

            while (resultSet.next()) {
                collection.add(mapper.createObject(resultSet));
            }
            return collection;
        } catch (SQLException exception) {
            transaction.rollback();
            throw new QueryExecutionException("SELECT_ERROR", String.format("Failed to run select query:%n%s", query.getQuery()), exception);
        }
    }

    @Override
    public boolean queryExistence(SelectQuery query) {
        return !queryList(query, EMPTY_MAPPER).isEmpty();
    }

    @Override
    public long insert(InsertQuery insertQuery) {
        try {
            boolean useSequence = insertQuery.sequenceSet();

            if (useSequence) {
                long key = keyGenerator.generateKey(insertQuery.getSequenceName(), transaction);
                insertQuery.sequenceValue(key);
            }

            Query rawQuery = insertQuery.build();
            PreparedStatement statement = rawQuery.createStatementWithValues(transaction);
            transaction.executeUpdate(statement);

            return useSequence ? keyGenerator.getKeyFromLastInsert(transaction) : 0;
        } catch (SQLException exception) {
            transaction.rollback();
            Query rawQuery = insertQuery.build();
            throw new QueryExecutionException("INSERT_ERROR", String.format("Failed to run insert query:%n%s", rawQuery), exception);
        }
    }

    @Override
    public int update(UpdateQuery updateQuery) {
        Query rawQuery = updateQuery.build();
        try {
            PreparedStatement statement = rawQuery.createStatementWithValues(transaction);
            return transaction.executeUpdate(statement);
        } catch (SQLException exception) {
            transaction.rollback();
            throw new QueryExecutionException("UPDATE_ERROR", String.format("Failed to run update query:%n%s", rawQuery.getQuery()), exception);
        }
    }

    @Override
    public int delete(DeleteQuery deleteQuery) {
        Query rawQuery = deleteQuery.build();
        try {
            PreparedStatement statement = rawQuery.createStatementWithValues(transaction);
            return transaction.executeUpdate(statement);
        } catch (SQLException exception) {
            transaction.rollback();
            throw new QueryExecutionException("DELETE_ERROR", String.format("Failed to run delete query:%n%s", rawQuery.getQuery()), exception);
        }
    }

    @Override
    public void commit() {
        transaction.commit();
    }

    @Override
    public void rollback() {
        transaction.rollback();
    }

    @Override
    public void rollbackAndClose() {
        rollback();
        transaction.close();
    }

    @Override
    public void commitAndClose() {
        commit();
        transaction.close();
    }

    @Override
    public void close() {
        transaction.close();
    }
}
