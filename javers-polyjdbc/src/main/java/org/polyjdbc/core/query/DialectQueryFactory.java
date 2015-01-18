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
package org.polyjdbc.core.query;

import org.polyjdbc.core.dialect.Dialect;

/**
 *
 * @author Adam Dubiel
 */
public class DialectQueryFactory {

    private final Dialect dialect;

    public DialectQueryFactory(Dialect dialect) {
        this.dialect = dialect;
    }

    /**
     * Create insert query.
     */
    public InsertQuery insert() {
        return new InsertQuery();
    }

    /**
     * Create select query, specifying the <b>select</b> clause.
     * <pre>QueryFactory.select("columnA, columnB");</pre>
     */
    public SelectQuery select(String what) {
        return new SelectQuery(dialect, what);
    }

    /**
     * Create select query which selects all columns, equivalent to
     * <code>QueryFactory.select("*")</code>.
     */
    public SelectQuery selectAll() {
        return new SelectQuery(dialect, "*");
    }

    /**
     * Create empty select statement, can only be used with
     * {@link SelectQuery#query(java.lang.String)} and must contain all clauses
     * except from order by and limit.
     * <pre>
     * QueryFactory.select().query("select * from test where column > :column").orderBy("column", Order.ASC)
     * .withArgument("column", 2);
     * </pre>
     */
    public SelectQuery select() {
        return new SelectQuery(dialect);
    }

    /**
     * Create update query on given table.
     */
    public UpdateQuery update(String what) {
        return new UpdateQuery(what);
    }

    /**
     * Create delete query.
     */
    public DeleteQuery delete() {
        return new DeleteQuery();
    }
}
