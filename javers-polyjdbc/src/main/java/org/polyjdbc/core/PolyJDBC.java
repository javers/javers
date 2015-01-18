/*
 * Copyright 2014 Adam Dubiel.
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
package org.polyjdbc.core;

import java.io.Closeable;
import javax.sql.DataSource;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.query.DialectQueryFactory;
import org.polyjdbc.core.query.QueryRunner;
import org.polyjdbc.core.query.QueryRunnerFactory;
import org.polyjdbc.core.query.SimpleQueryRunner;
import org.polyjdbc.core.query.TransactionRunner;
import org.polyjdbc.core.schema.SchemaInspector;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.SchemaManagerFactory;
import org.polyjdbc.core.transaction.DataSourceTransactionManager;
import org.polyjdbc.core.transaction.TransactionManager;
import org.polyjdbc.core.util.TheCloser;

/**
 *
 * @author Adam Dubiel
 */
public class PolyJDBC {

    private final Dialect dialect;
    
    private final DialectQueryFactory queryFactory;

    private final TransactionManager transactionManager;

    private final QueryRunnerFactory queryRunnerFactory;

    private final SimpleQueryRunner simpleQueryRunner;

    private final TransactionRunner transactionRunner;

    private final SchemaManagerFactory schemaManagerFactory;

    public PolyJDBC(DataSource dataSource, Dialect dialect) {
        this.dialect = dialect;
        this.queryFactory = new DialectQueryFactory(dialect);
        this.transactionManager = new DataSourceTransactionManager(dataSource);
        this.queryRunnerFactory = new QueryRunnerFactory(dialect, transactionManager);
        this.simpleQueryRunner = new SimpleQueryRunner(queryRunnerFactory);
        this.transactionRunner = new TransactionRunner(queryRunnerFactory);
        this.schemaManagerFactory = new SchemaManagerFactory(transactionManager);
    }

    public Dialect dialect() {
        return dialect;
    }
    
    public DialectQueryFactory query() {
        return queryFactory;
    }

    public QueryRunner queryRunner() {
        return queryRunnerFactory.create();
    }

    public SimpleQueryRunner simpleQueryRunner() {
        return simpleQueryRunner;
    }

    public TransactionRunner transactionRunner() {
        return transactionRunner;
    }

    public SchemaManager schemaManager() {
        return schemaManagerFactory.createManager();
    }

    public SchemaInspector schemaInspector() {
        return schemaManagerFactory.createInspector();
    }

    public void rollback(QueryRunner... toRollback) {
        TheCloser.rollback(toRollback);
    }
    
    public void close(Closeable... toClose) {
        TheCloser.close(toClose);
    }
}
