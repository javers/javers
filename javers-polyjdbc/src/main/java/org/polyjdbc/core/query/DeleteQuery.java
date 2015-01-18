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

/**
 * Builds insert query, use {@link QueryFactory#delete() } to create new instance.
 *
 * <pre>
 * QueryFactory.delete().from("test").where("id > :maxId").withArgument("maxId", 1000);
 * </pre>
 *
 * @author Adam Dubiel
 */
public class DeleteQuery {

    private final Query query = new Query();

    DeleteQuery() {
    }

    Query build() {
        query.compile();
        return query;
    }

    /**
     * Creates <b>FROM</b> clause with given table name.
     */
    public DeleteQuery from(String tableName) {
        query.append("DELETE FROM ").append(tableName);
        return this;
    }

    /**
     * Adds <b>WHERE</b> clause with given conditions. Words prefixed with
     * <b>:</b> are replaced into value placeholders, which can be populated
     * using {@link #withArgument(java.lang.String, java.lang.Object) } method.
     * <pre>
     * .where("value > :value").withArgument("value", 10);
     * </pre>
     */
    public DeleteQuery where(String conditions) {
        query.append(" WHERE ").append(conditions);
        return this;
    }

    /**
     * Sets value for placeholder defined in query. Placeholder name should
     * not start with <b>:</b>, it is stripped off. Based on passed object type,
     * appropriate JDBC type is chosen.
     *
     * @see org.polyjdbc.core.type.ColumnType
     *
     */
    public DeleteQuery withArgument(String argumentName, Object object) {
        query.setArgument(argumentName, object);
        return this;
    }
}
