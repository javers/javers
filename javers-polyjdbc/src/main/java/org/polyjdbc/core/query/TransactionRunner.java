/*
 * Copyright 2013 Adam Dubiel.
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

import org.polyjdbc.core.exception.TransactionInterruptedException;
import org.polyjdbc.core.util.TheCloser;

/**
 * Run any number of operations in single transaction without the need to wrap code in try-finally block as TransactionRunner takes care of
 * resource freeing even when exception is thrown. TransactionRunner is reusable, creates transaction per {@link #run(org.polyjdbc.core.query.TransactionWrapper)
 * } method usage.
 *
 * <pre>
 * Test test = transactionRunner.run(new TransactionWrapper<Test>() {
 *   @Override
 *   public Test perform(QueryRunner queryRunner) {
 *       SelectQuery query = QueryFactory.select().query("select * from test where name = :name")
 *           .withArgument("name", "test");
 *       return queryRunner.queryUnique(query, new TestMapper());
 *   }
 * });
 * </pre>
 *
 * @see TransactionWrapper
 * @see VoidTransactionWrapper
 *
 * @author Adam Dubiel
 */
public class TransactionRunner {

    private final QueryRunnerFactory queryRunnerFactory;

    public TransactionRunner(QueryRunnerFactory queryRunnerFactory) {
        this.queryRunnerFactory = queryRunnerFactory;
    }

    /**
     * Run specified operations in safe transaction block.
     */
    public <T> T run(TransactionWrapper<T> operation) {
        QueryRunner runner = null;
        try {
            runner = queryRunnerFactory.create();
            T result = operation.perform(runner);
            runner.commit();

            return result;
        } catch (Throwable throwable) {
            TheCloser.rollback(runner);
            throw new TransactionInterruptedException(throwable);
        } finally {
            TheCloser.close(runner);
        }
    }
}
