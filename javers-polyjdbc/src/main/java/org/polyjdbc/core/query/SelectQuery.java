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

import org.polyjdbc.core.dialect.Dialect;
import org.polyjdbc.core.util.StringBuilderUtil;

/**
 * Builds select query, use {@link QueryFactory } to create new instance.
 * DSL method should be called according to order of clauses in query. Otherwise
 * query will be malformed.
 *
 * <pre>
 * QueryFactory.selectAll().from("test").where("id > :maxId").withArgument("maxId", 1000);
 * QueryFactory.select().query("select * from test where id > :maxId").withArgument("maxId", 1000);
 * </pre>
 *
 * @author Adam Dubiel
 */
public class SelectQuery {

    private static final int ORDER_BY_LENGTH = 20;

    private final Dialect dialect;
    
    private final Query query;

    private StringBuilder orderBy;

    private String limit;

    SelectQuery(Dialect dialect, String what) {
        this(dialect);
        this.query.append("SELECT ").append(what).append(" ");
    }

    SelectQuery(Dialect dialect) {
        this.dialect = dialect;
        this.query = new Query();
    }

    Query build() {
        if (orderBy != null) {
            StringBuilderUtil.deleteLastCharacters(orderBy, 2);
            query.append(orderBy.toString());
        }
        if (limit != null) {
            query.append(limit);
        }

        query.compile();
        return query;
    }

    /**
     * Fill <b>FROM</b> clause.
     */
    public SelectQuery from(String from) {
        query.append("FROM ").append(from).append(" ");
        return this;
    }

    /**
     * Fill <b>WHERE</b> clause.
     */
    public SelectQuery where(String where) {
        query.append("WHERE ").append(where).append(" ");
        return this;
    }

    /**
     * Replaces contents of select query with given query string. Query has to
     * contain all clauses - nothing is generated. Only {@link Select#orderBy(java.lang.String, java.lang.String)} and
     * {@link SelectQuery#limit(int, int)} can be used with this method.
     */
    public SelectQuery query(String queryText) {
        query.overwrite(queryText);
        return this;
    }

    /**
     * Append any text to query, creation method does not matter. Useful when
     * additional <b>WHERE</b> clause conditions are needed after initial creation.
     */
    public SelectQuery append(String queryText) {
        query.append(queryText);
        return this;
    }

    /**
     * Create <b>ORDER BY</b> clause for given column, can be used multiple
     * times with multiple columns.
     */
    public SelectQuery orderBy(String name, Order order) {
        if (orderBy == null) {
            orderBy = new StringBuilder(ORDER_BY_LENGTH);
            orderBy.append(" ORDER BY ");
        }
        orderBy.append(name).append(" ").append(order.getStringCode()).append(", ");

        return this;
    }

    /**
     * Create <b>LIMIT</b> clause.
     */
    public SelectQuery limit(int limit, int offset) {
        this.limit = " LIMIT " + limit + " OFFSET " + offset;
        return this;
    }

    /**
     * Create <b>LIMIT</b> clause without specifying offset.
     */
    public SelectQuery limit(int limit) {
        return limit(limit, 0);
    }

    /**
     * Sets value for placeholder defined in query. Placeholder name should
     * not start with <b>:</b>, it is stripped off. Based on passed object type,
     * appropriate JDBC type is chosen.
     *
     * @see org.polyjdbc.core.type.ColumnType
     *
     */
    public SelectQuery withArgument(String argumentName, Object object) {
        query.setArgument(argumentName, object);
        return this;
    }
}
