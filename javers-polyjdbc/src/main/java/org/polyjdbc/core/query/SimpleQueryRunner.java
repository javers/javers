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

import org.polyjdbc.core.query.mapper.ObjectMapper;

import java.util.List;
import java.util.Set;

/**
 * Runs simple query in one-time transaction, can be used multiple times, resources are always freed.
 * Internally uses {@link TransactionRunner}. Each time a query is made, new transaction is
 * opened, closed after query execution.
 *
 * @author Adam Dubiel
 */
public class SimpleQueryRunner {

    private final TransactionRunner runner;

    /**
     * Create new runner that will use given query runner factory to create query runners.
     */
    public SimpleQueryRunner(QueryRunnerFactory queryRunnerFactory) {
        runner = new TransactionRunner(queryRunnerFactory);
    }

    /**
     * Find unique entry, uses {@link QueryRunner#queryUnique(org.polyjdbc.core.query.SelectQuery, org.polyjdbc.core.query.mapper.ObjectMapper) }.
     */
    public <T> T queryUnique(final SelectQuery query, final ObjectMapper<T> mapper) {
        return runner.run(new TransactionWrapper<T>() {
            public T perform(QueryRunner queryRunner) {
                return queryRunner.queryUnique(query, mapper);
            }
        });
    }

    /**
     * Retrieve list, uses {@link QueryRunner#queryList(org.polyjdbc.core.query.SelectQuery, org.polyjdbc.core.query.mapper.ObjectMapper) }.
     */
    public <T> List<T> queryList(final SelectQuery query, final ObjectMapper<T> mapper) {
        return runner.run(new TransactionWrapper<List<T>>() {
            public List<T> perform(QueryRunner queryRunner) {
                return queryRunner.queryList(query, mapper);
            }
        });
    }

    /**
     * Retrieve set, uses {@link QueryRunner#querySet(org.polyjdbc.core.query.SelectQuery, org.polyjdbc.core.query.mapper.ObjectMapper) }.
     */
    public <T> Set<T> querySet(final SelectQuery query, final ObjectMapper<T> mapper) {
        return runner.run(new TransactionWrapper<Set<T>>() {
            public Set<T> perform(QueryRunner queryRunner) {
                return queryRunner.querySet(query, mapper);
            }
        });
    }

    /**
     * Check if any result exists for query, uses {@link QueryRunner#queryExistence(org.polyjdbc.core.query.SelectQuery) }.
     */
    public boolean queryExistence(final SelectQuery query) {
        return runner.run(new TransactionWrapper<Boolean>() {
            public Boolean perform(QueryRunner queryRunner) {
                return queryRunner.queryExistence(query);
            }
        });
    }

    /**
     * Run insert query, uses {@link QueryRunner#insert(org.polyjdbc.core.query.InsertQuery) }.
     */
    public long insert(final InsertQuery query) {
        return runner.run(new TransactionWrapper<Long>() {
            public Long perform(QueryRunner queryRunner) {
                return queryRunner.insert(query);
            }
        });
    }

    /**
     * Run update query, uses {@link QueryRunner#update(org.polyjdbc.core.query.UpdateQuery) }.
     */
    public int update(final UpdateQuery query) {
        return runner.run(new TransactionWrapper<Integer>() {
            public Integer perform(QueryRunner queryRunner) {
                return queryRunner.update(query);
            }
        });
    }
}
