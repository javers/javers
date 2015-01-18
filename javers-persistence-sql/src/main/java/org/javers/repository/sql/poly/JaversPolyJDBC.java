package org.javers.repository.sql.poly;

import org.javers.repository.sql.ConnectionProvider;
import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.query.*;
import org.polyjdbc.core.schema.SchemaInspector;
import org.polyjdbc.core.schema.SchemaManager;
import org.polyjdbc.core.schema.SchemaManagerFactory;
import org.polyjdbc.core.transaction.TransactionManager;
import org.polyjdbc.core.util.TheCloser;

import java.io.Closeable;

/**
 * @author bartosz walacik
 */
public class JaversPolyJDBC {

    private final Dialect dialect;

    private final DialectQueryFactory queryFactory;

    private final TransactionManager transactionManager;

    private final QueryRunnerFactory queryRunnerFactory;

    private final SimpleQueryRunner simpleQueryRunner;

    private final TransactionRunner transactionRunner;

    private final SchemaManagerFactory schemaManagerFactory;

    public JaversPolyJDBC(ConnectionProvider connectionProvider, Dialect dialect) {
        this.dialect = dialect;
        this.queryFactory = new DialectQueryFactory(dialect);
        this.transactionManager = new ProvidedConnectionTransactionManager(connectionProvider);
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
